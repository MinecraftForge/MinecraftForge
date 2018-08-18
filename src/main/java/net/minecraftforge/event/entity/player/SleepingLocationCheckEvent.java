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

package net.minecraftforge.event.entity.player;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * This event is fired when game checks, if sleeping player should be still considered "in bed".<br>
 * Failing this check will cause player to wake up.<br>
 *
 * This event has a result. {@link HasResult}<br>
 *
 * setResult(ALLOW) informs game that player is still "in bed"<br>
 * setResult(DEFAULT) causes game to check {@link Block#isBed(IBlockState, IBlockAccess, BlockPos, Entity)} instead
 */
@HasResult
public class SleepingLocationCheckEvent extends PlayerEvent
{

    private final BlockPos sleepingLocation;

    public SleepingLocationCheckEvent(EntityPlayer player, BlockPos sleepingLocation)
    {
        super(player);
        this.sleepingLocation = sleepingLocation;
    }

    public BlockPos getSleepingLocation()
    {
        return sleepingLocation;
    }
}
