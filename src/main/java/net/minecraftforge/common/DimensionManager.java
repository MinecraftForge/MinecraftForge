/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multiset;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorldEventHandler;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;

public class DimensionManager
{
    private static Hashtable<ResourceLocation, WorldServer> worlds = new Hashtable<>();
    private static Set<ResourceLocation> activeDimensions = new HashSet<>();
    private static final Set<ResourceLocation> keepLoaded = new HashSet<>();
    private static List<ResourceLocation> unloadQueue = new ArrayList<>();
    private static ConcurrentMap<World, World> weakWorldMap = new MapMaker().weakKeys().weakValues().makeMap();
    private static Multiset<Integer> leakedWorlds = HashMultiset.create();

    public static final ResourceLocation OVERWORLD_ID = new ResourceLocation("minecraft:overworld");
    public static final ResourceLocation NETHER_ID = new ResourceLocation("minecraft:nether");
    public static final ResourceLocation END_ID = new ResourceLocation("minecraft:the_end");


    /**
     * Returns a list of dimensions associated with this DimensionType.
     * @param type the dimension type in question
     * @return a set of the dimension ids using that dimension type
     */
    public static Set<ResourceLocation> getDimensions(DimensionType type)
    {
    	Set<ResourceLocation> ret = new HashSet<>();
    	for(Dimension dimension : ForgeRegistries.DIMENSIONS.getValuesCollection())
    	{
    		if(dimension.getType()==type)
    			ret.add(dimension.getID());	
    	}
    	return ret;
    }
    
    /*Shouldn't need to call this outside of this class*/
    private static Set<Integer> getIntIDs(Collection<ResourceLocation> ids)
    {
    	Set<Integer> ret = new HashSet<>();
    	for(ResourceLocation id : ids)
    	{
    		ret.add(Dimension.getDimIntID(id));
    	}
    	return ret;
    }

    public static void registerDimensionActive(ResourceLocation dimID)
    {
    	if(!ForgeRegistries.DIMENSIONS.containsKey(dimID))
    	{
    		throw new IllegalArgumentException("Can't activate dimension "+ dimID.toString() + ", it hasn't been registered");
    	}
    	activeDimensions.add(dimID);
    }    
    
    /**
     * For unregistering a dimension when the save is changed (disconnected from a server or loaded a new save)
     */
    public static void unregisterDimensionActive(ResourceLocation dimID)
    {
        if (!ForgeRegistries.DIMENSIONS.containsKey(dimID))
        {
            throw new IllegalArgumentException("Failed to unregister dimension for dimID "+ dimID.toString() +" No provider registered");
        }
        activeDimensions.remove(dimID);
    }

    public static boolean isDimensionActive(ResourceLocation dimID)
    {
        return activeDimensions.contains(dimID);
    }
    
    public static boolean isDimensionActive(int dimIntID)
    {
        return isDimensionActive(Dimension.getID(dimIntID));
    }
    
    public static DimensionType getProviderType(int dimIntID)
    {
    	return getProviderType(Dimension.getID(dimIntID));
    }
    
    public static DimensionType getProviderType(ResourceLocation dimID)
    {
        if (!ForgeRegistries.DIMENSIONS.containsKey(dimID))
        {
            throw new IllegalArgumentException("Could not get provider type for dimension " + dimID.toString() + ", does not exist");
        }
        return ForgeRegistries.DIMENSIONS.getValue(dimID).getType();
    }
    
    public static WorldProvider getProvider(ResourceLocation dimID)
    {
        return getWorld(dimID).provider;
    }

    /**
     * @param check check for leaking worlds
     * @return all ids in use
     */
    public static Set<ResourceLocation> getIDs(boolean check)
    {
        if (check)
        {
            List<World> allWorlds = Lists.newArrayList(weakWorldMap.keySet());
            allWorlds.removeAll(worlds.values());
            for (ListIterator<World> li = allWorlds.listIterator(); li.hasNext(); )
            {
                World w = li.next();
                leakedWorlds.add(System.identityHashCode(w));
            }
            for (World w : allWorlds)
            {
                int leakCount = leakedWorlds.count(System.identityHashCode(w));
                if (leakCount == 5)
                {
                    FMLLog.log.debug("The world {} ({}) may have leaked: first encounter (5 occurrences).\n", Integer.toHexString(System.identityHashCode(w)), w.getWorldInfo().getWorldName());
                }
                else if (leakCount % 5 == 0)
                {
                    FMLLog.log.debug("The world {} ({}) may have leaked: seen {} times.\n", Integer.toHexString(System.identityHashCode(w)), w.getWorldInfo().getWorldName(), leakCount);
                }
            }
        }
        return getIDs();
    }
    
