/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import javax.annotation.Nullable;

/**
 * A tank is the unit of interaction with Fluid inventories.
 *
 * A reference implementation can be found at {@link FluidTank}.
 */
public interface IFluidTank
{
    /**
     * @return FluidStack representing the fluid in the tank, null if the tank is empty.
     */
    @Nullable
    FluidStack getFluid();

    /**
     * @return Current amount of fluid in the tank.
     */
    int getFluidAmount();

    /**
     * @return Capacity of this fluid tank.
     */
    int getCapacity();

    /**
     * Returns a wrapper object {@link FluidTankInfo } containing the capacity of the tank and the
     * FluidStack it holds.
     *
     * Should prevent manipulation of the IFluidTank. See {@link FluidTank}.
     *
     * @return State information for the IFluidTank.
     */
    FluidTankInfo getInfo();

    /**
     *
     * @param resource
     *            FluidStack attempting to fill the tank.
     * @param doFill
     *            If false, the fill will only be simulated.
     * @return Amount of fluid that was accepted by the tank.
     */
    int fill(FluidStack resource, boolean doFill);

    /**
     *
     * @param maxDrain
     *            Maximum amount of fluid to be removed from the container.
     * @param doDrain
     *            If false, the drain will only be simulated.
     * @return Amount of fluid that was removed from the tank.
     */
    @Nullable
    FluidStack drain(int maxDrain, boolean doDrain);
}
