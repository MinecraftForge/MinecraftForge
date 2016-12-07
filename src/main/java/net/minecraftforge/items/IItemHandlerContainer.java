/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
package net.minecraftforge.items;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Declares an item handler as being aware of container slot interactions.
 */
public interface IItemHandlerContainer
{
    /**
     * Queries if the given slot is acceptable for the inventory
     * @param slot The slot number within the inventory
     * @param stack The stack being tested
     * @return True if the provided stack is acceptable
     */
    boolean acceptsStack(int slot, @Nonnull ItemStack stack);

    /**
     * Gets the stack size limit allowed by the given slot and the given stack
     * @param slot The slot number within the inventory
     * @param stack An optional stack, for when the limit depends on the input
     * @return The stack size limit
     */
    int getEffectiveStackLimit(int slot, @Nonnull ItemStack stack);
}
