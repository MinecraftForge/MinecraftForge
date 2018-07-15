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

package net.minecraftforge.server;

import java.lang.ref.WeakReference;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.server.timings.TimeTracker;

/**
 * @deprecated To be removed in 1.13 - Implementation has been moved
 * @see net.minecraftforge.server.timings.TimeTracker
 */
@Deprecated
public class ForgeTimeTracker {
    /**
     * @deprecated To be removed in 1.13 - Implementation has been moved
     * @see net.minecraftforge.server.timings.TimeTracker
     */
    @Deprecated
    public static boolean tileEntityTracking;

    /**
     * @deprecated To be removed in 1.13 - Implementation has been moved
     * @see net.minecraftforge.server.timings.TimeTracker
     */
    @Deprecated
    public static int tileEntityTrackingDuration;

    /**
     * @deprecated To be removed in 1.13 - Implementation has been moved
     * @see net.minecraftforge.server.timings.TimeTracker
     */
    @Deprecated
    public static long tileEntityTrackingTime;

    private Map<TileEntity,int[]> tileEntityTimings;
    private WeakReference<TileEntity> tile;

    private static final ForgeTimeTracker INSTANCE = new ForgeTimeTracker();

    /* not implemented
    private WeakReference<Entity> entity;
    private Map<Entity,int[]> entityTimings;
    */

    private long timing;

    private ForgeTimeTracker()
    {
        MapMaker mm = new MapMaker();
        mm.weakKeys();
        tileEntityTimings = mm.makeMap();
        //entityTimings = mm.makeMap();
    }


    private void trackTileStart(TileEntity tileEntity, long nanoTime)
    {
        if (tileEntityTrackingTime == 0)
        {
            tileEntityTrackingTime = nanoTime;
        }
        else if (tileEntityTrackingTime + tileEntityTrackingDuration < nanoTime)
        {
            tileEntityTracking = false;
            tileEntityTrackingTime = 0;

            return;
        }
        tile = new WeakReference<TileEntity>(tileEntity);
        timing = nanoTime;
    }


    private void trackTileEnd(TileEntity tileEntity, long nanoTime)
    {
        if (tile == null || tile.get() != tileEntity)
        {
            tile = null;
            // race, exit
            return;
        }
        int[] timings = tileEntityTimings.computeIfAbsent(tileEntity, k -> new int[101]);
        int idx = timings[100] = (timings[100] + 1) % 100;
        timings[idx] = (int) (nanoTime - timing);
    }

    /**
     * @deprecated To be removed in 1.13 - Implementation has been moved
     * @see TimeTracker#getTimingData()
     */
    @Deprecated
    public static ImmutableMap<TileEntity,int[]> getTileTimings()
    {
        return INSTANCE.buildImmutableTileEntityTimingMap();
    }

    private ImmutableMap<TileEntity, int[]> buildImmutableTileEntityTimingMap()
    {
        ImmutableMap.Builder<TileEntity, int[]> builder = new ImmutableMap.Builder<>();
        TimeTracker.TILE_ENTITY_UPDATE.getTimingData().stream()
                .filter(t -> t.getObject().get() != null).forEach(e -> builder.put(e.getObject().get(), e.getRawTimingData()));
        return builder.build();
    }

    /**
     * @deprecated To be removed in 1.13 - Implementation has been moved
     * @see TimeTracker#trackStart(Object)
     */
    @Deprecated
    public static void trackStart(TileEntity tileEntity)
    {
        TimeTracker.TILE_ENTITY_UPDATE.trackStart(tileEntity);
    }

    /**
     * @deprecated To be removed in 1.13 - Implementation has been moved
     * @see TimeTracker#trackEnd(Object)
     */
    @Deprecated
    public static void trackEnd(TileEntity tileEntity)
    {
        TimeTracker.TILE_ENTITY_UPDATE.trackEnd(tileEntity);
    }

    /**
     * @deprecated To be removed in 1.13 - Implementation has been moved
     * @see TimeTracker#trackStart(Object)
     */
    @Deprecated
    public static void trackStart(Entity par1Entity)
    {
    }

    /**
     * @deprecated To be removed in 1.13 - Implementation has been moved
     * @see TimeTracker#trackEnd(Object)
     */
    @Deprecated
    public static void trackEnd(Entity par1Entity)
    {

    }

}
