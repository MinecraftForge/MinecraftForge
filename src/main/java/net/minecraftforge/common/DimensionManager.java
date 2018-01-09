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
import java.util.BitSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntListIterator;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multiset;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public class DimensionManager
{
    private static Hashtable<ResourceLocation, WorldServer> worlds = new Hashtable<ResourceLocation, WorldServer>();
    private static boolean hasInit = false;
    private static Set<ResourceLocation> activeDimensions = new HashSet<ResourceLocation>();
    private static List<ResourceLocation> unloadQueue = new ArrayList<ResourceLocation>();
    private static BitSet dimensionMap = new BitSet(Long.SIZE << 4);
    private static ConcurrentMap<World, World> weakWorldMap = new MapMaker().weakKeys().weakValues().<World,World>makeMap();
    private static Multiset<Integer> leakedWorlds = HashMultiset.create();

    /**
     * Returns a list of dimensions associated with this DimensionType.
     */
    public static String[] getDimensions(DimensionType type)
    {
    	String[] ret = new String[Dimension.REGISTRY.getKeys().size()];
        int x = 0;
        for (Dimension dimension : Dimension.REGISTRY)
        {
            if (dimension.getType() == type)
            {
                ret[x++] = Dimension.REGISTRY.getNameForObject(dimension).toString();
            }
        }

        return Arrays.copyOf(ret, x);

    }
    /*Will add dimension to registry if needed*/
    public static void registerDimensionActive(DimensionType type, String id)
    {
    	ResourceLocation dimID = new ResourceLocation(id);
        DimensionType.getById(type.getId()); //Check if type is invalid {will throw an error} No clue how it would be invalid tho...
        if(!Dimension.REGISTRY.containsKey(dimID))
        {
        	Dimension.REGISTRY.putObject(dimID, new Dimension(type));
        }
        activeDimensions.add(dimID);
    }
    
    /**
     * For unregistering a dimension when the save is changed (disconnected from a server or loaded a new save
     */
    public static void unregisterDimensionActive(String id)
    {
    	ResourceLocation dimID = new ResourceLocation(id);
        if (!Dimension.REGISTRY.containsKey(dimID))
        {
            throw new IllegalArgumentException(String.format("Failed to unregister dimension for id %d; No provider registered", id));
        }
        activeDimensions.remove(dimID);
    }

    public static boolean isDimensionActive(String id)
    {
        return activeDimensions.contains(new ResourceLocation(id));
    }
    
    public static DimensionType getProviderType(String id)
    {
    	ResourceLocation dimID = new ResourceLocation(id);
        if (!Dimension.REGISTRY.containsKey(dimID))
        {
            throw new IllegalArgumentException("Could not get provider type for dimension " + dimID.toString() + ", does not exist");
        }
        return Dimension.REGISTRY.getObject(dimID).getType();
    }
    
    public static WorldProvider getProvider(String dim)
    {
        return getWorld(dim).provider;
    }

    public static String[] getIDs(boolean check)
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
    
    public static String[] getIDs()
    {
        return worlds.keySet().toArray(new String[worlds.size()]); //Only loaded dims, since usually used to cycle through loaded worlds
    }
    
    public static void setWorld(String id, @Nullable WorldServer world, MinecraftServer server)
    {
    	ResourceLocation dimID = new ResourceLocation(id);
        if (world != null)
        {
            worlds.put(dimID, world);
            weakWorldMap.put(world, world);
            server.worldTickTimes.put(dimID, new long[100]);	//Have to patch or find a way around
            FMLLog.log.info("Loading dimension {} ({}) ({})", id.toString(), world.getWorldInfo().getWorldName(), world.getMinecraftServer());
        }
        else
        {
            worlds.remove(dimID);
            server.worldTickTimes.remove(dimID);
            FMLLog.log.info("Unloading dimension {}", id.toString());
        }

        ArrayList<WorldServer> tmp = new ArrayList<WorldServer>();
        if (worlds.get( 0) != null)
            tmp.add(worlds.get( 0));
        if (worlds.get(-1) != null)
            tmp.add(worlds.get(-1));
        if (worlds.get( 1) != null)
            tmp.add(worlds.get( 1));

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
    
    public static void initDimension(String dim)
    {
    	ResourceLocation dimID = new ResourceLocation(dim);
        WorldServer overworld = getWorld("minecraft:overworld");
        if (overworld == null)
        {
            throw new RuntimeException("Cannot Hotload Dim: Overworld is not Loaded!");
        }
        try
        {
            DimensionManager.getProviderType(dim);
        }
        catch (Exception e)
        {
            FMLLog.log.error("Cannot Hotload Dim: {}", dim, e);
            return; // If a provider hasn't been registered then we can't hotload the dim
        }
        MinecraftServer mcServer = overworld.getMinecraftServer();
        ISaveHandler savehandler = overworld.getSaveHandler();
        //WorldSettings worldSettings = new WorldSettings(overworld.getWorldInfo());

        WorldServer world = (dim.equals("minecraft:overworld") ? overworld : (WorldServer)(new WorldServerMulti(mcServer, savehandler, dim, overworld, mcServer.profiler).init()));	//requires patch
        world.addEventListener(new ServerWorldEventHandler(mcServer, world));
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
        if (!mcServer.isSinglePlayer())
        {
            world.getWorldInfo().setGameType(mcServer.getGameType());
        }

        mcServer.setDifficultyForAllWorlds(mcServer.getDifficulty());
    }
    
    public static WorldServer getWorld(String id)
    {
        return worlds.get(new ResourceLocation(id));
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
    public static ResourceLocation[] getStaticDimensionIDs()	//This can return string[] for consistency
    {
        return Dimension.REGISTRY.getKeys().toArray(new ResourceLocation[Dimension.REGISTRY.getKeys().size()]);
    }
    
    public static WorldProvider createProviderFor(String dim)
    {
        try
        {
            if (Dimension.REGISTRY.containsKey(new ResourceLocation(dim)))
            {
                WorldProvider ret = getProviderType(dim).createDimension();
                ret.setDimension(dim);
                return ret;
            }
            else
            {
                throw new RuntimeException("No WorldProvider bound for dimension "+ dim); //It's going to crash anyway at this point.  Might as well be informative
            }
        }
        catch (Exception e)
        {
            FMLLog.log.error("An error occurred trying to create an instance of WorldProvider {} ({})",
                    dim, getProviderType(dim), e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Queues a dimension to unload.
     * If the dimension is already queued, it will reset the delay to unload
     * @param id The id of the dimension
     */
    public static void unloadWorld(String id)
    {
    	ResourceLocation dimID = new ResourceLocation(id);
        if(!unloadQueue.contains(dimID))
        {
            FMLLog.log.debug("Queueing dimension {} to unload", id);
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
                setWorld(dimID.toString(), null, w.getMinecraftServer());
            }
        }
    }
    
/*					//I don't know if we can adapt this somehow to give ints
    /**
     * Return the next free dimension ID. Note: you are not guaranteed a contiguous
     * block of free ids. Always call for each individual ID you wish to get.
     * @return the next free dimension ID
     */
//    public static int getNextFreeDimId() {			
//        int next = 0;
//        while (true)
//        {
//            next = dimensionMap.nextClearBit(next);
//            if (dimensions.containsKey(next))
//            {
//                dimensionMap.set(next);
//            }
//            else
//            {
//                return next;
//            }
//        }
//    }
//
//
//    public static NBTTagCompound saveDimensionDataMap()
//    {
//        int[] data = new int[(dimensionMap.length() + Integer.SIZE - 1 )/ Integer.SIZE];
//        NBTTagCompound dimMap = new NBTTagCompound();
//        for (int i = 0; i < data.length; i++)
//        {
//            int val = 0;
//            for (int j = 0; j < Integer.SIZE; j++)
//            {
//                val |= dimensionMap.get(i * Integer.SIZE + j) ? (1 << j) : 0;
//            }
//            data[i] = val;
//        }
//        dimMap.setIntArray("DimensionArray", data);
//        return dimMap;
//    }
//
//    public static void loadDimensionDataMap(@Nullable NBTTagCompound compoundTag)
//    {
//        dimensionMap.clear();
//        if (compoundTag == null)
//        {
//            for (Integer id : dimensionIDMap.inverse().keySet())
//            {
//                if (id >= 0)
//                {
//                    dimensionMap.set(id);
//                }
//            }
//        }
//        else
//        {
//            int[] intArray = compoundTag.getIntArray("DimensionArray");
//            for (int i = 0; i < intArray.length; i++)
//            {
//                for (int j = 0; j < Integer.SIZE; j++)
//                {
//                    dimensionMap.set(i * Integer.SIZE + j, (intArray[i] & (1 << j)) != 0);
//                }
//            }
//        }
//    }

    /**
     * Return the current root directory for the world save. Accesses getSaveHandler from the overworld
     * @return the root directory of the save
     */
    @Nullable
    public static File getCurrentSaveRootDirectory()
    {
        if (DimensionManager.getWorld("minecraft:overworld") != null)
        {
            return DimensionManager.getWorld("minecraft:overworld").getSaveHandler().getWorldDirectory();
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
}
    
//    /*returns true if the dimension if from a mod given the modid*/
//    public static boolean isFromMod(World worldIn, String modid)	// needs patch really
//    {
//    	return true;
//    }
//    }
