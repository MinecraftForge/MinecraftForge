package za.co.mcportcentral;

import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.map.hash.TObjectLongHashMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.management.MBeanServer;

import net.minecraft.entity.Entity;
import net.minecraft.logging.ILogAgent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;

import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.spigotmc.SpigotConfig;

import com.google.gson.stream.JsonWriter;

public class MCPCHooks
{
    // Some mods such as Twilight Forest listen for specific events as their WorldProvider loads to hotload its dimension. This prevents this from happening so MV can create worlds using the same provider without issue.
    public static boolean craftWorldLoading = false;
    public static boolean overrideTileTicks = false;
    public static int tickingDimension = 0;
    public static ChunkCoordIntPair tickingChunk = null;
    public static ILogAgent logAgent = MinecraftServer.getServer().getLogAgent();

    private static TObjectLongHashMap<CollisionWarning> recentWarnings = new TObjectLongHashMap<CollisionWarning>();

    public static void logInfo(String msg, Object... args)
    {
        logAgent.logInfo(MessageFormat.format(msg, args));
    }

    public static void logWarning(String msg, Object... args)
    {
        logAgent.logWarning(MessageFormat.format(msg, args));
    }

    public static void logSevere(String msg, Object... args)
    {
        logAgent.logSevere(MessageFormat.format(msg, args));
    }

    public static void logSevereException(Throwable throwable, String msg, Object... args)
    {
        logAgent.logSevereException(MessageFormat.format(msg, args), throwable);
    }

    public static void logStack()
    {
        if (MCPCConfig.Setting.logWithStackTraces.getValue())
        {
            Throwable ex = new Throwable();
            ex.fillInStackTrace();
            ex.printStackTrace();
        }
    }

    public static void logEntityDeath(Entity entity)
    {
        if (MCPCConfig.Setting.entityDeathLogging.getValue())
        {
            logInfo("Dim: {0} setDead(): {1}", entity.worldObj.provider.dimensionId, entity);
            logStack();
        }
    }

    public static void logEntityDespawn(Entity entity, String reason)
    {
        if (MCPCConfig.Setting.entityDespawnLogging.getValue())
        {
            logInfo("Dim: {0} Despawning ({1}): {2}", entity.worldObj.provider.dimensionId, reason, entity);
            logInfo("Chunk Is Active: {0}", entity.worldObj.inActiveChunk(entity));
            logStack();
        }
    }

    public static void logEntitySpawn(World world, Entity entity, SpawnReason spawnReason)
    {
        if (MCPCConfig.Setting.entitySpawnLogging.getValue())
        {
            logInfo("Dim: {0} Spawning ({1}): {2}", world.provider.dimensionId, spawnReason, entity);
            logInfo("Dim: {0} Entities Last Tick: {1}", world.provider.dimensionId, world.entitiesTicked);
            logInfo("Dim: {0} Tiles Last Tick: {1}", world.provider.dimensionId, world.tilesTicked);
            logInfo("Chunk Is Active: {0}", world.inActiveChunk(entity));
            logStack();
        }
    }

    public static void logChunkLoad(ChunkProviderServer provider, String msg, int x, int z, boolean logLoadOnRequest)
    {
        if (MCPCConfig.Setting.chunkLoadLogging.getValue())
        {
            logInfo("{0} Chunk At [{1}] ({2}, {3})", msg, provider.worldObj.provider.dimensionId, x, z);
            if (logLoadOnRequest)
            {
                logLoadOnRequest(provider, x, z);
            }
            logStack();
        }
    }

    public static void logChunkUnload(ChunkProviderServer provider, int x, int z, String msg)
    {
        if (MCPCConfig.Setting.chunkUnloadLogging.getValue())
        {
            logInfo("{0} [{1}] ({2}, {3})", msg, provider.worldObj.provider.dimensionId, x, z);
            long currentTick = MinecraftServer.getServer().getTickCounter();
            long lastAccessed = provider.lastAccessed(x, z);
            long diff = currentTick - lastAccessed;
            logInfo(" Last accessed: {0, number} Current Tick: {1, number} [{2, number}]", lastAccessed, currentTick, diff);
        }
    }

    private static void logLoadOnRequest(ChunkProviderServer provider, int x, int z)
    {
        long currentTick = MinecraftServer.getServer().getTickCounter();
        long lastAccessed = provider.lastAccessed(x, z);
        long diff = currentTick - lastAccessed;
        logInfo(" Last accessed: {0, number} Current Tick: {1, number} [{2, number}]", lastAccessed, currentTick, diff);
        logInfo(" Finding Spawn Point: {0}", provider.worldObj.findingSpawnPoint);
        logInfo(" Load chunk on request: {0}", provider.loadChunkOnProvideRequest);
        logInfo(" Calling Forge Tick: {0}", MinecraftServer.callingForgeTick);
        logInfo(" Load chunk on forge tick: {0}", MCPCConfig.Setting.loadChunkOnForgeTick.getValue());
        long providerTickDiff = currentTick - provider.initialTick;
        if (providerTickDiff <= 100)
        {
            logInfo(" Current Tick - Initial Tick: {0, number}", providerTickDiff);
        }
    }

