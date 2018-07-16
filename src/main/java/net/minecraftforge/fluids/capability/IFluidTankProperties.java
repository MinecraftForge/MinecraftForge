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

package net.minecraftforge.fluids.capability;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;

/**
 * Simplified Read-only Information about the internals of an {@link IFluidHandler}.
 * This is useful for displaying information, and as hints for interacting with it.
 * These properties are constant and do not depend on the fluid contents (except the contents themselves, of course).
 *
 * The information here may not tell the full story of how the tank actually works,
 * for real fluid transactions you must use {@link IFluidHandler} to simulate, check, and then interact.
 * None of the information in these properties is required to successfully interact using a {@link IFluidHandler}.
 */
public interface IFluidTankProperties
{
    /**
     * @return A copy of the fluid contents of this tank. May be null.
     * To modify the contents, use {@link IFluidHandler}.
     */
    @Nullable
    FluidStack getContents();

    /**
     * @return The maximum amount of fluid this tank can hold, in millibuckets.
     */
    int getCapacity();

    /**
     * Returns true if the tank can be filled at any time (even if it is currently full).
     * It does not consider the contents or capacity of the tank.
     *
     * This value is constant. If the tank behavior is more complicated, returns true.
     */
    boolean canFill();

    /**
     * Returns true if the tank can be drained at any time (even if it is currently empty).
     * It does not consider the contents or capacity of the tank.
     *
     * This value is constant. If the tank behavior is more complicated, returns true.
     */
    boolean canDrain();

    /**
     * Returns true if the tank can be filled with a specific type of fluid.
     * Used as a filter for fluid types.
     *
     * Does not consider the current contents or capacity of the tank,
     * only whether it could ever fill with this type of fluid.
     * {@link FluidStack} is used here because fluid properties can depend on NBT, the amount is ignored.
     */
    boolean canFillFluidType(FluidStack fluidStack);

    /**
     * Returns true if the tank can drain out this a specific of fluid.
     * Used as a filter for fluid types.
     *
     * Does not consider the current contents or capacity of the tank,
     * only whether it could ever drain out this type of fluid.
     * {@link FluidStack} is used here because fluid properties can depend on NBT, the amount is ignored.
     */
    boolean canDrainFluidType(FluidStack fluidStack);
}
