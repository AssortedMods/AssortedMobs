package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.mobs.api.MobsTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

/**
 * Vanilla's ON_GROUND placement, but it takes the mod's word for what a seal or a walrus can stand
 * on. Vanilla asks the block, and ice answers no to everything but a polar bear: which kept both off
 * the frozen ocean, whose surface is ice, and onto the odd iceberg. The tag is
 * {@code assortedmobs:seals_spawnable_on}; the spawn rule still wants daylight and water close by.
 */
public final class ArcticSpawnPlacement implements SpawnPlacementType {

    public static final ArcticSpawnPlacement INSTANCE = new ArcticSpawnPlacement();

    private ArcticSpawnPlacement() {
    }

    @Override
    public boolean isSpawnPositionOk(LevelReader level, BlockPos pos, EntityType<?> type) {
        if (type == null || !level.getWorldBorder().isWithinBounds(pos)) {
            return false;
        }

        BlockPos below = pos.below();
        BlockState ground = level.getBlockState(below);
        if (!ground.is(MobsTags.Blocks.SEALS_SPAWNABLE_ON) && !ground.isValidSpawn(level, below, type)) {
            return false;
        }
        return this.isValidEmptySpawnBlock(level, pos, type) && this.isValidEmptySpawnBlock(level, pos.above(), type);
    }

    private boolean isValidEmptySpawnBlock(LevelReader level, BlockPos pos, EntityType<?> type) {
        BlockState state = level.getBlockState(pos);
        return NaturalSpawner.isValidEmptySpawnBlock(level, pos, state, state.getFluidState(), type);
    }

    @Override
    public BlockPos adjustSpawnPosition(LevelReader level, BlockPos candidate) {
        BlockPos below = candidate.below();
        return level.getBlockState(below).isPathfindable(PathComputationType.LAND) ? below : candidate;
    }
}
