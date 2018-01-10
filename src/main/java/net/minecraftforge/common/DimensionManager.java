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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
import net.minecraftforge.common.Dimension;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLLog;

import javax.annotation.Nullable;

public class DimensionManager
{
    private static Hashtable<ResourceLocation, WorldServer> worlds = new Hashtable<ResourceLocation, WorldServer>();
    private static Set<ResourceLocation> activeDimensions = new HashSet<ResourceLocation>();
    private static List<ResourceLocation> unloadQueue = new ArrayList<ResourceLocation>();
    private static ConcurrentMap<World, World> weakWorldMap = new MapMaker().weakKeys().weakValues().<World,World>makeMap();
    private static Multiset<ResourceLocation> leakedWorlds = HashMultiset.create();

    /**
     * Returns a list of dimensions associated with this DimensionType.
     */
    public static ResourceLocation[] getDimensions(DimensionType type)	//Could return Dimension[] since this contains name info
    {
    	Set<ResourceLocation> dimIDs =  Dimension.REGISTRY.getKeys();
    	return dimIDs.toArray(new ResourceLocation[dimIDs.size()]);
    }
    
    /*Dimension must be registered first*/
    public static void registerDimensionActive(ResourceLocation dimID)
    {
    	if(!Dimension.REGISTRY.containsKey(dimID))
    	{
    		throw new IllegalArgumentException("Can't activate dimension "+ dimID.toString() + ", it hasn't been registered");
    	}
    	activeDimensions.add(dimID);
    }    
    
    /**
     * For unregistering a dimension when the save is changed (disconnected from a server or loaded a new save
     */
    public static void unregisterDimensionActive(ResourceLocation dimID)
    {
        if (!Dimension.REGISTRY.containsKey(dimID))
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
        return activeDimensions.contains(Dimension.REGISTRY.getObjectById(dimIntID).getID());
    }
    
    public static DimensionType getProviderType(int dimIntID)
    {
    	return getProviderType(Dimension.REGISTRY.getObjectById(dimIntID).getID());
    }
    
    public static DimensionType getProviderType(ResourceLocation dimID)
    {
        if (!Dimension.REGISTRY.containsKey(dimID))
        {
            throw new IllegalArgumentException("Could not get provider type for dimension " + dimID.toString() + ", does not exist");
        }
        return Dimension.REGISTRY.getObject(dimID).getType();
    }
    
    public static WorldProvider getProvider(ResourceLocation dimID)
    {
        return getWorld(dimID).provider;
    }

