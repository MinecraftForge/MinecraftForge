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

package net.minecraftforge.common;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multiset;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ServerMultiWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.StartupQuery;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class DimensionManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker DIMMGR = MarkerManager.getMarker("DIMS");

    private static final Int2ObjectMap<Data> dimensions = Int2ObjectMaps.synchronize(new Int2ObjectLinkedOpenHashMap<>());
    private static final IntSet unloadQueue = IntSets.synchronize(new IntLinkedOpenHashSet());
    private static final ConcurrentMap<World, World> weakWorldMap = new MapMaker().weakKeys().weakValues().makeMap();
    private static final Multiset<Integer> leakedWorlds = HashMultiset.create();

    /**
     * Configures if the dimension will stay loaded in memory even if all chunks are unloaded.
     *
     * @param dim The dimension
     * @param value True to keep loaded, false to allow unloading
     * @return The old value for this dimension.
     */
    public static boolean keepLoaded(DimensionType dim, boolean value)
    {
        Validate.notNull(dim, "Dimension type must not be null");
        Data data = getData(dim);
        boolean ret = data.keepLoaded;
        data.keepLoaded = value;
        return ret;
    }

    /**
     * Determines if the dimension will stay loaded in memory even if all chunks are unloaded.
     * @param dim The dimension
     * @return True if the dimension will stay loaded with no chunks loaded.
     */
    public static boolean keepLoaded(DimensionType dim)
    {
        Validate.notNull(dim, "Dimension type must not be null");
        Data data = dimensions.get(dim.getId());
        return data != null && data.keepLoaded;
    }

    /**
     * Retrieves the world from the server allowing for null return, and optionally resetting it's unload timer.
     *
     * @param server The server that controlls this world.
     * @param dim Dimension to load.
     * @param resetUnloadDelay True to reset the unload timer, which is a delay that is used to prevent constant world loading/unloading cycle.
     * @param forceLoad True to attempt to load the dimension if the server has it unloaded.
     * @return The world, null if unloaded and not loadable.
     */
    @Nullable
    public static ServerWorld getWorld(MinecraftServer server, DimensionType dim, boolean resetUnloadDelay, boolean forceLoad)
    {
        Validate.notNull(server, "Must provide server when creating world");
        Validate.notNull(dim, "Dimension type must not be null");

        // If we're in the early stages of loading, we need to return null so CommandSource can work properly for command function
        if (StartupQuery.pendingQuery()) {
            return null;
        }

        if (resetUnloadDelay && unloadQueue.contains(dim.getId()))
            getData(dim).ticksWaited = 0;

        @SuppressWarnings("deprecation")
        ServerWorld ret = server.forgeGetWorldMap().get(dim);
        if (ret == null && forceLoad)
            ret = initWorld(server, dim);
        return ret;
    }

    @SuppressWarnings("deprecation")
    public static ServerWorld initWorld(MinecraftServer server, DimensionType dim)
    {
        Validate.isTrue(dim != DimensionType.OVERWORLD, "Can not hotload overworld. This must be loaded at all times by main Server.");
        Validate.notNull(server, "Must provide server when creating world");
        Validate.notNull(dim, "Must provide dimension when creating world");

        ServerWorld overworld = getWorld(server, DimensionType.OVERWORLD, false, false);
        Validate.notNull(overworld, "Cannot Hotload Dim: Overworld is not Loaded!");

        @SuppressWarnings("resource")
        ServerWorld world = new ServerMultiWorld(overworld, server, server.getBackgroundExecutor(), overworld.getSaveHandler(), dim, server.getProfiler(), new NoopChunkStatusListener());
        if (!server.isSinglePlayer())
            world.getWorldInfo().setGameType(server.getGameType());
        server.forgeGetWorldMap().put(dim, world);
        server.markWorldsDirty();

        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));

        return world;
    }

    private static boolean canUnloadWorld(ServerWorld world)
    {
        return world.getDimension().getType() != DimensionType.OVERWORLD
                && world.getPlayers().isEmpty()
                && world.getForcedChunks().isEmpty()
                && !getData(world.getDimension().getType()).keepLoaded;
    }

    /**
     * Queues a dimension to unload, if it can be unloaded.
     * @param world The world to unload
     */
    public static void unloadWorld(ServerWorld world)
    {
        if (world == null || !canUnloadWorld(world))
            return;

        int id = world.getDimension().getType().getId();
        if (unloadQueue.add(id))
            LOGGER.debug(DIMMGR,"Queueing dimension {} to unload", id);
    }

    @SuppressWarnings("deprecation")
    public static void unloadWorlds(MinecraftServer server, boolean checkLeaks)
    {
        IntIterator queueIterator = unloadQueue.iterator();
        while (queueIterator.hasNext())
        {
            int id = queueIterator.nextInt();
            DimensionType dim = DimensionType.getById(id);

            if (dim == null)
            {
                LOGGER.warn(DIMMGR, "Dimension with unknown type '{}' added to unload queue, removing", id);
                queueIterator.remove();
                continue;
            }

            Data dimension = dimensions.computeIfAbsent(id, k -> new Data());
            if (dimension.ticksWaited < ForgeConfig.SERVER.dimensionUnloadQueueDelay.get())
            {
                dimension.ticksWaited++;
                continue;
            }

            queueIterator.remove();

            ServerWorld w = server.forgeGetWorldMap().get(dim);

            dimension.ticksWaited = 0;
            // Don't unload the world if the status changed
            if (w == null || !canUnloadWorld(w))
            {
                LOGGER.debug(DIMMGR,"Aborting unload for dimension {} as status changed", id);
                continue;
            }
            try
            {
                w.save(null, true, true);
            }
            catch (Exception e)
            {
                LOGGER.error(DIMMGR,"Caught an exception while saving all chunks:", e);
            }
            finally
            {
                MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(w));
                LOGGER.debug(DIMMGR, "Unloading dimension {}", id);
                try {
                    w.close();
                } catch (IOException e) {
                    LOGGER.error("Exception closing the level", e);
                }
                server.forgeGetWorldMap().remove(dim);
                server.markWorldsDirty();
            }
        }

        if (checkLeaks)
        {
            List<World> allWorlds = Lists.newArrayList(weakWorldMap.keySet());
            allWorlds.removeAll(server.forgeGetWorldMap().values());
            allWorlds.stream().map(System::identityHashCode).forEach(leakedWorlds::add);

            for (World w : allWorlds)
            {
                int hash = System.identityHashCode(w);
                int leakCount = leakedWorlds.count(hash);
                if (leakCount == 5)
                    LOGGER.debug(DIMMGR,"The world {} ({}) may have leaked: first encounter (5 occurrences).\n", Integer.toHexString(hash), w.getWorldInfo().getWorldName());
                else if (leakCount % 5 == 0)
                    LOGGER.debug(DIMMGR,"The world {} ({}) may have leaked: seen {} times.\n", Integer.toHexString(hash), w.getWorldInfo().getWorldName(), leakCount);
            }
        }
    }
    private static Data getData(DimensionType dim)
    {
        return dimensions.computeIfAbsent(dim.getId(), k -> new Data());
    }

    private static class Data
    {
        int ticksWaited = 0;
        boolean keepLoaded = false;
    }

    private static class NoopChunkStatusListener implements IChunkStatusListener
    {
        @Override public void start(ChunkPos center) { }
        @Override public void statusChanged(ChunkPos p_219508_1_, ChunkStatus p_219508_2_) { }
        @Override public void stop() { }
    }
}
