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

package net.minecraftforge.event.entity.living;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired when the ender dragon or wither attempts to destroy a block and when ever a zombie attempts to break a door. Basically a event version of {@link Block#canEntityDestroy(IBlockState, IBlockAccess, BlockPos, Entity)}<br>
 * <br>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * If this event is canceled, the block will not be destroyed.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingDestroyBlockEvent extends LivingEvent
{
    private final BlockPos pos;
    private final BlockState state;
    
    public LivingDestroyBlockEvent(LivingEntity entity, BlockPos pos, BlockState state)
    {
        super(entity);
        this.pos = pos;
        this.state = state;
    }

    public BlockState getState()
    {
        return state;
    }
    
    public BlockPos getPos()
    {
        return pos;
    }
}
