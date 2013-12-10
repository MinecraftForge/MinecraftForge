package za.co.mcportcentral;

import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.map.hash.TObjectLongHashMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import com.google.gson.stream.JsonWriter;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;
import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

public class MCPCHooks {
    public static int tickingDimension = 0;
    public static ChunkCoordIntPair tickingChunk = null;

    public static void logStack()
    {
        if (MCPCConfig.Setting.logWithStackTraces.getValue())
        {
            Throwable ex = new Throwable();
            ex.fillInStackTrace();
            ex.printStackTrace();
        }
    }

    public static void logEntityDeath(String msg)
    {
        if (MCPCConfig.Setting.entityDeathLogging.getValue())
        {
            MinecraftServer.getServer().logInfo(msg);
            logStack();
        }
    }

    public static void logEntityDespawn(String msg)
    {
        if (MCPCConfig.Setting.entityDespawnLogging.getValue())
        {
            MinecraftServer.getServer().getLogAgent().logInfo(msg);
            logStack();
        }
    }

    public static void logEntitySpawn(World world, Entity entity, SpawnReason spawnReason)
    {
        if (MCPCConfig.Setting.entitySpawnLogging.getValue())
        {
            MinecraftServer.getServer().getLogAgent().logInfo("Dim: " + world.provider.dimensionId + " Spawning (" + spawnReason + "): " + entity);
            logStack();
        }
    }

    public static void logChunkLoad(ChunkProviderServer provider, String msg, int x, int z, boolean logLoadOnRequest)
    {
        if (MCPCConfig.Setting.chunkLoadLogging.getValue())
        {
            MinecraftServer.getServer().getLogAgent().logInfo(msg + " Chunk At " + x + ", " + z + " Dim: " + provider.worldObj.provider.dimensionId);
            if (logLoadOnRequest)
            {
                logLoadOnRequest(provider, x, z);
            }
            logStack();
        }
    }

    private static void logLoadOnRequest(ChunkProviderServer provider, int x, int z)
    {
        long currentTick = MinecraftServer.getServer().getTickCounter();
        long lastAccessed = provider.lastAccessed(x, z);
        long diff = currentTick - lastAccessed;
        MinecraftServer.getServer().logInfo(" Last accessed: " + lastAccessed + " Current Tick: " + currentTick + " Diff: " + diff + " Server tick: " + MinecraftServer.getServer().getTickCounter());
        MinecraftServer.getServer().logInfo(" Finding Spawn Point: " + provider.worldObj.findingSpawnPoint);
        MinecraftServer.getServer().logInfo(" Load chunk on request: " + provider.loadChunkOnProvideRequest);
        MinecraftServer.getServer().logInfo(" Calling Forge Tick: " + MinecraftServer.callingForgeTick);
        MinecraftServer.getServer().logInfo(" Load chunk on forge tick: " + MCPCConfig.Setting.loadChunkOnForgeTick.getValue());
        MinecraftServer.getServer().logInfo(" Current Tick - Initial Tick: " + (MinecraftServer.currentTick - provider.initialTick));
    }

    public static void logChunkUnload(ChunkProviderServer provider, int x, int z, String msg)
    {
        if (MCPCConfig.Setting.chunkUnloadLogging.getValue())
        {
            MinecraftServer.getServer().getLogAgent().logInfo(msg + " " + x + ", " + z + " Dim: " + provider.worldObj.provider.dimensionId);
            long currentTick = MinecraftServer.getServer().getTickCounter();
            long lastAccessed = provider.lastAccessed(x, z);
            long diff = currentTick - lastAccessed;
            MinecraftServer.getServer().logInfo("Last accessed: " + lastAccessed + " Current Tick: " + currentTick + " Diff: " + diff + " Server tick: " + MinecraftServer.getServer().getTickCounter());
        }
    }

    public static void logEntitySize(World world, Entity entity, List list)
    {
        if (list == null)
        {
            return;
        }
        MinecraftServer.getServer();
        if (MinecraftServer.currentTick % 10 == 0 && MCPCConfig.Setting.largeCollisionLogSize.getValue() > 0
                && list.size() >= MCPCConfig.Setting.largeCollisionLogSize.getValue())
        {
            MCPCHooks.CollisionWarning warning = new MCPCHooks.CollisionWarning(world, entity);
            if (recentWarnings.contains(warning))
            {
                long lastWarned = recentWarnings.get(warning);
                if (System.currentTimeMillis() - lastWarned < 30000)
                {
                    return;
                }
            }
            recentWarnings.put(warning, System.currentTimeMillis());
            MinecraftServer.getServer().logWarning("Entity collision > " + MCPCConfig.Setting.largeCollisionLogSize.getValue() + " at: " + entity);
        }
    }

