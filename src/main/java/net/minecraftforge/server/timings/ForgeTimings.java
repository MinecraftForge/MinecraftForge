/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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

    /**
     * Returns a copy of the raw timings data collected by the tracker
     * @return The raw timing data
     * @deprecated Added for compatibility, remove in 1.13
     */
    public int[] getRawTimingData(){
        return Arrays.copyOfRange(rawTimingData, 0, rawTimingData.length);
    }
}
