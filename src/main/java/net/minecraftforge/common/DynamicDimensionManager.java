/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

/**
 * API for creating and uncreating dynamic dimensions during game runtime. This
 * should only be used for creating dynamic dimensions, whose quantity and
 * properties aren't known until created by some game mechanic. Static
 * dimensions (whose quantities and properties are fixed ahead of time) should
 * be created using the vanilla json dimension system.
 */
@ParametersAreNonnullByDefault
public class DynamicDimensionManager
{
    private static final Set<ResourceKey<Level>> VANILLA_WORLDS = ImmutableSet.of(Level.OVERWORLD, Level.NETHER, Level.END); 
    private static Set<ResourceKey<Level>> pendingLevelsToUnregister = new HashSet<>();
    
    /**
     * Retrieves the level for a given level key, registering and creating one if it doesn't already exist.
     * 
     * Levels created this way will be saved to the save folder's dimension registry
     * and will be automatically loaded the next time the server starts. All registered levels will tick while they exist
     * (though a level without any loaded chunks won't do much in the tick).
     * 
     * @param server a server
     * @param levelKey The resource key for the world
     * @param dimensionFactory The function to use to create the level's LevelStem instance if it doesn't already exist;
     * the given LevelStem key will have the same ID as the given Level key
     * @return A server level for the given key
     * 
     * @apiNote To retreive static dimensions that always exist, or to look up a (nullable) world without force-creating it,
     * use {@link MinecraftServer#getLevel(ResourceKey)} instead.
     * 
     * To register a static dimension that always exists, make a json dimension instead; minecraft will load and register it automatically
     * 
     * To unregister a dynamic dimension level (preventing it from ticking, or from loading at server startup),
     * use {@link DynamicDimensionManager#unregisterDimensions(MinecraftServer, Set)}.
     */
    public static ServerLevel getOrCreateLevel(final MinecraftServer server, final ResourceKey<Level> levelKey, final BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory)
    {
        // (we're doing the lookup this way because we'll need the map if we need to add a new level)
        @SuppressWarnings("deprecation") // forgeGetWorldMap is deprecated because it's a forge-internal-use-only method
        final Map<ResourceKey<Level>, ServerLevel> map = server.forgeGetWorldMap();
        
        // if the level already exists, return it
        final @Nullable ServerLevel existingLevel = map.get(levelKey);
        if (existingLevel != null)
        {
            return existingLevel;
        }

        final ServerLevel newLevel = createAndRegisterWorldAndDimension(server, map, levelKey, dimensionFactory);
        return newLevel;
    }
    
    /**
     * Marks a level and its levelstem for unregistration. Unregistered levels will stop ticking,
     * unregistered levelstems will not be loaded on server startup unless and until they are reregistered again.
     * 
     * Unregistration is delayed until the end of the server tick (just after the post-server-tick-event fires).
     * 
     * Players who are still in the given level at that time will be ejected to their respawn points.
     * Players who have respawn points in levels being unloaded will have their spawn points reset to the overworld and respawned there.
     * 
     * Unregistering a level does not delete the region files or other data associated with the level's level folder.
     * If a level is reregistered after unregistering it, the level will retain all prior data (unless manually deleted via server admin)
     * 
     * @param levelToRemove The key for the level to schedule for unregistration. Vanilla dimensions are not removable as they are
     * generally assumed to exist (especially the overworld)
     * 
     * @apiNote Not intended for use with vanilla or json dimensions, doing so may cause strange problems.
     * 
     * However, if a vanilla or json dimension *is* removed, restarting the server will reconstitute it as
     * vanilla automatically detects and registers these.
     * 
     * Mods whose dynamic dimensions require the ejection of players to somewhere other than their respawn point
     * should teleport these worlds' players to appropriate locations before unregistering their dimensions.
     */
    public static void markDimensionForUnregistration(final MinecraftServer server, final ResourceKey<Level> levelToRemove)
    {
        if (!VANILLA_WORLDS.contains(levelToRemove))
        {
            DynamicDimensionManager.pendingLevelsToUnregister.add(levelToRemove);
        }
    }
    