    private static TObjectLongHashMap<CollisionWarning> recentWarnings = new TObjectLongHashMap<CollisionWarning>();

    private static class CollisionWarning {
        public ChunkCoordinates chunkCoords;
        public int dimensionId;

        public CollisionWarning(World world, Entity entity)
        {
            this.dimensionId = world.provider.dimensionId;
            this.chunkCoords = new ChunkCoordinates(entity.chunkCoordX, entity.chunkCoordY, entity.chunkCoordZ);
        }

        @Override
        public boolean equals(Object otherObj)
        {
            if (!(otherObj instanceof CollisionWarning) || otherObj == null)
            {
                return false;
            }
            CollisionWarning other = (CollisionWarning) otherObj;
            return other.dimensionId == this.dimensionId && other.chunkCoords.equals(this.chunkCoords);
        }

        @Override
        public int hashCode()
        {
            return chunkCoords.hashCode() + dimensionId;
        }
    }

    private static int getTileTickInterval(TileEntity tileEntity)
    {
        String path = "tick-intervals.tiles.update." + tileEntity.getClass().getName().replace(".", "-");
        //if (!MCPCConfig.isSet(path)) return tileEntity.canUpdate() ? 1 : 0;
        return MCPCConfig.getInt(path, tileEntity.canUpdate() ? 1 : 0);
    }
    
    public static boolean canUpdate(TileEntity tileEntity)
    {
        if (tileEntity == null || !tileEntity.canUpdate()) return false; // quick exit
        return MCPCHooks.getTileTickInterval(tileEntity) != 0;
    }
    
    public static void updateEntity(TileEntity tileEntity)
    {
        int interval = getTileTickInterval(tileEntity);
        if (MinecraftServer.getServer().getTickCounter() % interval == 0)
        {
            tileEntity.updateEntity();
        }
    }

