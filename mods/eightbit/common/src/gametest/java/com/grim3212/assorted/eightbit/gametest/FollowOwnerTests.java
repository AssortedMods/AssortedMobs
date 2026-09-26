package com.grim3212.assorted.eightbit.gametest;

import com.grim3212.assorted.eightbit.common.entity.Bobomb;
import com.grim3212.assorted.eightbit.common.entity.EightBitEntities;
import com.grim3212.assorted.eightbit.common.entity.Parabuzzy;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.survivalPlayer;
import static com.grim3212.assorted.eightbit.gametest.EightBitTestSupport.*;

/** A tamed or owned creature left far behind comes to its owner. */
final class FollowOwnerTests {

    private FollowOwnerTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("tame_parabuzzy_teleports_to_its_owner", FollowOwnerTests::tameParabuzzyTeleportsToItsOwner);
        out.accept("owned_bobomb_teleports_to_its_owner", FollowOwnerTests::ownedBobombTeleportsToItsOwner);
    }

    /** Above the 9-high box, so the platform never reaches into a neighbouring test. */
    private static final BlockPos PLATFORM = new BlockPos(4, 14, 4);

    private static void tameParabuzzyTeleportsToItsOwner(GameTestHelper helper) {
        ServerPlayer owner = ownerOnAPlatform(helper);
        Parabuzzy parabuzzy = helper.spawn(EightBitEntities.PARABUZZY.get(), CENTRE);
        parabuzzy.tame(owner);
        succeedWhenOnThePlatform(helper, parabuzzy);
    }

    private static void ownedBobombTeleportsToItsOwner(GameTestHelper helper) {
        ServerPlayer owner = ownerOnAPlatform(helper);
        Bobomb bobomb = helper.spawn(EightBitEntities.BOBOMB.get(), CENTRE);
        bobomb.setOwner(owner);
        succeedWhenOnThePlatform(helper, bobomb);
    }

    /**
     * An owner standing on a stone platform high over the box, further off than the teleport distance
     * and out of reach on foot.
     */
    private static ServerPlayer ownerOnAPlatform(GameTestHelper helper) {
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                helper.setBlock(PLATFORM.offset(x, -1, z), Blocks.STONE);
                helper.setBlock(PLATFORM.offset(x, 0, z), Blocks.AIR);
                helper.setBlock(PLATFORM.offset(x, 1, z), Blocks.AIR);
            }
        }

        ServerPlayer owner = survivalPlayer(helper);
        owner.snapTo(helper.absoluteVec(Vec3.atBottomCenterOf(PLATFORM)));
        owner.setOnGround(true);
        owner.setInvulnerable(true);
        return owner;
    }

    private static void succeedWhenOnThePlatform(GameTestHelper helper, Mob mob) {
        double platformY = helper.absoluteVec(Vec3.atBottomCenterOf(PLATFORM)).y;
        helper.succeedWhen(() -> {
            helper.assertTrue(mob.getY() >= platformY - 1.0D, mob.getType().getDescriptionId() + " never teleported to its owner, still at y " + mob.getY());
            mob.discard();
        });
    }
}
