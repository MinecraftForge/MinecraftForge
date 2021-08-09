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

package net.minecraftforge.common.extensions;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public interface IForgeFlowingFluid extends IForgeFluid
{
    private FlowingFluid self()
    {
        return (FlowingFluid) this;
    }

    /**
     * @param state      The {@link FluidState} of this fluid.
     * @param otherState The secondary {@link FluidState} to check
     * @return Whether the fluid type of the other fluid matches either the flowing or source type of the first fluidstate.
     */
    @Override
    default boolean is(FluidState state, FluidState otherState)
    {
        Fluid otherFluid = otherState.getType();
        return otherFluid == self().getFlowing() || otherFluid == self().getSource();
    }
}
