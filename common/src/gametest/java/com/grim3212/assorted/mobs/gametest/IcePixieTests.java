package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.common.entity.IcePixie;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.survivalPlayer;
import static com.grim3212.assorted.mobs.gametest.MobsTestSupport.*;

/** Only fire hurts an ice pixie. */
final class IcePixieTests {

    private IcePixieTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("ice_pixie_shrugs_off_bare_hands", IcePixieTests::icePixieShrugsOffBareHands);
        out.accept("ice_pixie_is_hurt_by_a_torch_bearer", IcePixieTests::icePixieIsHurtByATorchBearer);
        out.accept("ice_pixie_burns_near_torches", IcePixieTests::icePixieBurnsNearTorches);
    }

    private static void icePixieShrugsOffBareHands(GameTestHelper helper) {
        IcePixie pixie = helper.spawnWithNoFreeWill(MobsEntities.ICE_PIXIE.get(), CENTRE);
        ServerPlayer player = survivalPlayer(helper, new ItemStack(Items.DIAMOND_SWORD));

        boolean hurt = pixie.hurtServer(helper.getLevel(), pixie.damageSources().playerAttack(player), 4.0F);
        helper.assertFalse(hurt, "a sword hurt an ice pixie");
        helper.assertValueEqual(pixie.getHealth(), pixie.getMaxHealth(), "ice pixie health after a sword");
        helper.succeed();
    }

    private static void icePixieIsHurtByATorchBearer(GameTestHelper helper) {
        IcePixie pixie = helper.spawnWithNoFreeWill(MobsEntities.ICE_PIXIE.get(), CENTRE);
        ServerPlayer player = survivalPlayer(helper, new ItemStack(Items.TORCH));

        boolean hurt = pixie.hurtServer(helper.getLevel(), pixie.damageSources().playerAttack(player), 4.0F);
        helper.assertTrue(hurt, "a player holding a torch could not hurt an ice pixie");
        helper.assertTrue(pixie.getHealth() < pixie.getMaxHealth(), "the ice pixie lost no health to a torch bearer");
        helper.succeed();
    }

    private static void icePixieBurnsNearTorches(GameTestHelper helper) {
        IcePixie pixie = helper.spawnWithNoFreeWill(MobsEntities.ICE_PIXIE.get(), CENTRE);
        helper.setBlock(CENTRE.east(2), Blocks.TORCH);
        helper.setBlock(CENTRE.west(2), Blocks.SOUL_TORCH);

        helper.succeedWhen(() -> helper.assertTrue(pixie.getHealth() < pixie.getMaxHealth(), "the ice pixie has not been burnt by the torches beside it"));
    }
}