    /**
     * @return an immutable view of the levels pending to be unregistered and unloaded at the end of the current server tick
     */
    public static Set<ResourceKey<Level>> getWorldsPendingUnregistration()
    {
        return Collections.unmodifiableSet(DynamicDimensionManager.pendingLevelsToUnregister);
    }
    
    /**
     * called at the end of the server tick just before the post-server-tick-event
     * @deprecated Internal forge method 
     */
    @Deprecated
    public static void unregisterScheduledDimensions(final MinecraftServer server)
    {
        // flush the buffer
        final Set<ResourceKey<Level>> keysToRemove = DynamicDimensionManager.pendingLevelsToUnregister;
        DynamicDimensionManager.pendingLevelsToUnregister = new HashSet<>();
        
        // we need to remove the dimension/world form three places
        // the server's dimension registry, the server's world registry, and the overworld's world border listener
        // the world registry is just a simple map and the world border listener has a remove() method
        // the dimension registry has five sub-collections that need to be cleaned up
        // we should also eject players from the removed worlds or they could get stuck there
        
        final WorldGenSettings worldGenSettings = server.getWorldData().worldGenSettings();
        final Set<ResourceKey<Level>> removedLevelKeys = new HashSet<>();
        final ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        
        for (final ResourceKey<Level> levelKeyToRemove : keysToRemove)
        {
            @Nullable ServerLevel removedLevel = server.forgeGetWorldMap().remove(levelKeyToRemove); // null if the specified key was not present
            if (removedLevel != null) // if we removed the key from the map
            {
                // eject players from dead world
                // iterate over a copy as the world will remove players from the original list
                for (final ServerPlayer player : Lists.newArrayList(removedLevel.players()))
                {
                    // send players to their respawn point
                    ResourceKey<Level> respawnKey = player.getRespawnDimension();
                    // if we're removing their respawn world then just send them to the overworld
                    if (keysToRemove.contains(respawnKey))
                    {
                        respawnKey = Level.OVERWORLD;
                        player.setRespawnPosition(Level.OVERWORLD, null, 0, false, false);
                    }
                    if (respawnKey == null)
                        respawnKey = Level.OVERWORLD;
                    final ServerLevel destinationLevel = server.getLevel(respawnKey);
                    BlockPos destinationPos = player.getRespawnPosition();
                    if (destinationPos == null)
                        destinationPos = destinationLevel.getSharedSpawnPos();
                    final float respawnAngle = player.getRespawnAngle();
                    // "respawning" the player via the player list schedules a task in the server to run after the post-server tick
                    // that causes some minor logspam due to the player's world no longer being loaded
                    // teleporting the player this way instead avoids this
                    player.teleportTo(destinationLevel, destinationPos.getX(), destinationPos.getY(), destinationPos.getZ(), respawnAngle, 0F);
                }
                // save the world now or it won't be saved later and data that may be wanted to be kept may be lost
                removedLevel.save(null, false, removedLevel.noSave());

                // fire world unload event -- when the server stops, this would fire after worlds get saved, so we'll do that here too
                MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(removedLevel));
                
                // remove the world border listener if possible
                final WorldBorder overworldBorder = overworld.getWorldBorder();
                final WorldBorder removedWorldBorder = removedLevel.getWorldBorder();
                final List<BorderChangeListener> listeners = overworldBorder.listeners;
                BorderChangeListener targetListener = null;
                for (BorderChangeListener listener : listeners)
                {
                    if (listener instanceof BorderChangeListener.DelegateBorderChangeListener && removedWorldBorder == ((BorderChangeListener.DelegateBorderChangeListener)listener).worldBorder)
                    {
                        targetListener = listener;
                        break;
                    }
                }
                if (targetListener != null)
                {
                    overworldBorder.removeListener(targetListener);
                }
                
                // track the removed world
                removedLevelKeys.add(levelKeyToRemove);
            }
        }
        
