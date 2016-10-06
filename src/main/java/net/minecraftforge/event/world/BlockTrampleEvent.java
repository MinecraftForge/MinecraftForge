/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * BlockTrampleEvent is fired when a block is about to change state due to being trampled.<br>
 * This event is fired during farmland trampling in
 * {@link BlockFarmland#onFallenUpon(World, BlockPos, Entity, float)}.<br>
 * <br>
 * {@link #pos} contains the coordinates of the block.<br>
 * {@link #origin} contains the current state of the block.<br>
 * {@link #target} contains the state of the block to which it will be changed.<br>
 * {@link #entity} contains the entity that triggered this event.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the block state does not change.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class BlockTrampleEvent extends BlockEvent
{
    private final EntityLivingBase entity;
    private IBlockState target;

    public BlockTrampleEvent(World world, BlockPos pos, IBlockState origin, IBlockState target, EntityLivingBase entity)
    {
        super(world, pos, origin);
        this.entity = entity;
        this.target = target;
    }

    /**
     * Get the current state of the block (before the change has occurred)
     *
     * @return origin state of the block
     */
    @Override
    public IBlockState getState()
    {
        return super.getState();
    }

    /**
     * Get the state to which the block will change after the event has processed
     *
     * @return target state of the block
     */
    public IBlockState getTargetState()
    {
        return target;
    }

    /**
     * Set the state to which the block will change after the event has processed
     */
    public void setTargetState(IBlockState target)
    {
        this.target = target;
    }

    /**
     * Get the entity that trampled the block
     *
     * @return entity that triggered the event
     */
    public EntityLivingBase getEntity()
    {
        return entity;
    }
}
