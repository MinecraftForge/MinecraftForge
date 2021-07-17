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

package net.minecraftforge.fluids.capability;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A simple fluid container, to replace the functionality of the old FluidContainerRegistry and IFluidContainerItem.
 * This fluid container may be set so that is can only completely filled or empty. (binary)
 * It may also be set so that it gets consumed when it is drained. (consumable)
 */
public class ItemFluidContainer extends Item
{
    protected final int capacity;

    /**
     * @param capacity   The maximum capacity of this fluid container.
     */
    public ItemFluidContainer(Item.Properties properties, int capacity)
    {
        super(properties);
        this.capacity = capacity;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new FluidHandlerItemStack(stack, capacity);
    }
}
