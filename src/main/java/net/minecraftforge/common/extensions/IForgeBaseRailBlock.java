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

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface IForgeBaseRailBlock
{

    /**
     * Return true if the rail can make corners.
     * Used by placement logic.
     * @param world The world.
     * @param pos Block's position in world
     * @return True if the rail can make corners.
     */
    boolean isFlexibleRail(BlockState state, BlockGetter world, BlockPos pos);

    /**
     * Returns true if the rail can make up and down slopes.
     * Used by placement logic.
     * @param world The world.
     * @param pos Block's position in world
     * @return True if the rail can make slopes.
     */
    default boolean canMakeSlopes(BlockState state, BlockGetter world, BlockPos pos)
    {
        return true;
    }

    /**
    * Return the rail's direction.
    * Can be used to make the cart think the rail is a different shape,
    * for example when making diamond junctions or switches.
    * The cart parameter will often be null unless it it called from EntityMinecart.
    *
    * @param world The world.
    * @param pos Block's position in world
    * @param state The BlockState
    * @param cart The cart asking for the metadata, null if it is not called by EntityMinecart.
    * @return The direction.
    */
    RailShape getRailDirection(BlockState state, BlockGetter world, BlockPos pos, @Nullable AbstractMinecart cart);

    /**
     * Returns the max speed of the rail at the specified position.
     * @param world The world.
     * @param cart The cart on the rail, may be null.
     * @param pos Block's position in world
     * @return The max speed of the current rail.
     */
    default float getRailMaxSpeed(BlockState state, Level world, BlockPos pos, AbstractMinecart cart)
    {
        return 0.4f;
    }

    /**
      * This function is called by any minecart that passes over this rail.
      * It is called once per update tick that the minecart is on the rail.
      * @param world The world.
      * @param cart The cart on the rail.
      * @param pos Block's position in world
      */
    default void onMinecartPass(BlockState state, Level world, BlockPos pos, AbstractMinecart cart){}
}
