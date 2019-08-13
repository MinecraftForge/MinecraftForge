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
 * This event is fired when all players are asleep and the time should be set to daytime.<br>
 *
 * This event is cancelable. {@link Cancelable}<br>
 *
 * setWakeUpTime(wakeUpTime) sets a new time that will be set when all players wakeup.<br>
 */
@Cancelable
public class SleepFinishedTimeEvent extends WorldEvent
{
    private long wakeUpTime;

    public SleepFinishedTimeEvent(ServerWorld worldIn, long wakeUpTimeIn)
    {
        super(worldIn);
        this.wakeUpTime = wakeUpTimeIn;
    }

    /**
     * @returns the wakeup time
     */
    public long getWakeUpTime()
    {
        return wakeUpTime;
    }

    /**
     * Sets the new time which should be set when all players wake up
     * @param wakeUpTimeIn The new time at wakeup
     */
    public void setWakeUpTime(long wakeUpTimeIn)
    {
        this.wakeUpTime = wakeUpTimeIn;
    }
}
