package com.grim3212.assorted.eightbit.gametest;

import com.grim3212.assorted.eightbit.common.entity.EightBitEntities;
import com.grim3212.assorted.eightbit.common.entity.Parabuzzy;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.eightbit.gametest.EightBitTestSupport.CENTRE;
import static com.grim3212.assorted.eightbit.gametest.EightBitTestSupport.standingPlayer;

/** The parabuzzy as a pet, as the site describes it: a fish tames one, food heals it, and it breaks its owner's falls from their head. */
final class TamingTests {

    private TamingTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("parabuzzy_is_tamed_with_fish_and_healed_with_food", TamingTests::parabuzzyIsTamedWithFishAndHealedWithFood);
        out.accept("perched_parabuzzy_spares_its_owner_a_fall", TamingTests::perchedParabuzzySparesItsOwnerAFall);
    }

    /** One fish in three tames, so a stack all but certainly does. */
    private static void parabuzzyIsTamedWithFishAndHealedWithFood(GameTestHelper helper) {
        ServerPlayer player = standingPlayer(helper, CENTRE);
        Parabuzzy parabuzzy = helper.spawnWithNoFreeWill(EightBitEntities.PARABUZZY.get(), CENTRE.east());
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.COD, 64));
        for (int i = 0; i < 64 && !parabuzzy.isTame(); i++) {
            parabuzzy.mobInteract(player, InteractionHand.MAIN_HAND);
        }
        helper.assertTrue(parabuzzy.isTame() && parabuzzy.isOwnedBy(player), "sixty-four fish did not tame a parabuzzy");

        parabuzzy.setHealth(1.0F);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BREAD));
        parabuzzy.mobInteract(player, InteractionHand.MAIN_HAND);
        helper.assertTrue(parabuzzy.getHealth() > 1.0F, "bread did not heal a tame parabuzzy");
        helper.succeed();
    }

    private static void perchedParabuzzySparesItsOwnerAFall(GameTestHelper helper) {
        ServerPlayer player = standingPlayer(helper, CENTRE);
        Parabuzzy parabuzzy = helper.spawnWithNoFreeWill(EightBitEntities.PARABUZZY.get(), CENTRE.east());
        parabuzzy.tame(player);
        parabuzzy.interact(player, InteractionHand.MAIN_HAND);
        helper.assertTrue(parabuzzy.perch().isOn(player), "the tame parabuzzy did not climb onto its owner");

        player.fallDistance = 20.0F;
        parabuzzy.tick();
        helper.assertValueEqual(player.fallDistance, 0.0F, "the fall its owner had built up with a parabuzzy on their head");
        helper.succeed();
    }
}
