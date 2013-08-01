package org.bukkit.craftbukkit;

import org.spigotmc.CustomTimingsHandler;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class SpigotTimings {

    public static final CustomTimingsHandler serverTickTimer = new CustomTimingsHandler("** Full Server Tick");
    public static final CustomTimingsHandler playerListTimer = new CustomTimingsHandler("Player List");
    public static final CustomTimingsHandler connectionTimer = new CustomTimingsHandler("Player Tick");
    public static final CustomTimingsHandler tickablesTimer = new CustomTimingsHandler("Tickables");
    public static final CustomTimingsHandler schedulerTimer = new CustomTimingsHandler("Scheduler");
    public static final CustomTimingsHandler chunkIOTickTimer = new CustomTimingsHandler("ChunkIOTick");
    public static final CustomTimingsHandler syncChunkLoadTimer = new CustomTimingsHandler("syncChunkLoad");

    public static final CustomTimingsHandler entityMoveTimer = new CustomTimingsHandler("** entityMove");
    public static final CustomTimingsHandler tickEntityTimer = new CustomTimingsHandler("** tickEntity");
    public static final CustomTimingsHandler activatedEntityTimer = new CustomTimingsHandler("** activatedTickEntity");
    public static final CustomTimingsHandler tickTileEntityTimer = new CustomTimingsHandler("** tickTileEntity");

    public static final CustomTimingsHandler timerEntityBaseTick = new CustomTimingsHandler("** livingEntityBaseTick");
    public static final CustomTimingsHandler timerEntityAI = new CustomTimingsHandler("** livingEntityAI");
    public static final CustomTimingsHandler timerEntityAICollision = new CustomTimingsHandler("** livingEntityAICollision");
    public static final CustomTimingsHandler timerEntityAIMove = new CustomTimingsHandler("** livingEntityAIMove");
    public static final CustomTimingsHandler timerEntityTickRest = new CustomTimingsHandler("** livingEntityTickRest");

    public static final CustomTimingsHandler playerCommandTimer = new CustomTimingsHandler("** playerCommand");

    public static final CustomTimingsHandler entityActivationCheckTimer = new CustomTimingsHandler("entityActivationCheck");
    public static final CustomTimingsHandler checkIfActiveTimer = new CustomTimingsHandler("** checkIfActive");

    public static final HashMap<String, CustomTimingsHandler> entityTypeTimingMap = new HashMap<String, CustomTimingsHandler>();
    public static final HashMap<String, CustomTimingsHandler> tileEntityTypeTimingMap = new HashMap<String, CustomTimingsHandler>();
    public static final HashMap<String, CustomTimingsHandler> pluginTaskTimingMap = new HashMap<String, CustomTimingsHandler>();

    /**
     * Gets a timer associated with a plugins tasks.
     * @param task
     * @param period
     * @return
     */
    public static CustomTimingsHandler getPluginTaskTimings(BukkitTask task, long period) {
        String plugin = task.getOwner().getDescription().getFullName();
        String name = "Task: " + plugin +" Id:";
        if (period > 0) {
            name += "(interval:" + period +")";
        } else {
            name += "(Single)";
        }
        CustomTimingsHandler result = pluginTaskTimingMap.get(name);
        if (result == null) {
            result = new CustomTimingsHandler(name);
            pluginTaskTimingMap.put(name, result);
        }
        return result;
    }

    /**
     * Get a named timer for the specified entity type to track type specific timings.
     * @param entity
     * @return
     */
    public static CustomTimingsHandler getEntityTimings(net.minecraft.entity.Entity entity) {
        String entityType = entity.getClass().getSimpleName();
        CustomTimingsHandler result = entityTypeTimingMap.get(entityType);
        if (result == null) {
            result = new CustomTimingsHandler("** tickEntity - " + entityType, activatedEntityTimer);
            entityTypeTimingMap.put(entityType, result);
        }
        return result;
    }

    /**
     * Get a named timer for the specified tile entity type to track type specific timings.
     * @param entity
     * @return
     */
    public static CustomTimingsHandler getTileEntityTimings(net.minecraft.tileentity.TileEntity entity) {
        String entityType = entity.getClass().getSimpleName();
        CustomTimingsHandler result = tileEntityTypeTimingMap.get(entityType);
        if (result == null) {
            result = new CustomTimingsHandler("** tickTileEntity - " + entityType, tickTileEntityTimer);
            tileEntityTypeTimingMap.put(entityType, result);
        }
        return result;
    }

    /**
     * Set of timers per world, to track world specific timings.
     */
    public static class WorldTimingsHandler {
        public final CustomTimingsHandler mobSpawn;
        public final CustomTimingsHandler doChunkUnload;
        public final CustomTimingsHandler doPortalForcer;
        public final CustomTimingsHandler doTickPending;
        public final CustomTimingsHandler doTickTiles;
        public final CustomTimingsHandler doVillages;
        public final CustomTimingsHandler doChunkMap;
        public final CustomTimingsHandler doChunkGC;
        public final CustomTimingsHandler doSounds;
        public final CustomTimingsHandler entityTick;
        public final CustomTimingsHandler tileEntityTick;
        public final CustomTimingsHandler tileEntityPending;
        public final CustomTimingsHandler tracker;

        public WorldTimingsHandler(net.minecraft.world.World server) {
            String name = server.worldInfo.getWorldName() +" - ";

            mobSpawn = new CustomTimingsHandler(name + "mobSpawn");
            doChunkUnload = new CustomTimingsHandler(name + "doChunkUnload");
            doTickPending = new CustomTimingsHandler(name + "doTickPending");
            doTickTiles = new CustomTimingsHandler(name + "doTickTiles");
            doVillages = new CustomTimingsHandler(name + "doVillages");
            doChunkMap = new CustomTimingsHandler(name + "doChunkMap");
            doSounds = new CustomTimingsHandler(name + "doSounds");
            doChunkGC = new CustomTimingsHandler(name + "doChunkGC");
            doPortalForcer = new CustomTimingsHandler(name + "doPortalForcer");
            entityTick = new CustomTimingsHandler(name + "entityTick");
            tileEntityTick = new CustomTimingsHandler(name + "tileEntityTick");
            tileEntityPending = new CustomTimingsHandler(name + "tileEntityPending");
            tracker = new CustomTimingsHandler(name + "tracker");
        }
    }
}
