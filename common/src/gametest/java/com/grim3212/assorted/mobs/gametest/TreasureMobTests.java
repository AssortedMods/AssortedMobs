package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.entity.TreasureChest;
import com.grim3212.assorted.mobs.common.entity.TreasureMob;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.survivalPlayer;
import static com.grim3212.assorted.mobs.gametest.MobsTestSupport.*;

/** The treasure mob's chest: filled on arrival, kept through a save, dropped on death. */
final class TreasureMobTests {

    private TreasureMobTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("treasure_mob_arrives_with_loot", TreasureMobTests::treasureMobArrivesWithLoot);
        out.accept("treasure_mob_drops_its_chest", TreasureMobTests::treasureMobDropsItsChest);
        out.accept("treasure_mob_keeps_its_chest_through_a_reload", TreasureMobTests::treasureMobKeepsItsChestThroughAReload);
        out.accept("tame_treasure_mob_opens_for_its_owner", TreasureMobTests::tameTreasureMobOpensForItsOwner);
        out.accept("treasure_mob_is_tamed_with_gold_nuggets", TreasureMobTests::treasureMobIsTamedWithGoldNuggets);
        out.accept("treasure_mob_stands_still_while_open", TreasureMobTests::treasureMobStandsStillWhileOpen);
    }

    private static void treasureMobArrivesWithLoot(GameTestHelper helper) {
        TreasureMob mob = helper.spawn(MobsEntities.TREASURE_MOB.get(), CENTRE, EntitySpawnReason.NATURAL);
        helper.assertFalse(mob.getChest().isEmpty(), "a treasure mob arrived with an empty chest");
        helper.succeed();
    }

    private static void treasureMobDropsItsChest(GameTestHelper helper) {
        TreasureMob mob = helper.spawnWithNoFreeWill(MobsEntities.TREASURE_MOB.get(), CENTRE);
        mob.getChest().clearContent();
        mob.getChest().setItem(13, new ItemStack(Items.DIAMOND, 3));

        mob.kill(helper.getLevel());

        helper.succeedWhen(() -> helper.assertItemEntityPresent(Items.DIAMOND, CENTRE, 2.0D));
    }

    private static void treasureMobKeepsItsChestThroughAReload(GameTestHelper helper) {
        TreasureMob mob = helper.spawnWithNoFreeWill(MobsEntities.TREASURE_MOB.get(), CENTRE);
        mob.getChest().clearContent();
        mob.getChest().setItem(0, new ItemStack(Items.EMERALD, 5));
        mob.getChest().setItem(TreasureChest.SIZE - 1, new ItemStack(Items.BREAD));

        TreasureMob reloaded = afterReload(helper, mob, MobsEntities.TREASURE_MOB.get());
        helper.assertTrue(ItemStack.matches(reloaded.getChest().getItem(0), new ItemStack(Items.EMERALD, 5)), "the first slot came back as " + reloaded.getChest().getItem(0));
        helper.assertTrue(ItemStack.matches(reloaded.getChest().getItem(TreasureChest.SIZE - 1), new ItemStack(Items.BREAD)), "the last slot came back as " + reloaded.getChest().getItem(TreasureChest.SIZE - 1));
        helper.succeed();
    }

    /**
     * A player stands still with nuggets and feeds the treasure mob once it comes close, turning to
     * face it as a player aiming at it would. Each nugget tames it one time in three.
     */
    private static void treasureMobIsTamedWithGoldNuggets(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper, new ItemStack(Items.GOLD_NUGGET, 64));
        player.snapTo(helper.absoluteVec(Vec3.atBottomCenterOf(CENTRE.west(3))));
        player.setOnGround(true);
        TreasureMob mob = helper.spawn(MobsEntities.TREASURE_MOB.get(), CENTRE.east(3));

        helper.onEachTick(() -> {
            if (!mob.isTame() && helper.getTick() % 5 == 0) {
                player.setYRot(player.getYRot() + 10.0F);
                if (player.distanceToSqr(mob) < 9.0D) {
                    mob.interact(player, InteractionHand.MAIN_HAND, mob.position());
                }
            }
        });
        helper.succeedWhen(() -> helper.assertTrue(mob.isOwnedBy(player), "the treasure mob was never tamed: " + (64 - player.getMainHandItem().getCount()) + " nuggets fed"));
    }

    /** Opened while walking somewhere, it stops where it is until the chest is closed again. */
    private static void treasureMobStandsStillWhileOpen(GameTestHelper helper) {
        TreasureMob mob = helper.spawn(MobsEntities.TREASURE_MOB.get(), CENTRE);
        ServerPlayer owner = survivalPlayer(helper);
        owner.snapTo(helper.absoluteVec(Vec3.atBottomCenterOf(CENTRE.north())));
        owner.setOnGround(true);
        mob.tame(owner);

        BlockPos corner = helper.absolutePos(new BlockPos(7, 1, 7));
        mob.getNavigation().moveTo(corner.getX() + 0.5D, corner.getY(), corner.getZ() + 0.5D, 1.0D);
        owner.setShiftKeyDown(true);
        mob.interact(owner, InteractionHand.MAIN_HAND, mob.position());
        helper.assertTrue(mob.getChest().isOpen(), "sneaking and using the treasure mob did not open its chest");

        Vec3[] openedAt = new Vec3[1];
        helper.runAfterDelay(5, () -> openedAt[0] = mob.position());
        helper.runAfterDelay(45, () -> {
            helper.assertTrue(mob.position().distanceTo(openedAt[0]) < 0.05D, "the treasure mob moved from " + openedAt[0] + " to " + mob.position() + " with its chest open");
            owner.closeContainer();
            helper.assertFalse(mob.getChest().isOpen(), "closing the menu left the chest counted as open");
            helper.succeed();
        });
    }

    private static void tameTreasureMobOpensForItsOwner(GameTestHelper helper) {
        TreasureMob mob = helper.spawnWithNoFreeWill(MobsEntities.TREASURE_MOB.get(), CENTRE);
        ServerPlayer owner = survivalPlayer(helper);
        ServerPlayer stranger = survivalPlayer(helper);
        owner.snapTo(helper.absoluteVec(Vec3.atBottomCenterOf(CENTRE.east())));
        stranger.snapTo(helper.absoluteVec(Vec3.atBottomCenterOf(CENTRE.west())));
        mob.tame(owner);

        stranger.setShiftKeyDown(true);
        mob.interact(stranger, InteractionHand.MAIN_HAND, mob.position());
        helper.assertFalse(stranger.containerMenu instanceof ChestMenu, "a treasure mob opened for a player who does not own it");

        owner.setShiftKeyDown(true);
        mob.interact(owner, InteractionHand.MAIN_HAND, mob.position());
        helper.assertTrue(owner.containerMenu instanceof ChestMenu chest && chest.getContainer() == mob.getChest(), "sneaking and using a tame treasure mob did not open its chest for its owner");
        helper.succeed();
    }
}
