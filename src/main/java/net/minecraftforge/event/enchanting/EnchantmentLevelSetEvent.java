/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.enchanting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

/**
 * Fired when the enchantment level is set for each of the three potential enchantments in the enchanting table.
 * The {@link #enchantLevel} is set to the vanilla value and can be modified by this event handler.
 *
 * The {@link #enchantRow} is used to determine which enchantment level is being set, 1, 2, or 3. The {@link #power} is a number
 * from 0-15 and indicates how many bookshelves surround the enchanting table. The {@link #itemStack} representing the item being
 * enchanted is also available.
 */
public class EnchantmentLevelSetEvent extends Event
{
    private final Level level;
    private final BlockPos pos;
    private final int enchantRow;
    private final int power;
    @Nonnull
    private final ItemStack itemStack;
    private final int originalLevel;
    private int enchantLevel;

    public EnchantmentLevelSetEvent(Level level, BlockPos pos, int enchantRow, int power, @Nonnull ItemStack itemStack, int enchantLevel)
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
    @Nonnull
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
     * @param enchantLevel the new level of the enchantment (0-30)
     */
    public void setEnchantLevel(int enchantLevel)
    {
        this.enchantLevel = enchantLevel;
    }
}
