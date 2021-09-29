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

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * This event is fired when game checks, if sleeping player should be still considered "in bed".<br>
 * Failing this check will cause player to wake up.<br>
 *
 * This event has a result. {@link HasResult}<br>
 *
 * setResult(ALLOW) informs game that player is still "in bed"<br>
 * setResult(DEFAULT) causes game to check {@link Block#isBed(IBlockState, net.minecraft.world.IWorldReader, BlockPos, Entity)} instead
 */
@HasResult
public class SleepingLocationCheckEvent extends LivingEvent
{

    private final BlockPos sleepingLocation;

    public SleepingLocationCheckEvent(LivingEntity player, BlockPos sleepingLocation)
    {
        super(player);
        this.sleepingLocation = sleepingLocation;
    }

    public BlockPos getSleepingLocation()
    {
        return sleepingLocation;
    }
}
