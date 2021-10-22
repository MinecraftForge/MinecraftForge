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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
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
     * Gets whether the fluid type of the other fluid matches either the
     * flowing or source type of the current fluid.
     *
     * @param state      the {@link FluidState} of this fluid
     * @param otherState the secondary {@link FluidState} to check
     * @return {@code true} if the fluid matches, {@code false} otherwise
     */
    @Override
    default boolean sameType(FluidState state, FluidState otherState)
    {
        Fluid otherFluid = otherState.getType();
        return otherFluid == self().getFlowing() || otherFluid == self().getSource();
    }

    /**
     * Gets whether a fluid can multiply and create new source blocks.
     *
     * @param state the fluid trying to multiply
     * @param reader the current level the fluid is in
     * @param pos the position of the fluid
     * @return {@code true} if the fluid can multiply, {@code false} otherwise
     */
    default boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos)
    {
        return false;
    }
}