    public static void writeChunks(File file, boolean logAll)
    {
        try
        {
            if (file.getParentFile() != null)
            {
                file.getParentFile().mkdirs();
            }

            FileWriter fileWriter = new FileWriter(file);
            JsonWriter writer = new JsonWriter(fileWriter);
            writer.setIndent("  ");
            writer.beginArray();

            for (net.minecraft.world.WorldServer world : MinecraftServer.getServer().worlds)
            {
                writer.beginObject();
                writer.name("name").value(world.getWorld().getName());
                writer.name("dimensionId").value(world.provider.dimensionId);
                writer.name("players").value(world.playerEntities.size());
                writer.name("lastBBTime").value(world.lastBoundingBoxTime);
                writer.name("avgBBTime").value(String.format("%.2f", (1d * world.totalBoundingBoxTime) / world.totalBoundingBoxCalls));
                writer.name("loadedChunks").value(world.theChunkProviderServer.loadedChunkHashMap.size());
                writer.name("activeChunks").value(world.activeChunkSet.size());
                writer.name("entities").value(world.loadedEntityList.size());
                writer.name("tiles").value(world.loadedTileEntityList.size());

                TObjectIntHashMap<ChunkCoordIntPair> chunkEntityCounts = new TObjectIntHashMap<ChunkCoordIntPair>();
                TObjectIntHashMap<Class> classEntityCounts = new TObjectIntHashMap<Class>();
                TObjectIntHashMap<Entity> entityCollisionCounts = new TObjectIntHashMap<Entity>();
                Set<ChunkCoordinates> collidingCoords = new HashSet<ChunkCoordinates>();
                for (int i = 0; i < world.loadedEntityList.size(); i++)
                {
                    Entity entity = (Entity) world.loadedEntityList.get(i);
                    ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair((int) entity.posX >> 4, (int) entity.posZ >> 4);
                    chunkEntityCounts.adjustOrPutValue(chunkCoords, 1, 1);
                    classEntityCounts.adjustOrPutValue(entity.getClass(), 1, 1);
                    if (entity.boundingBox != null && logAll)
                    {
                        ChunkCoordinates coords = new ChunkCoordinates(entity.chunkCoordX, entity.chunkCoordY, entity.chunkCoordZ);
                        if (!collidingCoords.contains(coords))
                        {
                            collidingCoords.add(coords);
                            int size = entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(1, 1, 1)).size();
                            if (size < 5) continue;
                            entityCollisionCounts.put(entity, size);
                        }
                    }
                }

                TObjectIntHashMap<ChunkCoordIntPair> chunkTileCounts = new TObjectIntHashMap<ChunkCoordIntPair>();
                TObjectIntHashMap<Class> classTileCounts = new TObjectIntHashMap<Class>();
                writer.name("tiles").beginArray();
                for (int i = 0; i < world.loadedTileEntityList.size(); i++)
                {
                    TileEntity tile = (TileEntity) world.loadedTileEntityList.get(i);
                    if (logAll)
                    {
                        writer.beginObject();
                        writer.name("type").value(tile.getClass().toString());
                        writer.name("x").value(tile.xCoord);
                        writer.name("y").value(tile.yCoord);
                        writer.name("z").value(tile.zCoord);
                        writer.name("isInvalid").value(tile.isInvalid());
                        writer.name("canUpdate").value(tile.canUpdate());
                        writer.name("blockId").value("" + tile.getBlockType());
                        writer.endObject();
                    }
                    ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(tile.xCoord >> 4, tile.zCoord >> 4);
                    chunkTileCounts.adjustOrPutValue(chunkCoords, 1, 1);
                    classTileCounts.adjustOrPutValue(tile.getClass(), 1, 1);
                }
                writer.endArray();

                if (logAll)
                {
                    writeChunkCounts(writer, "topEntityColliders", entityCollisionCounts, 20);
                }
                writeChunkCounts(writer, "entitiesByClass", classEntityCounts);
                writeChunkCounts(writer, "entitiesByChunk", chunkEntityCounts);

                writeChunkCounts(writer, "tilesByClass", classTileCounts);
                writeChunkCounts(writer, "tilesByChunk", chunkTileCounts);

                writer.endObject(); // Dimension
            }
            writer.endArray(); // Dimensions
            writer.close();
            fileWriter.close();
        }
        catch (Throwable throwable)
        {
            MinecraftServer.getServer().getLogAgent().logSevereException("Could not save chunk info report to " + file, throwable);
        }
    }

    private static <T> void writeChunkCounts(JsonWriter writer, String name, final TObjectIntHashMap<T> map) throws IOException
    {
        writeChunkCounts(writer, name, map, 0);
    }

    private static <T> void writeChunkCounts(JsonWriter writer, String name, final TObjectIntHashMap<T> map, int max) throws IOException
    {
        List<T> sortedCoords = new ArrayList<T>(map.keySet());
        Collections.sort(sortedCoords, new Comparator<T>() {
            @Override
            public int compare(T s1, T s2)
            {
                return map.get(s2) - map.get(s1);
            }
        });

        int i = 0;
        writer.name(name).beginArray();
        for (T key : sortedCoords)
        {
            if (max > 0 && i++ > max) break;
            if (map.get(key) < 5) continue;
            writer.beginObject();
            writer.name("key").value(key.toString());
            writer.name("count").value(map.get(key));
            writer.endObject();
        }
        writer.endArray();
    }

    public static class HeapDump {
        // This is the name of the HotSpot Diagnostic MBean
        private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";

        // field to store the hotspot diagnostic MBean
        private static volatile Object hotspotMBean;

        /**
         * Call this method from your application whenever you want to dump the
         * heap snapshot into a file.
         * 
         * @param fileName
         *            name of the heap dump file
         * @param live
         *            flag that tells whether to dump only the live objects
         */
        public static void dumpHeap(File file, boolean live)
        {
            try
            {
                if (file.getParentFile() != null)
                {
                    file.getParentFile().mkdirs();
                }
                // initialize hotspot diagnostic MBean
                initHotspotMBean();
                try
                {
                    Class clazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
                    Method m = clazz.getMethod("dumpHeap", String.class, boolean.class);
                    m.invoke(hotspotMBean, file.getPath(), live);
                }
                catch (RuntimeException re)
                {
                    throw re;
                }
                catch (Exception exp)
                {
                    throw new RuntimeException(exp);
                }
            }
            catch (Throwable t)
            {
                MinecraftServer.getServer().getLogAgent().logSevereException("Could not write heap to " + file, t);
            }
        }

        // initialize the hotspot diagnostic MBean field
        private static void initHotspotMBean()
        {
            if (hotspotMBean == null)
            {
                synchronized (HeapDump.class)
                {
                    if (hotspotMBean == null)
                    {
                        hotspotMBean = getHotspotMBean();
                    }
                }
            }
        }

        // get the hotspot diagnostic MBean from the
        // platform MBean server
        private static Object getHotspotMBean()
        {
            try
            {
                Class clazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
                MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                Object bean = ManagementFactory.newPlatformMXBeanProxy(server, HOTSPOT_BEAN_NAME, clazz);
                return bean;
            }
            catch (RuntimeException re)
            {
                throw re;
            }
            catch (Exception exp)
            {
                throw new RuntimeException(exp);
            }
        }
    }
}
