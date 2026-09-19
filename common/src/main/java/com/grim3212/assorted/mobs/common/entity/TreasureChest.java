package com.grim3212.assorted.mobs.common.entity;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;

/** The chest a treasure mob carries. It closes when the mob dies or wanders out of reach. */
public class TreasureChest extends SimpleContainer {

    public static final int SIZE = 27;

    private final TreasureMob owner;

    public TreasureChest(TreasureMob owner) {
        super(SIZE);
        this.owner = owner;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.owner.isAlive() && player.isWithinEntityInteractionRange(this.owner, 4.0D);
    }
}
