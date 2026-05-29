/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Fired when the player removes a "repaired" item from the Anvil's Output slot.
 *
 * breakChance specifies as a percentage the chance that the anvil will be "damaged" when used.
 *
 * ItemStacks are the inputs/output from the anvil. They cannot be edited.
 */
@NullMarked
public final class AnvilRepairEvent extends MutableEvent implements PlayerEvent {
    public static final EventBus<AnvilRepairEvent> BUS = EventBus.create(AnvilRepairEvent.class);

    private final Player player;
    private final ItemStack left; // The left side of the input
    private final ItemStack right; // The right side of the input
    private final ItemStack output; // Set this to set the output stack
    private float breakChance; // Anvil's chance to break (reduced by 1 durability) when this is complete. Default is 12% (0.12f)

    public AnvilRepairEvent(Player player, ItemStack left, ItemStack right, ItemStack output) {
        this.player = player;
        this.output = output;
        this.left = left;
        this.right = right;
        this.setBreakChance(0.12f);
    }

    @Override
    public Player getEntity() {
        return player;
    }

    /**
     * Get the output result from the anvil
     * @return the output
     */
    public ItemStack getOutput() { return output; }

    /**
     * Get the first item input into the anvil
     * @return the first input slot
     */
    public ItemStack getLeft() { return left; }

    /**
     * Get the second item input into the anvil
     * @return the second input slot
     */
    public ItemStack getRight() { return right; }

    public float getBreakChance() { return breakChance; }
    public void setBreakChance(float breakChance) { this.breakChance = breakChance; }
}