    public static Set<Integer> getIntIDs(boolean check)
    {
        Set<ResourceLocation> ids = getIDs(check);
        return getIntIDs(ids);
    }
    
    public static Set<Integer> getIntIDs()
    {
        Set<ResourceLocation> ids = getIDs();
        return getIntIDs(ids);
    }
    
    public static Set<ResourceLocation> getIDs()
    {
        return worlds.keySet(); //Only loaded dims, since usually used to cycle through loaded worlds
    }
    
    public static void setWorld(ResourceLocation dimID, @Nullable WorldServer world, MinecraftServer server)
    {
        if (world != null)
        {
            worlds.put(dimID, world);
            weakWorldMap.put(world, world);
            server.worldTickTimes.put(dimID, new long[100]);	//Have to patch or find a way around
            FMLLog.log.info("Loading dimension {} ({}) ({})", dimID.toString(), world.getWorldInfo().getWorldName(), world.getMinecraftServer());
        }
        else
        {
            worlds.remove(dimID);
            server.worldTickTimes.remove(dimID);
            FMLLog.log.info("Unloading dimension {}", dimID.toString());
        }

        ArrayList<WorldServer> tmp = new ArrayList<>();
        if (worlds.get(OVERWORLD_ID) != null)
            tmp.add(worlds.get(OVERWORLD_ID));
        if (worlds.get(NETHER_ID) != null)
            tmp.add(worlds.get(NETHER_ID));
        if (worlds.get(END_ID) != null)
            tmp.add(worlds.get(END_ID));

        for (Entry<ResourceLocation, WorldServer> entry : worlds.entrySet())
        {
            ResourceLocation dim = entry.getKey();
            if (dim.equals(OVERWORLD_ID) || dim.equals(NETHER_ID) || dim.equals(END_ID))
            {
                continue;
            }
            tmp.add(entry.getValue());
        }

        server.worlds = tmp.toArray(new WorldServer[tmp.size()]);
    }
    
    public static void setWorld(int dimIntID, @Nullable WorldServer world, MinecraftServer server)
    {
    	setWorld(Dimension.getID(dimIntID), world, server);
    }
      
    public static void initDimension(int dimIntID)
    {
    	initDimension(Dimension.getID(dimIntID));
    }
    
    public static void initDimension(ResourceLocation dimID)
    {
        WorldServer overworld = getWorld(OVERWORLD_ID);
        if (overworld == null)
        {
            throw new RuntimeException("Cannot Hotload Dim: Overworld is not Loaded!");
        }
        try
        {
            DimensionManager.getProviderType(dimID);
        }
        catch (Exception e)
        {
            FMLLog.log.error("Cannot Hotload Dim: {}", dimID.toString(), e);
            return; // If a provider hasn't been registered then we can't hotload the dim
        }
        MinecraftServer mcServer = overworld.getMinecraftServer();
        ISaveHandler savehandler = overworld.getSaveHandler();
        //WorldSettings worldSettings = new WorldSettings(overworld.getWorldInfo());

        WorldServer world = (dimID.equals(OVERWORLD_ID) ? overworld : (WorldServer)(new WorldServerMulti(mcServer, savehandler, ForgeRegistries.DIMENSIONS.getValue(dimID).getDimIntID(), overworld, mcServer.profiler).init()));	//requires patch
        world.addEventListener(new ServerWorldEventHandler(mcServer, world));
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
        if (!mcServer.isSinglePlayer())
        {
            world.getWorldInfo().setGameType(mcServer.getGameType());
        }

        mcServer.setDifficultyForAllWorlds(mcServer.getDifficulty());
    }
    
    public static WorldServer getWorld(ResourceLocation dimID)
    {
        return worlds.get(dimID);
    }
    
    public static WorldServer getWorld(int dimIntID)
    {
        return getWorld(Dimension.getID(dimIntID));
    }

    public static Collection<WorldServer> getWorlds()
    {
        return worlds.values();
    }

    /**
     * Not public API: used internally to get dimensions that should load at
     * server startup. Mods must register a dimension active to have this work.
     */
    public static Set<Integer> getStaticDimensionIDs()
    {
    	return getIntIDs(activeDimensions);
    }
    
    public static WorldProvider createProviderFor(ResourceLocation dimID)
    {
        try
        {
            if (ForgeRegistries.DIMENSIONS.containsKey(dimID))
            {
                WorldProvider ret = getProviderType(dimID).createDimension();
                ret.setDimension(dimID);
                return ret;
            }
            else
            {
                throw new RuntimeException("No WorldProvider bound for dimension "+ dimID.toString()); //It's going to crash anyway at this point.  Might as well be informative
            }
        }
        catch (Exception e)
        {
            FMLLog.log.error("An error occurred trying to create an instance of WorldProvider {} ({})",
                    dimID.toString(), getProviderType(dimID), e);
            throw new RuntimeException(e);
        }
    }
    
