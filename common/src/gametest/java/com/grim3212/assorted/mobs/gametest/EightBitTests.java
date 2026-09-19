package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.common.entity.Bobomb;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.entity.Parabuzzy;
import com.grim3212.assorted.mobs.common.entity.Perch;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.survivalPlayer;
import static com.grim3212.assorted.lib.test.TestSupport.useOnTopOf;
import static com.grim3212.assorted.mobs.gametest.MobsTestSupport.*;

/** The Bob-omb and the parabuzzy, and the perch on a player's head that both of them use. */
final class EightBitTests {

    private EightBitTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("bobomb_is_lit_not_hurt_by_a_player", EightBitTests::bobombIsLitNotHurtByAPlayer);
        out.accept("bobomb_defends_a_player_from_a_monster", EightBitTests::bobombDefendsAPlayerFromAMonster);
        out.accept("bobomb_leaves_monsters_alone", EightBitTests::bobombLeavesMonstersAlone);
        out.accept("bobomb_belongs_to_whoever_puts_it_down", EightBitTests::bobombBelongsToWhoeverPutsItDown);
        out.accept("owned_bobomb_only_defends_its_owner", EightBitTests::ownedBobombOnlyDefendsItsOwner);
        out.accept("bobomb_is_lit_not_hurt_by_a_monster", EightBitTests::bobombIsLitNotHurtByAMonster);
        out.accept("mobs_perch_on_a_players_head", EightBitTests::mobsPerchOnAPlayersHead);
        out.accept("sneaking_on_the_ground_puts_a_perched_mob_down", EightBitTests::sneakingOnTheGroundPutsAPerchedMobDown);
        out.accept("bobomb_is_picked_up_with_an_empty_hand", EightBitTests::bobombIsPickedUpWithAnEmptyHand);
        out.accept("parabuzzy_colours_set_their_stats", EightBitTests::parabuzzyColoursSetTheirStats);
        out.accept("only_blue_parabuzzies_drop_a_shell", EightBitTests::onlyBlueParabuzziesDropAShell);
    }

    /**
     * A monster that has hurt a player is walked up to, the fuse is lit beside it, and it goes off. The
     * player it follows is out of its reach overhead, where following would keep it busy for good:
     * defending has to win.
     */
    private static void bobombDefendsAPlayerFromAMonster(GameTestHelper helper) {
        ServerPlayer player = playerOverhead(helper);
        Bobomb bobomb = helper.spawn(MobsEntities.BOBOMB.get(), CENTRE);
        Husk husk = helper.spawnWithNoFreeWill(EntityTypes.HUSK, CENTRE.south(3));
        player.setLastHurtByMob(husk);

        helper.succeedWhen(() -> helper.assertTrue(bobomb.isRemoved(), "the Bob-omb never went off: its target " + bobomb.getTarget() + ", lit " + bobomb.isLit()));
    }

    /** A monster that has hurt no one is not the Bob-omb's business. */
    private static void bobombLeavesMonstersAlone(GameTestHelper helper) {
        playerOverhead(helper);
        Bobomb bobomb = helper.spawn(MobsEntities.BOBOMB.get(), CENTRE);
        helper.spawnWithNoFreeWill(EntityTypes.HUSK, CENTRE.south(3));

        helper.runAfterDelay(60, () -> {
            helper.assertTrue(bobomb.getTarget() == null, "the Bob-omb went after a monster that hurt no one: " + bobomb.getTarget());
            helper.assertFalse(bobomb.isLit(), "the Bob-omb lit its fuse with no one to defend");
            bobomb.discard();
            helper.succeed();
        });
    }

    private static void bobombBelongsToWhoeverPutsItDown(GameTestHelper helper) {
        ServerPlayer player = standingPlayer(helper, CENTRE);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(MobsItems.BOBOMB.get()));
        useOnTopOf(helper, player, CENTRE.east().below());

        Bobomb bobomb = helper.findOneEntity(MobsEntities.BOBOMB.get());
        helper.assertTrue(bobomb.getOwner() == player, "the Bob-omb's owner is " + bobomb.getOwner() + ", not the player who put it down");
        EntityReference<LivingEntity> reloaded = afterReload(helper, bobomb, MobsEntities.BOBOMB.get()).getOwnerReference();
        helper.assertTrue(reloaded != null && reloaded.matches(player), "the Bob-omb's owner did not survive a reload: " + reloaded);

        bobomb.discard();
        helper.succeed();
    }

    /**
     * Another player's attacker is left alone, while its owner's is not. Both players are out of reach
     * overhead, as in {@link #bobombDefendsAPlayerFromAMonster}.
     */
    private static void ownedBobombOnlyDefendsItsOwner(GameTestHelper helper) {
        ServerPlayer owner = playerOverhead(helper);
        ServerPlayer stranger = playerOverhead(helper);
        Bobomb bobomb = helper.spawn(MobsEntities.BOBOMB.get(), CENTRE);
        bobomb.setOwner(owner);
        Husk husk = helper.spawnWithNoFreeWill(EntityTypes.HUSK, CENTRE.south(3));
        stranger.setLastHurtByMob(husk);

        boolean[] ownerHurt = {false};
        helper.runAfterDelay(60, () -> {
            helper.assertTrue(bobomb.getTarget() == null, "the Bob-omb defended a player who does not own it");
            helper.assertFalse(bobomb.isLit(), "the Bob-omb lit its fuse for a player who does not own it");
            owner.setLastHurtByMob(husk);
            ownerHurt[0] = true;
        });
        helper.succeedWhen(() -> {
            helper.assertTrue(ownerHurt[0], "the Bob-omb went off before its owner was hurt");
            helper.assertTrue(bobomb.isRemoved(), "the Bob-omb never went off for its owner: its target " + bobomb.getTarget() + ", lit " + bobomb.isLit());
        });
    }

    /** A monster's hit lights the fuse like a player's does, rather than setting it off there and then. */
    private static void bobombIsLitNotHurtByAMonster(GameTestHelper helper) {
        Bobomb bobomb = helper.spawnWithNoFreeWill(MobsEntities.BOBOMB.get(), CENTRE);
        Husk husk = helper.spawnWithNoFreeWill(EntityTypes.HUSK, CENTRE.south(3));

        bobomb.hurtServer(helper.getLevel(), bobomb.damageSources().mobAttack(husk), 4.0F);
        helper.assertFalse(bobomb.isRemoved(), "a monster's hit set the Bob-omb off at once");
        helper.assertTrue(bobomb.isLit(), "a monster's hit did not light the Bob-omb");
        helper.assertValueEqual(bobomb.getHealth(), bobomb.getMaxHealth(), "Bob-omb health after a monster's hit");

        // Not left to go off inside the test grid.
        bobomb.discard();
        helper.succeed();
    }

    /** A player hanging out of reach over the box, so following them never gets anywhere. */
    private static ServerPlayer playerOverhead(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        Vec3 above = helper.absoluteVec(new Vec3(4.5D, 11.0D, 4.5D));
        player.snapTo(above.x, above.y, above.z);
        player.setNoGravity(true);
        player.setInvulnerable(true);
        return player;
    }

    private static void bobombIsLitNotHurtByAPlayer(GameTestHelper helper) {
        Bobomb bobomb = helper.spawnWithNoFreeWill(MobsEntities.BOBOMB.get(), CENTRE);
        ServerPlayer player = survivalPlayer(helper);

        bobomb.hurtServer(helper.getLevel(), bobomb.damageSources().playerAttack(player), 4.0F);
        helper.assertTrue(bobomb.isLit(), "a player's hit did not light the Bob-omb");
        helper.assertValueEqual(bobomb.getHealth(), bobomb.getMaxHealth(), "Bob-omb health after a player's hit");

        // Not left to go off inside the test grid.
        bobomb.discard();
        helper.succeed();
    }

    private static void mobsPerchOnAPlayersHead(GameTestHelper helper) {
        ServerPlayer player = standingPlayer(helper, CENTRE);
        Bobomb bobomb = helper.spawnWithNoFreeWill(MobsEntities.BOBOMB.get(), CENTRE.east());
        Parabuzzy parabuzzy = helper.spawnWithNoFreeWill(MobsEntities.PARABUZZY.get(), CENTRE.west());
        parabuzzy.tame(player);

        bobomb.interact(player, InteractionHand.MAIN_HAND, bobomb.position());
        helper.assertTrue(bobomb.perch().isOn(player), "using a Bob-omb did not put it on the player's head");
        helper.assertTrue(Perch.isCarrying(player), "the player is not counted as carrying the Bob-omb");

        parabuzzy.interact(player, InteractionHand.MAIN_HAND, parabuzzy.position());
        helper.assertFalse(parabuzzy.perch().isPerched(), "a second creature climbed onto a head already carrying one");

        helper.runAfterDelay(2, () -> {
            helper.assertValueEqual(bobomb.getY(), player.getY() + player.getBbHeight(), "height of a perched Bob-omb");
            helper.assertValueEqual(bobomb.getX(), player.getX(), "x of a perched Bob-omb");
            bobomb.discard();
            helper.succeed();
        });
    }

    private static void sneakingOnTheGroundPutsAPerchedMobDown(GameTestHelper helper) {
        ServerPlayer player = standingPlayer(helper, CENTRE);
        Parabuzzy parabuzzy = helper.spawnWithNoFreeWill(MobsEntities.PARABUZZY.get(), CENTRE.west());
        parabuzzy.tame(player);

        parabuzzy.interact(player, InteractionHand.MAIN_HAND, parabuzzy.position());
        helper.assertTrue(parabuzzy.perch().isOn(player), "using a tame parabuzzy did not put it on the player's head");

        player.setShiftKeyDown(true);
        helper.succeedWhen(() -> {
            helper.assertFalse(parabuzzy.perch().isPerched(), "the parabuzzy stayed on a player sneaking on the ground");
            helper.assertValueEqual(parabuzzy.getY(), player.getY(), "height of a parabuzzy put down");
        });
    }

    private static void bobombIsPickedUpWithAnEmptyHand(GameTestHelper helper) {
        ServerPlayer player = standingPlayer(helper, CENTRE);
        Bobomb bobomb = helper.spawnWithNoFreeWill(MobsEntities.BOBOMB.get(), CENTRE.east());

        player.setShiftKeyDown(true);
        bobomb.interact(player, InteractionHand.MAIN_HAND, bobomb.position());
        helper.assertTrue(bobomb.isRemoved(), "the Bob-omb was not picked up");
        helper.succeedWhen(() -> helper.assertItemEntityPresent(MobsItems.BOBOMB.get(), CENTRE.east(), 2.0D));
    }

    private static void parabuzzyColoursSetTheirStats(GameTestHelper helper) {
        Parabuzzy parabuzzy = helper.spawnWithNoFreeWill(MobsEntities.PARABUZZY.get(), CENTRE);
        parabuzzy.setVariant(Parabuzzy.Variant.RED_SPIKED);
        helper.assertValueEqual(parabuzzy.getMaxHealth(), (float) Parabuzzy.Variant.RED_SPIKED.health, "red spiked parabuzzy health");
        helper.assertValueEqual(parabuzzy.getAttributeValue(Attributes.ATTACK_DAMAGE), Parabuzzy.Variant.RED_SPIKED.damage, "red spiked parabuzzy damage");

        Parabuzzy reloaded = afterReload(helper, parabuzzy, MobsEntities.PARABUZZY.get());
        helper.assertValueEqual(reloaded.getVariant(), Parabuzzy.Variant.RED_SPIKED, "parabuzzy colour after a reload");
        helper.assertValueEqual(reloaded.getMaxHealth(), (float) Parabuzzy.Variant.RED_SPIKED.health, "red spiked parabuzzy health after a reload");
        helper.succeed();
    }

    private static void onlyBlueParabuzziesDropAShell(GameTestHelper helper) {
        BlockPos bluePos = new BlockPos(1, 1, 1);
        BlockPos redPos = new BlockPos(7, 1, 7);
        Parabuzzy blue = helper.spawnWithNoFreeWill(MobsEntities.PARABUZZY.get(), bluePos);
        Parabuzzy red = helper.spawnWithNoFreeWill(MobsEntities.PARABUZZY.get(), redPos);
        blue.setVariant(Parabuzzy.Variant.BLUE);
        red.setVariant(Parabuzzy.Variant.RED);

        blue.kill(helper.getLevel());
        red.kill(helper.getLevel());

        helper.succeedWhen(() -> {
            helper.assertItemEntityPresent(MobsItems.PARABUZZY_SHELL.get(), bluePos, 2.0D);
            helper.assertItemEntityNotPresent(MobsItems.PARABUZZY_SHELL.get(), redPos, 2.0D);
        });
    }

    /** A survival player on the floor of the box at {@code rel}, not moving. */
    private static ServerPlayer standingPlayer(GameTestHelper helper, BlockPos rel) {
        ServerPlayer player = survivalPlayer(helper);
        player.snapTo(helper.absoluteVec(Vec3.atBottomCenterOf(rel)));
        player.setOnGround(true);
        return player;
    }
}
