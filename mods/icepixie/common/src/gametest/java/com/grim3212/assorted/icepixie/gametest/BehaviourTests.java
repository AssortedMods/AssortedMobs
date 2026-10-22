package com.grim3212.assorted.icepixie.gametest;

import com.grim3212.assorted.icepixie.common.entity.IceCube;
import com.grim3212.assorted.icepixie.common.entity.IcePixie;
import com.grim3212.assorted.icepixie.common.entity.IcePixieEntities;
import com.grim3212.assorted.lib.test.TestSupport;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.icepixie.gametest.IcePixieTestSupport.CENTRE;

/** What the site says an ice pixie does: throws ice at a player it sees, and moves faster on ice and snow. */
final class BehaviourTests {

    private BehaviourTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("ice_pixie_throws_ice_at_a_player", BehaviourTests::icePixieThrowsIceAtAPlayer);
        out.accept("ice_pixie_is_faster_on_snow", BehaviourTests::icePixieIsFasterOnSnow);
    }

    /** A player who can be hurt, as the target goal passes over one who cannot. */
    private static void icePixieThrowsIceAtAPlayer(GameTestHelper helper) {
        helper.spawn(IcePixieEntities.ICE_PIXIE.get(), CENTRE);
        ServerPlayer player = TestSupport.mortalPlayer(helper, ItemStack.EMPTY);
        TestSupport.stand(helper, player, CENTRE.east(3).below());
        helper.succeedWhen(() -> helper.assertFalse(
                helper.getLevel().getEntitiesOfClass(IceCube.class, new AABB(helper.absolutePos(CENTRE)).inflate(8.0D)).isEmpty(),
                "an ice pixie with a player three blocks off threw no ice"));
    }

    private static void icePixieIsFasterOnSnow(GameTestHelper helper) {
        helper.setBlock(CENTRE.below(), Blocks.STONE);
        helper.setBlock(CENTRE.east(3).below(), Blocks.SNOW_BLOCK);
        IcePixie onStone = helper.spawnWithNoFreeWill(IcePixieEntities.ICE_PIXIE.get(), CENTRE);
        IcePixie onSnow = helper.spawnWithNoFreeWill(IcePixieEntities.ICE_PIXIE.get(), CENTRE.east(3));
        helper.succeedWhen(() -> helper.assertTrue(
                onSnow.getAttributeValue(Attributes.MOVEMENT_SPEED) > onStone.getAttributeValue(Attributes.MOVEMENT_SPEED),
                "an ice pixie on snow is no faster than one on stone"));
    }
}