    public static ResourceLocation[] getIDs(boolean check)
    {
        if (check)
        {
            List<World> allWorlds = Lists.newArrayList(weakWorldMap.keySet());
            allWorlds.removeAll(worlds.values());
            for (ListIterator<World> li = allWorlds.listIterator(); li.hasNext(); )
            {
                ResourceLocation w = li.next().provider.getDimension();
                leakedWorlds.add(w);
            }
            for (World w : allWorlds)
            {
                int leakCount = leakedWorlds.count(w.provider.getDimension());
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
    
    public static Integer[] getIntIDs(boolean check)
    {
        if (check)
        {
            List<World> allWorlds = Lists.newArrayList(weakWorldMap.keySet());
            allWorlds.removeAll(worlds.values());
            for (ListIterator<World> li = allWorlds.listIterator(); li.hasNext(); )
            {
                ResourceLocation w = li.next().provider.getDimension();
                leakedWorlds.add(w);
            }
            for (World w : allWorlds)
            {
                int leakCount = leakedWorlds.count(w.provider.getDimension());
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
        return getIntIDs();
    }
    
    public static ResourceLocation[] getIDs()
    {
        return worlds.keySet().toArray(new ResourceLocation[worlds.keySet().size()]); //Only loaded dims, since usually used to cycle through loaded worlds
    }
    
    public static Integer[] getIntIDs()
    {
    	ResourceLocation[] ids = getIDs();
    	Integer[] ret = new Integer[ids.length];
    	int x = 0;
    	for(ResourceLocation id : ids)
    	{
    		ret[x++] = Dimension.REGISTRY.getObject(id).getDimIntID();
    	}
    	return Arrays.copyOf(ret, x);
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

        ArrayList<WorldServer> tmp = new ArrayList<WorldServer>();
        if (worlds.get(new ResourceLocation("minecraft:overworld")) != null)
            tmp.add(worlds.get(new ResourceLocation("minecraft:overworld")));
        if (worlds.get(new ResourceLocation("minecraft:nether")) != null)
            tmp.add(worlds.get(new ResourceLocation("minecraft:nether")));
        if (worlds.get(new ResourceLocation("minecraft:the_end")) != null)
            tmp.add(worlds.get(new ResourceLocation("minecraft:the_end")));

        for (Entry<ResourceLocation, WorldServer> entry : worlds.entrySet())
        {
            String dim = entry.getKey().toString();
            if (dim.equals("minecraft:overworld") || dim.equals("minecraft:nether") || dim.equals("minecraft:the_end"))
            {
                continue;
            }
            tmp.add(entry.getValue());
        }

        server.worlds = tmp.toArray(new WorldServer[tmp.size()]);
    }
    
    public static void setWorld(int dimIntID, @Nullable WorldServer world, MinecraftServer server)
    {
    	ResourceLocation dimID = Dimension.REGISTRY.getObjectById(dimIntID).getID();
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

        ArrayList<WorldServer> tmp = new ArrayList<WorldServer>();
        if (worlds.get(new ResourceLocation("minecraft:overworld")) != null)
            tmp.add(worlds.get(new ResourceLocation("minecraft:overworld")));
        if (worlds.get(new ResourceLocation("minecraft:nether")) != null)
            tmp.add(worlds.get(new ResourceLocation("minecraft:nether")));
        if (worlds.get(new ResourceLocation("minecraft:the_end")) != null)
            tmp.add(worlds.get(new ResourceLocation("minecraft:the_end")));

        for (Entry<ResourceLocation, WorldServer> entry : worlds.entrySet())
        {
            String dim = entry.getKey().toString();
            if (dim.equals("minecraft:overworld") || dim.equals("minecraft:nether") || dim.equals("minecraft:the_end"))
            {
                continue;
            }
            tmp.add(entry.getValue());
        }

        server.worlds = tmp.toArray(new WorldServer[tmp.size()]);
    }
    
    
    public static void initDimension(int dimIntID)
    {
    	initDimension(Dimension.REGISTRY.getObjectById(dimIntID).getID());
    }
    
    public static void initDimension(ResourceLocation dimID)
    {
        WorldServer overworld = getWorld(new ResourceLocation("minecraft:overworld"));
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

        WorldServer world = (dimID.toString().equals("minecraft:overworld") ? overworld : (WorldServer)(new WorldServerMulti(mcServer, savehandler, Dimension.REGISTRY.getObject(dimID).getDimIntID(), overworld, mcServer.profiler).init()));	//requires patch
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
        return getWorld(Dimension.REGISTRY.getObjectById(dimIntID).getID());
    }

    public static WorldServer[] getWorlds()
    {
        return worlds.values().toArray(new WorldServer[worlds.size()]);
    }

    static
    {
    	//Is there anything to do anymore?
    }

    /**
     * Not public API: used internally to get dimensions that should load at
     * server startup
     */
    public static Integer[] getStaticDimensionIDs()
    {
    	Integer[] ret= new Integer[Dimension.REGISTRY.getKeys().size()];
    	int x = 0;
    	for(ResourceLocation dimID: Dimension.REGISTRY.getKeys())
    	{
    		ret[x++] = Dimension.REGISTRY.getObject(dimID).getDimIntID();
    	}
    	return Arrays.copyOf(ret, x);
    }
    
    public static WorldProvider createProviderFor(ResourceLocation dimID)
    {
        try
        {
            if (Dimension.REGISTRY.containsKey(dimID))
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
    	ResourceLocation dimID = Dimension.REGISTRY.getObjectById(dimIntID).getID();
    	try
        {
            if (Dimension.REGISTRY.containsKey(dimID))
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
    
    /**
     * Queues a dimension to unload.
     * If the dimension is already queued, it will reset the delay to unload
     * @param id The id of the dimension
     */
    public static void unloadWorld(ResourceLocation dimID)
    {
        if(!unloadQueue.contains(dimID))
        {
            FMLLog.log.debug("Queueing dimension {} to unload", dimID.toString());
            unloadQueue.add(dimID);
        }
        else
        {
            Dimension.REGISTRY.getObject(dimID).setTicksWaited(0);
        }
    }
    
    /*
    * To be called by the server at the appropriate time, do not call from mod code.
    */
    public static void unloadWorlds(Hashtable<ResourceLocation, long[]> worldTickTimes) {
        Iterator<ResourceLocation> queueIterator = unloadQueue.iterator();
        while (queueIterator.hasNext()) {
            ResourceLocation dimID = queueIterator.next();
            Dimension dimension = Dimension.REGISTRY.getObject(dimID);
            if (dimension.getTicksWaited() < ForgeModContainer.dimensionUnloadQueueDelay)
            {
            	
                dimension.addTicksWaited(1);
                continue;
            }
            WorldServer w = worlds.get(dimID);
            queueIterator.remove();
            dimension.setTicksWaited(0);
            if (w == null || !ForgeChunkManager.getPersistentChunksFor(w).isEmpty() || !w.playerEntities.isEmpty() || dimension.getType().shouldLoadSpawn()) //Don't unload the world if the status changed
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
    /* returns the dimensions registered to a mod based on its modid */
    public static ResourceLocation[] getDimensionForMod(String modid)
    {
    	ResourceLocation[] ret = new ResourceLocation[Dimension.REGISTRY.getKeys().size()];
    	int x = 0;
    	for(ResourceLocation id : Dimension.REGISTRY.getKeys())
    	{
    		if(id.getResourceDomain().equals(modid))
    		{
    			ret[x++] = id;
    		}
    	}
    	return Arrays.copyOf(ret, x);
    }

    /*returns true if the dimension if from a mod given the modid*/
    public static boolean isFromMod(World worldIn, String modid)	// needs patch really
    {
    	ResourceLocation dimID = worldIn.provider.getDimension();
    	return dimID.getResourceDomain().equals(modid);
    }
}