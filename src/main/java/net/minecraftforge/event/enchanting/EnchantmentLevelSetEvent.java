/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.enchanting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when the enchantment level is set for each of the three potential enchantments in the enchanting table.
 * The {@link #level} is set to the vanilla value and can be modified by this event handler.
 *
 * The {@link #enchantRow} is used to determine which enchantment level is being set, 1, 2, or 3. The {@link #power} is a number
 * from 0-15 and indicates how many bookshelves surround the enchanting table. The {@link #itemStack} representing the item being
 * enchanted is also available.
 */
public class EnchantmentLevelSetEvent extends net.minecraftforge.eventbus.api.Event
{
    private final Level level;
    private final BlockPos pos;
    private final int enchantRow;
    private final int power;
    @NotNull
    private final ItemStack itemStack;
    private final int originalLevel;
    private int enchantLevel;

    public EnchantmentLevelSetEvent(Level level, BlockPos pos, int enchantRow, int power, @NotNull ItemStack itemStack, int enchantLevel)
    {
        this.level = level;
        this.pos = pos;
        this.enchantRow = enchantRow;
        this.power = power;
        this.itemStack = itemStack;
        this.originalLevel = enchantLevel;
        this.enchantLevel = enchantLevel;
    }

    /**
     * Get the world object
     *
     * @return the world object
     */
    public Level getLevel()
    {
        return level;
    }

    /**
     * Get the pos of the enchantment table
     *
     * @return the pos of the enchantment table
     */
    public BlockPos getPos()
    {
        return pos;
    }

    /**
     * Get the row for which the enchantment level is being set
     *
     * @return the row for which the enchantment level is being set
     */
    public int getEnchantRow()
    {
        return enchantRow;
    }

    /**
     * Get the power (# of bookshelves) for the enchanting table
     *
     * @return the power (# of bookshelves) for the enchanting table
     */
    public int getPower()
    {
        return power;
    }

    /**
     * Get the item being enchanted
     *
     * @return the item being enchanted
     */
    @NotNull
    public ItemStack getItem()
    {
        return itemStack;
    }

    /**
     * Get the original level of the enchantment for this row (0-30)
     *
     * @return the original level of the enchantment for this row (0-30)
     */
    public int getOriginalLevel()
    {
        return originalLevel;
    }

    /**
     * Get the level of the enchantment for this row (0-30)
     *
     * @return the level of the enchantment for this row (0-30)
     */
    public int getEnchantLevel()
    {
        return enchantLevel;
    }

    /**
     * Set the new level of the enchantment (0-30)
     *
     * @param level the new level of the enchantment (0-30)
     */
    public void setEnchantLevel(int level)
    {
        this.enchantLevel = level;
    }
}
