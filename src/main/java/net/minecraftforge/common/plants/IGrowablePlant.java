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

package net.minecraftforge.common.plants;

import java.util.Random;

import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * The base interface of all growable plants. Implementers must override canGrow/grow, or callers will end up with a StackOverflowException. This class extends IGrowable in order
 * to reduce the number of patches needed. Modders should not override the IGrowable methods.
 */
@SuppressWarnings("deprecation")
public interface IGrowablePlant extends IPlant, IGrowable
{

    /**
     * @param world
     *            The world
     * @param pos
     *            The current pos
     * @param state
     *            The current state
     * 
     * @return If this plant can grow, and {@link IPlant#grow(World, Random, BlockPos, IBlockState)} will actually do something.
     */
    default boolean canGrow(World world, BlockPos pos, IBlockState state)
    {
        return canGrow(world, pos, state, world.isRemote);
    }

    /**
     * @param world
     *            The world
     * @param rand
     *            A random
     * @param pos
     *            The current pos
     * @param state
     *            The current state
     * 
     * @return If this plant can be forcibly grown using bonemeal.
     */
    boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state);

    /**
     * This method is called to grow the plant. If this plant cannot be grown further, this method should do nothing. This method is not responsible for posting the forge
     * CropGrowPre/Post events. Callers should post those themselves.
     * 
     * @param world
     *            The world
     * @param pos
     *            The current pos
     * @param state
     *            The current state
     * @param natural
     *            If this grow call was a result of "natural" causes like random block updates, or artificial, from fertilizers such as bonemeal.
     */
    default void grow(World world, Random rand, BlockPos pos, IBlockState state, boolean natural)
    {
        grow(world, rand, pos, state);
    }

    @Deprecated
    default boolean canGrow(IBlockReader world, BlockPos pos, IBlockState state, boolean isClient)
    {
        return canGrow((World) world, pos, state);
    }

    @Deprecated
    default void grow(World world, Random rand, BlockPos pos, IBlockState state)
    {
        grow(world, rand, pos, state, false);
    }

}
