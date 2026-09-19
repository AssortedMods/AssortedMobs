package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.entity.TreasureChest;
import com.grim3212.assorted.mobs.common.entity.TreasureMob;
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
