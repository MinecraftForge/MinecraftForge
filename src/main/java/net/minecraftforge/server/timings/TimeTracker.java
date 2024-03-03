/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.timings;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * A class to assist in the collection of data to measure the update times of ticking objects {currently Tile Entities and Entities}
 *
 * @param <T>
 */
public class TimeTracker<T>
{

    /**
     * A tracker for timing tile entity update
     */
    public static final TimeTracker<BlockEntity> BLOCK_ENTITY_UPDATE = new TimeTracker<>();
    /**
     * A tracker for timing entity updates
     */
    public static final TimeTracker<Entity> ENTITY_UPDATE = new TimeTracker<>();

    private boolean enabled;
    private int trackingDuration;
    private Map<T, int[]> timings = new MapMaker().weakKeys().makeMap();
    private WeakReference<T> currentlyTracking;
    private long trackTime;
    private long timing;

    /**
     * Returns the timings data recorded by the tracker
     *
     * @return An immutable list of timings data collected by this tracker
     */
    public ImmutableList<ForgeTimings<T>> getTimingData()
    {
        ImmutableList.Builder<ForgeTimings<T>> builder = ImmutableList.builder();

        for (Map.Entry<T, int[]> entry : timings.entrySet())
        {
            builder.add(new ForgeTimings<>(entry.getKey(), Arrays.copyOfRange(entry.getValue(), 0, 99)));
        }
        return builder.build();
    }

    /**
     * Resets the tracker (clears timings and stops any in-progress timings)
     */
    public void reset()
    {
        enabled = false;
        trackTime = 0;
        timings.clear();
    }

    /**
     * Ends the timing of the currently tracking object
     *
     * @param tracking The object to stop timing
     */
    public void trackEnd(T tracking)
    {
        if (!enabled)
            return;
        this.trackEnd(tracking, System.nanoTime());
    }

    /**
     * Starts recording tracking data for the given duration in seconds
     *
     * @param duration The duration for the time to track
     */
    public void enable(int duration)
    {
        this.trackingDuration = duration;
        this.enabled = true;
    }

    /**
     * Starts timing of the provided object
     *
     * @param toTrack The object to start timing
     */
    public void trackStart(T toTrack)
    {
        if (!enabled)
            return;
        this.trackStart(toTrack, System.nanoTime());
    }

    private void trackEnd(T object, long nanoTime)
    {
        if (currentlyTracking == null || currentlyTracking.get() != object)
        {
            currentlyTracking = null;
            return;
        }
        int[] timings = this.timings.computeIfAbsent(object, k -> new int[101]);
        int idx = timings[100] = (timings[100] + 1) % 100;
        timings[idx] = (int) (nanoTime - timing);
    }

    private void trackStart(T toTrack, long nanoTime)
    {
        if (trackTime == 0)
        {
            trackTime = nanoTime;
        }
        else if (trackTime + TimeUnit.NANOSECONDS.convert(trackingDuration, TimeUnit.SECONDS) < nanoTime)
        {
            enabled = false;
            trackTime = 0;
        }

        currentlyTracking = new WeakReference<>(toTrack);
        timing = nanoTime;
    }
}
