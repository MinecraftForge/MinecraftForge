/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.event.world;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * SaplingGrowTreeEvent is fired when a sapling grows into a tree.<br>
 * This event is fired during sapling growth in
 * {@link BlockSapling#generateTree(World, BlockPos, IBlockState, Random)}.<br>
 * <br>
 * {@link #pos} contains the coordinates of the growing sapling. <br>
 * {@link #rand} contains an instance of Random for use. <br>
 * <br>
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * <br>
 * This event has a result. {@link HasResult} <br>
 * This result determines if the sapling is allowed to grow. <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.<br>
 **/
@HasResult
public class SaplingGrowTreeEvent extends WorldEvent
{
    private final BlockPos pos;
    private final Random rand;

    public SaplingGrowTreeEvent(IWorld world, Random rand, BlockPos pos)
    {
        super(world);
        this.rand = rand;
        this.pos = pos;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public Random getRand()
    {
        return rand;
    }
}
