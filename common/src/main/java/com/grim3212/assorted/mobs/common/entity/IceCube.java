package com.grim3212.assorted.mobs.common.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** What an ice pixie throws. It hits harder the faster it is going, and shatters on anything. */
public class IceCube extends ThrowableItemProjectile {

    public IceCube(EntityType<? extends IceCube> type, Level level) {
        super(type, level);
    }

    public IceCube(Level level, LivingEntity owner, ItemStack stack) {
        super(MobsEntities.ICE_CUBE.get(), owner, level, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ICE;
    }

    /** Heavier than a snowball, so it drops sooner. */
    @Override
    protected double getDefaultGravity() {
        return 0.05D;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel level) {
            int damage = Mth.ceil(this.getDeltaMovement().length() * 2.0D);
            result.getEntity().hurtServer(level, this.damageSources().thrown(this, this.getOwner()), damage);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level() instanceof ServerLevel level) {
            level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, Items.ICE), this.getX(), this.getY(), this.getZ(), 8, 0.1D, 0.1D, 0.1D, 0.05D);
            level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.HOSTILE, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.discard();
        }
    }
}
