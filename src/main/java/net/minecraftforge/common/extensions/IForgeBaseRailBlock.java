/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IForgeBaseRailBlock
{

    /**
     * Return true if the rail can make corners.
     * Used by placement logic.
     * @param level The level.
     * @param pos Block's position in level
     * @return True if the rail can make corners.
     */
    boolean isFlexibleRail(BlockState state, BlockGetter level, BlockPos pos);

    /**
     * Returns true if the rail can make up and down slopes.
     * Used by placement logic.
     * @param level The level.
     * @param pos Block's position in level
     * @return True if the rail can make slopes.
     */
    default boolean canMakeSlopes(BlockState state, BlockGetter level, BlockPos pos)
    {
        return true;
    }

    /**
    * Return the rail's direction.
    * Can be used to make the cart think the rail is a different shape,
    * for example when making diamond junctions or switches.
    * The cart parameter will often be null unless it it called from EntityMinecart.
    *
    * @param level The level.
    * @param pos Block's position in level
    * @param state The BlockState
    * @param cart The cart asking for the metadata, null if it is not called by EntityMinecart.
    * @return The direction.
    */
    RailShape getRailDirection(BlockState state, BlockGetter level, BlockPos pos, @Nullable AbstractMinecart cart);

    /**
     * Returns the max speed of the rail at the specified position.
     * @param level The level.
     * @param cart The cart on the rail, may be null.
     * @param pos Block's position in level
     * @return The max speed of the current rail.
     */
    default float getRailMaxSpeed(BlockState state, Level level, BlockPos pos, AbstractMinecart cart)
    {
        if (cart instanceof MinecartFurnace) return cart.isInWater() ? 0.15f : 0.2f;
        else return cart.isInWater() ? 0.2f : 0.4f;
    }

    /**
      * This function is called by any minecart that passes over this rail.
      * It is called once per update tick that the minecart is on the rail.
      * @param level The level.
      * @param cart The cart on the rail.
      * @param pos Block's position in level
      */
    default void onMinecartPass(BlockState state, Level level, BlockPos pos, AbstractMinecart cart){}

    /**
     * Returns true if the given {@link RailShape} is valid for this rail block.
     * This is called when the RailShape for the initial placement of this block is calculated or
     * when another rail block tries to connect to this block and this block's RailState calculates
     * the new RailShape for its current neigbors.
     * @param shape The new RailShape
     * @return True when the given RailShape is valid
     */
    default boolean isValidRailShape(RailShape shape)
    {
        return true;
    }
}
