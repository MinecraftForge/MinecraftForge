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

package net.minecraftforge.server.timings;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * ForgeTimings aggregates timings data collected by {@link TimeTracker} for an Object
 * and performs operations for interpretation of the data.
 *
 * @param <T>
 */
public class ForgeTimings<T>
{

    private WeakReference<T> object;

    private int[] rawTimingData;

    public ForgeTimings(T object, int[] rawTimingData)
    {
        this.object = new WeakReference<T>(object);
        this.rawTimingData = rawTimingData;
    }

    /**
     * Retrieves the object that the timings are for
     *
     * @return The object
     */
    public WeakReference<T> getObject()
    {
        return object;
    }


    /**
     * Averages the raw timings data collected
     *
     * @return An average of the raw timing data
     */
    public double getAverageTimings()
    {
        double sum = 0.0;

        for (int data : rawTimingData)
        {
            sum += data;
        }

        return sum / rawTimingData.length;
    }
}
