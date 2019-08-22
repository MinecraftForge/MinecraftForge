/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired when all players are asleep and the time should be set to day.<br>
 *
 * setWakeUpTime(wakeUpTime) sets a new time that will be added to the dayTime.<br>
 */
public class SleepFinishedTimeEvent extends WorldEvent
{
    private long additionalTime;

    public SleepFinishedTimeEvent(ServerWorld worldIn)
    {
        super(worldIn);
        this.additionalTime = -1L;
    }

    /**
     * @returns the time addition
     */
    public long getTimeAddition()
    {
        return additionalTime;
    }

    /**
     * Sets the new time which should be set when all players wake up
     * @param additionalTimeIn The new time at wakeup
     */
    public void setTimeAddition(long additionalTimeIn)
    {
        this.additionalTime = additionalTimeIn;
    }
}
