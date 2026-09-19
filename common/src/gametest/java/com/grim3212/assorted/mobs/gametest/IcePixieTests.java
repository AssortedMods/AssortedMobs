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
        out.accept("ice_pixie_drops_snow_and_ice", IcePixieTests::icePixieDropsSnowAndIce);
    }

    /**
     * Snowballs every time, and nothing that is not snow or ice but, rarely, Assorted Tools' frost
     * rod - by its tag, so the pool is there whether Tools is or not.
     */
    private static void icePixieDropsSnowAndIce(GameTestHelper helper) {
        IcePixie pixie = helper.spawnWithNoFreeWill(MobsEntities.ICE_PIXIE.get(), CENTRE);
        ServerPlayer player = survivalPlayer(helper, new ItemStack(Items.TORCH));
        pixie.hurtServer(helper.getLevel(), pixie.damageSources().playerAttack(player), 1000.0F);
        helper.assertFalse(pixie.isAlive(), "a torch bearer could not kill the ice pixie");

        String table = readJson(helper, "/data/assortedmobs/loot_table/entities/ice_pixie.json").toString();
        helper.assertTrue(table.contains("\"c:rods/frost\""), "the ice pixie has no chance of dropping a frost rod");

        helper.succeedWhen(() -> {
            helper.assertItemEntityPresent(Items.SNOWBALL, CENTRE, 2.0D);
            helper.assertItemEntityNotPresent(Items.COD, CENTRE, 2.0D);
            helper.assertItemEntityNotPresent(Items.STICK, CENTRE, 2.0D);
        });
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
