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

package net.minecraftforge.common.brewing;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Wraps the ingredients for a brewing recipe in a container for use in the recipe implementation
 */
public interface IBrewingContainer extends Container
{
    @Override
    default void clearContent()
    {
    }

    @Override
    default int getContainerSize()
    {
        return 2;
    }

    @Override
    default boolean isEmpty()
    {
        return base().isEmpty() && reagent().isEmpty();
    }

    @Override
    default ItemStack getItem(final int slot)
    {
        return switch (slot) {
            case 0 -> base();
            case 1 -> reagent();
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    default ItemStack removeItem(final int slot, final int amount)
    {
        return getItem(slot).split(amount);
    }

    @Override
    default ItemStack removeItemNoUpdate(final int slot)
    {
        return getItem(slot);
    }

    @Override
    default void setItem(final int p_18944_, final ItemStack p_18945_)
    {
    }

    @Override
    default void setChanged()
    {
    }

    @Override
    default boolean stillValid(final Player p_18946_)
    {
        return true;
    }

    ItemStack base();

    ItemStack reagent();

    record Impl(ItemStack base, ItemStack reagent) implements IBrewingContainer
    {
    }
}