        if (!removedLevelKeys.isEmpty())
        {
            // replace the old dimension registry with a new one containing the dimensions that weren't removed, in the same order
            final MappedRegistry<LevelStem> oldRegistry = worldGenSettings.dimensions();
            final MappedRegistry<LevelStem> newRegistry = new MappedRegistry<>(Registry.LEVEL_STEM_REGISTRY, oldRegistry.elementsLifecycle());
            
            for (final Entry<ResourceKey<LevelStem>, LevelStem> entry : oldRegistry.entrySet())
            {
                final ResourceKey<LevelStem> oldKey = entry.getKey();
                final ResourceKey<Level> oldLevelKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, oldKey.location());
                final LevelStem dimension = entry.getValue();
                if (oldKey != null && dimension != null && !removedLevelKeys.contains(oldLevelKey))
                {
                    newRegistry.register(oldKey, dimension, oldRegistry.lifecycle(dimension));
                }
            }
            
            // then replace the old registry with the new registry
            worldGenSettings.dimensions = newRegistry;

            // update the server's levels so dead levels don't get ticked
            server.markWorldsDirty();
            // client will need to be notified of the removed level for the dimension command suggester
            NetworkHooks.updateClientDimensionLists(ImmutableSet.of(), removedLevelKeys);
        }
    }
    
    @SuppressWarnings("deprecation") // because we call the forge internal method server#markWorldsDirty
    private static ServerLevel createAndRegisterWorldAndDimension(final MinecraftServer server, final Map<ResourceKey<Level>, ServerLevel> map, final ResourceKey<Level> worldKey, final BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory)
    {
        // get everything we need to create the dimension and the level
        final ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        
        // dimension keys have a 1:1 relationship with level keys, they have the same IDs as well
        final ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, worldKey.location());
        final LevelStem dimension = dimensionFactory.apply(server, dimensionKey);

        // the int in create() here is radius of chunks to watch, 11 is what the server uses when it initializes worlds
        final ChunkProgressListener chunkProgressListener = server.progressListenerFactory.create(11);
        final Executor executor = server.executor;
        final LevelStorageAccess anvilConverter = server.storageSource;
        final WorldData worldData = server.getWorldData();
        final WorldGenSettings worldGenSettings = worldData.worldGenSettings();
        final DerivedLevelData derivedLevelData = new DerivedLevelData(worldData, worldData.overworldData());
        
        // now we have everything we need to create the dimension and the level
        // this is the same order server init creates levels:
        // the dimensions are already registered when levels are created, we'll do that first
        // then instantiate level, add border listener, add to map, fire world load event
        
        // register the actual dimension
        worldGenSettings.dimensions().register(dimensionKey, dimension, Lifecycle.experimental());

        // create the world instance
        final ServerLevel newWorld = new ServerLevel(
            server,
            executor,
            anvilConverter,
            derivedLevelData,
            worldKey,
            dimension.type(),
            chunkProgressListener,
            dimension.generator(),
            worldGenSettings.isDebug(),
            net.minecraft.world.level.biome.BiomeManager.obfuscateSeed(worldGenSettings.seed()),
            ImmutableList.of(), // "special spawn list"
                // phantoms, travelling traders, patrolling/sieging raiders, and cats are overworld special spawns
                // this is always empty for non-overworld dimensions (including json dimensions)
                // these spawners are ticked when the world ticks to do their spawning logic,
                // mods that need "special spawns" for their own dimensions should implement them via tick events or other systems
            false // "tick time", true for overworld, always false for nether, end, and json dimensions
            );
        
        // add world border listener, for parity with json dimensions
        // the vanilla behaviour is that world borders exist in every dimension simultaneously with the same size and position
        // these border listeners are automatically added to the overworld as worlds are loaded, so we should do that here too
        // TODO if world-specific world borders are ever added, change it here too
        overworld.getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(newWorld.getWorldBorder()));
        
        // register level
        map.put(worldKey, newWorld);
        
        // update forge's world cache so the new level can be ticked
        server.markWorldsDirty();
        
        // fire world load event
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(newWorld));
        
        // update clients' dimension lists
        NetworkHooks.updateClientDimensionLists(ImmutableSet.of(worldKey), ImmutableSet.of());
        
        return newWorld;
    }
}