    public static WorldProvider createProviderFor(int dimIntID)
    {
    	return createProviderFor(Dimension.getID(dimIntID));
    }

    /**
     * Sets if a dimension should stay loaded
     * @param dim the dimension ID
     * @param keep whether or not the dimension should be kept loaded
     * @return true iff the dimension's status changed
     */
    public static boolean keepDimensionLoaded(ResourceLocation dim, boolean keep)
    {
        return keep ? keepLoaded.add(dim) : keepLoaded.remove(dim);
    }

    private static boolean canUnloadWorld(WorldServer world)
    {
        return ForgeChunkManager.getPersistentChunksFor(world).isEmpty()
                && world.playerEntities.isEmpty()
                && !world.provider.getDimensionType().shouldLoadSpawn()
                && !keepLoaded.contains(world.provider.getDimension());
    }

    /**
     * Queues a dimension to unload.
     * If the dimension is already queued, it will reset the delay to unload
     * @param dimID The id of the dimension
     */
    public static void unloadWorld(ResourceLocation dimID)
    {
        WorldServer world = worlds.get(dimID);
        if (world == null || !canUnloadWorld(world)) return;

        if (!unloadQueue.contains(dimID))
        {
            FMLLog.log.debug("Queueing dimension {} to unload", dimID.toString());
            unloadQueue.add(dimID);
        }
        else
        {
        	ForgeRegistries.DIMENSIONS.getValue(dimID).setTicksWaited(0);
        }
    }
    
    /*
    * To be called by the server at the appropriate time, do not call from mod code.
    */
    public static void unloadWorlds(Map<ResourceLocation, long[]> worldTickTimes) {
        Iterator<ResourceLocation> queueIterator = unloadQueue.iterator();
        while (queueIterator.hasNext()) {
            ResourceLocation dimID = queueIterator.next();
            Dimension dimension = ForgeRegistries.DIMENSIONS.getValue(dimID);
            if (dimension.getTicksWaited() < ForgeModContainer.dimensionUnloadQueueDelay)
            {
            	
                dimension.addTicksWaited(1);
                continue;
            }
            WorldServer w = worlds.get(dimID);
            queueIterator.remove();
            dimension.setTicksWaited(0);
            // Don't unload the world if the status changed
            if (w == null || !canUnloadWorld(w))
            {
                FMLLog.log.debug("Aborting unload for dimension {} as status changed", dimID.toString());
                continue;
            }
            try
            {
                w.saveAllChunks(true, null);
            }
            catch (MinecraftException e)
            {
                FMLLog.log.error("Caught an exception while saving all chunks:", e);
            }
            finally
            {
                MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(w));
                w.flush();
                setWorld(dimID, null, w.getMinecraftServer());
            }
        }
    }

    /**
     * Return the current root directory for the world save. Accesses getSaveHandler from the overworld
     * @return the root directory of the save
     */
    @Nullable
    public static File getCurrentSaveRootDirectory()
    {
        if (DimensionManager.getWorld(new ResourceLocation("minecraft:overworld")) != null)
        {
            return DimensionManager.getWorld(new ResourceLocation("minecraft:overworld")).getSaveHandler().getWorldDirectory();
        }/*
        else if (MinecraftServer.getServer() != null)
        {
            MinecraftServer srv = MinecraftServer.getServer();
            SaveHandler saveHandler = (SaveHandler) srv.getActiveAnvilConverter().getSaveLoader(srv.getFolderName(), false);
            return saveHandler.getWorldDirectory();
        }*/
        else
        {
            return null;
        }
    }
    /**
     * gets all dimensions registered by a mod
     * @param modid the modid of the mod
     * @return the ids of those dimensions
     */
    public static Set<ResourceLocation> getDimensionsForMod(String modid)
    {
    	Set<ResourceLocation> ret = new HashSet<>();
    	for(ResourceLocation id : ForgeRegistries.DIMENSIONS.getKeys())
    	{
    		if(id.getResourceDomain().equals(modid))
    		{
    			ret.add(id);
    		}
    	}
    	return ret;
    }

    /*returns true if the dimension if from a mod given the modid*/
    public static boolean isFromMod(World worldIn, String modid)	// needs patch really
    {
    	ResourceLocation dimID = worldIn.provider.getDimension();
    	return dimID.getResourceDomain().equals(modid);
    }
}