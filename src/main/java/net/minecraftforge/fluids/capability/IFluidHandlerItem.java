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

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.FluidStack;

/**
 * ItemStacks handled by an {@link IFluidHandlerBlock} may change, so this class allows
 * users of the fluid handler to get the container after it has been used.
 */
public interface IFluidHandlerItem extends IFluidHandlerBase {
    /**
     * Fills fluid into item, distribution is left entirely to the IFluidHandlerBlock.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param action   If SIMULATE, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled
     * and the resulting ItemStack of the Item that performed the fill action.
     */
    FluidResult fillItem(FluidStack resource, IFluidHandlerBlock.FluidAction action);


    /**
     * Drains fluid out of item, distribution is left entirely to the IFluidHandlerBlock.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained and the resulting ItemStack of the Item that performed the fill action.
     */
    @Nonnull
    FluidResult drainItem(FluidStack resource, IFluidHandlerBlock.FluidAction action);

    /**
     * Drains fluid out of item, distribution is left entirely to the IFluidHandlerBlock.
     * <p/>
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained and the resulting ItemStack of the Item that performed the fill action.
     */
    @Nonnull
    FluidResult drainItem(int maxDrain, IFluidHandlerBlock.FluidAction action);
}