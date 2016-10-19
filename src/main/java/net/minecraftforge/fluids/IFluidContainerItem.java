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

package net.minecraftforge.fluids;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.wrappers.FluidContainerItemWrapper;

/**
 * Implement this interface on Item classes that support external manipulation of their internal
 * fluid storage.
 *
 * A reference implementation is provided {@link ItemFluidContainer}.
 *
 * NOTE: Use of NBT data on the containing ItemStack is encouraged.
 *
 * @deprecated See {@link net.minecraftforge.fluids.capability.ItemFluidContainer} for a CapabilityProvider implementing the Capability {@link net.minecraftforge.fluids.capability.IFluidHandler}
 * @see FluidContainerItemWrapper
 */
@Deprecated
public interface IFluidContainerItem
{
    /**
     *
     * @param container
     *            ItemStack which is the fluid container.
     * @return FluidStack representing the fluid in the container, null if the container is empty.
     */
    FluidStack getFluid(ItemStack container);

    /**
     *
     * @param container
     *            ItemStack which is the fluid container.
     * @return Capacity of this fluid container.
     */
    int getCapacity(ItemStack container);

    /**
     *
     * @param container
     *            ItemStack which is the fluid container.
     * @param resource
     *            FluidStack attempting to fill the container.
     * @param doFill
     *            If false, the fill will only be simulated.
     * @return Amount of fluid that was (or would have been, if simulated) filled into the
     *         container.
     */
    int fill(ItemStack container, FluidStack resource, boolean doFill);

    /**
     *
     * @param container
     *            ItemStack which is the fluid container.
     * @param maxDrain
     *            Maximum amount of fluid to be removed from the container.
     * @param doDrain
     *            If false, the drain will only be simulated.
     * @return Amount of fluid that was (or would have been, if simulated) drained from the
     *         container.
     */
    FluidStack drain(ItemStack container, int maxDrain, boolean doDrain);
}
