package com.grim3212.assorted.mobs.common.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

/**
 * Sitting on a player's head. This is not riding: nothing may ride a player, because a player is
 * never saved and its passengers would be lost with it. The mob stays an ordinary entity of the
 * level, saved wherever it is, and both sides pin it to the player at the end of every tick. Which
 * player is synced by entity id, and not saved, so a mob loaded from disk is back on the ground.
 */
public final class Perch {

    private final Mob mob;
    private final EntityDataAccessor<OptionalInt> player;

    public Perch(Mob mob, EntityDataAccessor<OptionalInt> player) {
        this.mob = mob;
        this.player = player;
    }

    public static void define(SynchedEntityData.Builder builder, EntityDataAccessor<OptionalInt> player) {
        builder.define(player, OptionalInt.empty());
    }

    public boolean isPerched() {
        return this.mob.getEntityData().get(this.player).isPresent();
    }

    public boolean isOn(Player player) {
        OptionalInt id = this.mob.getEntityData().get(this.player);
        return id.isPresent() && id.getAsInt() == player.getId();
    }

    /** The player carrying this mob, if it is on one this side knows about. */
    @Nullable
    public Player player() {
        OptionalInt id = this.mob.getEntityData().get(this.player);
        if (id.isEmpty()) {
            return null;
        }
        return this.mob.level().getEntity(id.getAsInt()) instanceof Player carrier ? carrier : null;
    }

    /** Whether something already sits on this player: each player carries one. */
    public static boolean isCarrying(Player player) {
        return !player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(1.0D, 2.0D, 1.0D),
                mob -> mob instanceof PerchingMob perching && perching.perch().isOn(player)).isEmpty();
    }

    /** Server side. Whether the mob climbed up. */
    public boolean perchOn(Player player) {
        if (this.isPerched() || isCarrying(player)) {
            return false;
        }

        this.mob.getEntityData().set(this.player, OptionalInt.of(player.getId()));
        this.mob.getNavigation().stop();
        this.mob.setTarget(null);
        this.follow(player);
        player.sendOverlayMessage(Component.translatable("mount.onboard", Component.keybind("key.sneak")));
        return true;
    }

    /** Server side. Back down, where the player stands. */
    public void leave() {
        Player carrier = this.player();
        this.mob.getEntityData().set(this.player, OptionalInt.empty());
        if (carrier != null) {
            this.mob.setPos(carrier.getX(), carrier.getY(), carrier.getZ());
        }
    }

    /** At the end of the mob's own tick, on both sides. */
    public void tick() {
        if (!this.isPerched()) {
            return;
        }

        Player carrier = this.player();
        boolean server = !this.mob.level().isClientSide();
        if (carrier == null || !carrier.isAlive() || carrier.isSpectator()) {
            // A client may simply not have the player yet; only the server decides it is gone.
            if (server) {
                this.leave();
            }
            return;
        }

        if (server && carrier.isShiftKeyDown() && carrier.onGround()) {
            this.leave();
            return;
        }

        this.follow(carrier);
    }

    private void follow(Player carrier) {
        this.mob.setPos(carrier.getX(), carrier.getY() + carrier.getBbHeight(), carrier.getZ());
        this.mob.setDeltaMovement(Vec3.ZERO);
        this.mob.setYRot(carrier.getYRot());
        this.mob.setYBodyRot(carrier.getYRot());
        this.mob.setYHeadRot(carrier.getYHeadRot());
        this.mob.resetFallDistance();
    }
}
