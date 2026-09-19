package com.grim3212.assorted.mobs.common.item;

import com.grim3212.assorted.mobs.common.entity.Bobomb;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * A Bob-omb waiting to be put down. It stands on the face that was clicked, facing the same way, and
 * belongs to the player who put it there.
 */
public class BobombItem extends Item {

    public BobombItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        Bobomb bobomb = MobsEntities.BOBOMB.get().create(level, EntitySpawnReason.SPAWN_ITEM_USE);
        if (bobomb == null) {
            return InteractionResult.FAIL;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Player player = context.getPlayer();
        bobomb.snapTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player != null ? player.getYRot() : 0.0F, 0.0F);
        bobomb.setOwner(player);
        level.addFreshEntity(bobomb);
        level.gameEvent(player, GameEvent.ENTITY_PLACE, pos);
        context.getItemInHand().consume(1, player);
        return InteractionResult.SUCCESS_SERVER;
    }
}
