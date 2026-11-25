package com.grim3212.assorted.treasuremob.common.entity;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * The chest a treasure mob carries. It closes when the mob dies or is pushed out of reach; the mob
 * itself stands still while anyone has it open.
 */
public class TreasureChest extends SimpleContainer {

    public static final int SIZE = 27;

    private final TreasureMob owner;
    /** Server side only: the menu opens and closes it there. */
    private final List<Player> viewers = new ArrayList<>();

    public TreasureChest(TreasureMob owner) {
        super(SIZE);
        this.owner = owner;
    }

    @Override
    public void startOpen(Player user) {
        this.viewers.add(user);
    }

    @Override
    public void stopOpen(Player user) {
        this.viewers.remove(user);
    }

    /** Who has it open, first opened first. */
    public List<Player> getViewers() {
        return List.copyOf(this.viewers);
    }

    public boolean isOpen() {
        return !this.viewers.isEmpty();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.owner.isAlive() && player.canInteractWithEntity(this.owner, 4.0D);
    }
}
