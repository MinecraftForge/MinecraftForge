/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Fired when the player removes a "repaired" item from the Anvil's Output slot.
 *
 * breakChance specifies as a percentage the chance that the anvil will be "damaged" when used.
 *
 * ItemStacks are the inputs/output from the anvil. They cannot be edited.
 */
public class AnvilRepairEvent extends PlayerEvent
{
    @Nonnull
    private final ItemStack left; // The left side of the input
    @Nonnull
    private final ItemStack right; // The right side of the input
    @Nonnull
    private final ItemStack output; // Set this to set the output stack
    private float breakChance; // Anvil's chance to break (reduced by 1 durability) when this is complete. Default is 12% (0.12f)

    public AnvilRepairEvent(Player player, @Nonnull ItemStack left, @Nonnull ItemStack right, @Nonnull ItemStack output)
    {
        super(player);
        this.output = output;
        this.left = left;
        this.right = right;
        this.setBreakChance(0.12f);
    }

    /**
     * Get the output result from the anvil
     * @return the output
     */
    @Nonnull
    public ItemStack getItemResult() { return output; }

    /**
     * Get the first item input into the anvil
     * @return the first input slot
     */
    @Nonnull
    public ItemStack getItemInput() { return left; }

    /**
     * Get the second item input into the anvil
     * @return the second input slot
     */
    @Nonnull
    public ItemStack getIngredientInput() { return right; }

    public float getBreakChance() { return breakChance; }
    public void setBreakChance(float breakChance) { this.breakChance = breakChance; }
}
