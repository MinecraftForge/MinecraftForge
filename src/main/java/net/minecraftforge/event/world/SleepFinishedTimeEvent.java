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

import net.minecraft.world.server.ServerWorld;

/**
 * This event is fired when all players are asleep and the time should be set to day.<br>
 *
 * setWakeUpTime(wakeUpTime) sets a new time that will be added to the dayTime.<br>
 */
public class SleepFinishedTimeEvent extends WorldEvent
{
    private long newTime;
    private final long minTime;

    public SleepFinishedTimeEvent(ServerWorld worldIn, long newTimeIn, long minTimeIn)
    {
        super(worldIn);
        this.newTime = newTimeIn;
        this.minTime = minTimeIn;
    }

    /**
     * @return the new time
     */
    public long getNewTime()
    {
        return newTime;
    }

    /**
     * Sets the new time which should be set when all players wake up
     * @param newTimeIn The new time at wakeup
     * @return {@code false} if newTimeIn was lower than current time
     */
    public boolean setTimeAddition(long newTimeIn)
    {
        if (minTime > newTimeIn)
            return false;
        this.newTime = newTimeIn;
        return true;
    }
}
