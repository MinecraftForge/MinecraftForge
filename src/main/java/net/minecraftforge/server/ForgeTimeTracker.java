package net.minecraftforge.server;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.MapMaker;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class ForgeTimeTracker {
    public static boolean tileEntityTracking;
    public static int tileEntityTrackingDuration;
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
        int[] timings = tileEntityTimings.get(tileEntity);
        if (timings == null)
        {
            timings = new int[101];
            tileEntityTimings.put(tileEntity, timings);
        }
        int idx = timings[100] = (timings[100] + 1) % 100;
        timings[idx] = (int) (nanoTime - timing);
    }

    public static ImmutableMap<TileEntity,int[]> getTileTimings()
    {
        return INSTANCE.buildImmutableTileEntityTimingMap();
    }
    
    private ImmutableMap<TileEntity, int[]> buildImmutableTileEntityTimingMap()
    {
        Builder<TileEntity, int[]> builder = ImmutableMap.<TileEntity,int[]>builder();
        for (Entry<TileEntity, int[]> entry : tileEntityTimings.entrySet())
        {
            builder.put(entry.getKey(), Arrays.copyOfRange(entry.getValue(), 0, 100));
        }
        return builder.build();
    }


    public static void trackStart(TileEntity tileEntity)
    {
        if (!tileEntityTracking) return;
        INSTANCE.trackTileStart(tileEntity, System.nanoTime());
    }

    public static void trackEnd(TileEntity tileEntity)
    {
        if (!tileEntityTracking) return;
        INSTANCE.trackTileEnd(tileEntity, System.nanoTime());
    }

    public static void trackStart(Entity par1Entity)
    {
        
    }

    public static void trackEnd(Entity par1Entity)
    {
        
    }

}
