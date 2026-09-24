package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.entity.Parabuzzy;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.mobs.gametest.MobsTestSupport.*;

/** How a parabuzzy behaves: fighting back, and what sitting on a head does to its orders. */
final class ParabuzzyTests {

    private ParabuzzyTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("parabuzzy_turns_on_a_player_who_hits_it", ParabuzzyTests::parabuzzyTurnsOnAPlayerWhoHitsIt);
        out.accept("perching_ends_a_parabuzzys_order_to_sit", ParabuzzyTests::perchingEndsAParabuzzysOrderToSit);
        out.accept("reloaded_parabuzzy_sits_for_its_owner", ParabuzzyTests::reloadedParabuzzySitsForItsOwner);
    }

    private static void parabuzzyTurnsOnAPlayerWhoHitsIt(GameTestHelper helper) {
        ServerPlayer player = standingPlayer(helper, CENTRE.east(3));
        Parabuzzy parabuzzy = helper.spawn(MobsEntities.PARABUZZY.get(), CENTRE.west(2));
        double start = parabuzzy.distanceTo(player);

        // Not on its first tick: HurtByTargetGoal ignores a hurt timestamp of 0.
        helper.runAfterDelay(5, () -> parabuzzy.hurtServer(helper.getLevel(), player.damageSources().playerAttack(player), 1.0F));
        helper.succeedWhen(() -> {
            helper.assertTrue(parabuzzy.getTarget() == player, "the parabuzzy did not turn on the player who hit it: its target is " + parabuzzy.getTarget());
            helper.assertTrue(parabuzzy.isAngry(), "a wild parabuzzy going after a player is not angry");
            helper.assertTrue(parabuzzy.distanceTo(player) < start - 1.0D, "the parabuzzy never came at the player who hit it");
        });
    }

    /** Freshly tamed means sitting; left on, it rode with folded wings and stayed put when set down. */
    private static void perchingEndsAParabuzzysOrderToSit(GameTestHelper helper) {
        ServerPlayer player = standingPlayer(helper, CENTRE);
        Parabuzzy parabuzzy = helper.spawn(MobsEntities.PARABUZZY.get(), CENTRE.west());
        parabuzzy.tame(player);
        parabuzzy.setOrderedToSit(true);

        helper.runAfterDelay(10, () -> {
            helper.assertTrue(parabuzzy.isInSittingPose(), "a parabuzzy ordered to sit never sat");
            parabuzzy.interact(player, InteractionHand.MAIN_HAND, parabuzzy.position());
            helper.assertTrue(parabuzzy.perch().isOn(player), "using a sitting tame parabuzzy did not put it on the player's head");
            helper.assertFalse(parabuzzy.isOrderedToSit(), "a parabuzzy on a head is still ordered to sit");
        });
        helper.runAfterDelay(20, () -> {
            helper.assertFalse(parabuzzy.isInSittingPose(), "a parabuzzy on a head is still in its sitting pose, wings folded");
            helper.succeed();
        });
    }

    /** A sneaking owner's empty hand toggles the order, on a parabuzzy that has been through a save. */
    private static void reloadedParabuzzySitsForItsOwner(GameTestHelper helper) {
        ServerPlayer player = standingPlayer(helper, CENTRE);
        Parabuzzy tamed = helper.spawn(MobsEntities.PARABUZZY.get(), CENTRE.west());
        tamed.tame(player);
        Parabuzzy parabuzzy = afterReload(helper, tamed, MobsEntities.PARABUZZY.get());
        helper.assertTrue(parabuzzy.isOwnedBy(player), "a reloaded parabuzzy no longer knows its owner");

        player.setShiftKeyDown(true);
        parabuzzy.interact(player, InteractionHand.MAIN_HAND, parabuzzy.position());
        helper.assertTrue(parabuzzy.isOrderedToSit(), "a sneaking owner's empty hand did not order it to sit");
        helper.assertFalse(parabuzzy.perch().isPerched(), "a sneaking owner's empty hand put it on their head");
        parabuzzy.interact(player, InteractionHand.MAIN_HAND, parabuzzy.position());
        helper.assertFalse(parabuzzy.isOrderedToSit(), "a second use did not stand it back up");
        helper.succeed();
    }
}