    public static boolean checkBoundingBoxSize(Entity entity, AxisAlignedBB aabb)
    {
        int logSize = MCPCConfig.Setting.largeBoundingBoxLogSize.getValue();
        if (logSize <= 0) return false;
        int x = MathHelper.floor_double(aabb.minX);
        int x1 = MathHelper.floor_double(aabb.maxX + 1.0D);
        int y = MathHelper.floor_double(aabb.minY);
        int y1 = MathHelper.floor_double(aabb.maxY + 1.0D);
        int z = MathHelper.floor_double(aabb.minZ);
        int z1 = MathHelper.floor_double(aabb.maxZ + 1.0D);
        
        int size = Math.abs(x1-x) * Math.abs(y1-y) * Math.abs(z1-z);
        if (size > MCPCConfig.Setting.largeBoundingBoxLogSize.getValue())
        {
            logWarning("Entity being removed for bounding box restrictions");
            logWarning("BB Size: {0} > {1} avg edge: {2}", size, logSize, aabb.getAverageEdgeLength());
            logWarning("Motion: ({0}, {1}, {2})", entity.motionX, entity.motionY, entity.motionZ);
            logWarning("Calculated bounding box: {0}", aabb);
            logWarning("Entity bounding box: {0}", entity.getBoundingBox());
            logWarning("Entity: {0}", entity);
            NBTTagCompound tag = new NBTTagCompound();
            entity.writeToNBT(tag);
            logWarning("Entity NBT: {0}", tag);
            logStack();
            entity.setDead();
            return true;
        }
        return false;
    }
    
    public static void logEntitySpeed(Entity entity, double x, double y, double z)
    {
        if (MCPCConfig.Setting.entitySpeedWarnSize.getValue() > 0)
        {
            double length = Math.sqrt(x*x + y*y + z*z);
            if (length > 5)
            {
                za.co.mcportcentral.MCPCHooks.logInfo("Fast moving entity - Distance: {0}", length);
                za.co.mcportcentral.MCPCHooks.logInfo("Move offset: ({0}, {1}, {2})", x, y, z);
                za.co.mcportcentral.MCPCHooks.logInfo("Motion: ({0}, {1}, {2})", entity.motionX, entity.motionY, entity.motionZ);
                za.co.mcportcentral.MCPCHooks.logInfo("Entity: {0}", entity);
                NBTTagCompound tag = new NBTTagCompound();
                entity.writeToNBT(tag);
                za.co.mcportcentral.MCPCHooks.logInfo("Entity NBT: {0}", tag);
            }
        }
    }
    
    public static void logEntitySize(World world, Entity entity, List list)
    {
        if (!MCPCConfig.Setting.logEntityCollisionChecks.getValue()) return;
        long largeCountLogSize = MCPCConfig.Setting.largeCollisionLogSize.getValue();
        if (largeCountLogSize > 0 && world.entitiesTicked > largeCountLogSize)
        {
            logWarning("Entity size > {0, number} at: {1}", largeCountLogSize, entity);
        }
        if (list == null) return;
        long largeCollisionLogSize = MCPCConfig.Setting.largeCollisionLogSize.getValue();
        if (largeCollisionLogSize > 0 &&
                (MinecraftServer.getServer().getTickCounter() % 10) == 0 &&
                list.size() >= largeCollisionLogSize)
        {
            MCPCHooks.CollisionWarning warning = new MCPCHooks.CollisionWarning(world, entity);
            if (recentWarnings.contains(warning))
            {
                long lastWarned = recentWarnings.get(warning);
                if ((MinecraftServer.getSystemTimeMillis() - lastWarned) < 30000) return;
            }
            recentWarnings.put(warning, System.currentTimeMillis());
            logWarning("Entity collision > {0, number} at: {1}", largeCollisionLogSize, entity);
        }
    }

    private static class CollisionWarning
    {
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
            if (!(otherObj instanceof CollisionWarning) || (otherObj == null)) return false;
            CollisionWarning other = (CollisionWarning) otherObj;
            return (other.dimensionId == this.dimensionId) && other.chunkCoords.equals(this.chunkCoords);
        }

        @Override
        public int hashCode()
        {
            return chunkCoords.hashCode() + dimensionId;
        }
    }

    public static int getTileTickInterval(TileEntity tileEntity)
    {
        String path = "tick-intervals.tiles.update." + tileEntity.getClass().getName().replace(".", "-");
        // if (!MCPCConfig.isSet(path)) return tileEntity.canUpdate() ? 1 : 0;
        return MCPCConfig.getInt(path, tileEntity.canUpdate() ? 1 : 0);
    }

    public static boolean canUpdate(TileEntity tileEntity)
    {
        if (tileEntity == null || !tileEntity.canUpdate()) return false; // quick exit
        return MCPCHooks.getTileTickInterval(tileEntity) != 0;
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
                    if ((entity.boundingBox != null) && logAll)
                    {
                        ChunkCoordinates coords = new ChunkCoordinates((int)Math.floor(entity.posX), (int)Math.floor(entity.posY), (int)Math.floor(entity.posZ));
                        if (!collidingCoords.contains(coords))
                        {
                            collidingCoords.add(coords);
                            int size = entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(1, 1, 1)).size();
                            if (size < 5)
                            {
                                continue;
                            }
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
        Collections.sort(sortedCoords, new Comparator<T>()
        {
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
            if ((max > 0) && (i++ > max))
            {
                break;
            }
            if (map.get(key) < 5)
            {
                continue;
            }
            writer.beginObject();
            writer.name("key").value(key.toString());
            writer.name("count").value(map.get(key));
            writer.endObject();
        }
        writer.endArray();
    }

    public static void dumpHeap(File file, boolean live)
    {
        try
        {
            if (file.getParentFile() != null)
            {
                file.getParentFile().mkdirs();
            }
            Class clazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            Object hotspotMBean = ManagementFactory.newPlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", clazz);
            Method m = clazz.getMethod("dumpHeap", String.class, boolean.class);
            m.invoke(hotspotMBean, file.getPath(), live);
        }
        catch (Throwable t)
        {
            logSevereException(t, "Could not write heap to {0}", file);
        }
    }
}
