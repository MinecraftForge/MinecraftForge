package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class DimensionManager
{
    private static Hashtable<Integer, Class<? extends WorldProvider>> providers = new Hashtable<Integer, Class<? extends WorldProvider>>();
    private static Hashtable<Integer, Boolean> spawnSettings = new Hashtable<Integer, Boolean>();
    private static Hashtable<Integer, WorldServer> worlds = new Hashtable<Integer, WorldServer>();
    private static boolean hasInit = false;
    private static Hashtable<Integer, Integer> dimensions = new Hashtable<Integer, Integer>();
    private static Map<World, ListMultimap<ChunkCoordIntPair, String>> persistentChunkStore = Maps.newHashMap();

    public static boolean registerProviderType(int id, Class<? extends WorldProvider> provider, boolean keepLoaded)
    {
        if (providers.containsValue(id))
        {
            return false;
        }
        providers.put(id, provider);
        spawnSettings.put(id, keepLoaded);
        return true;
    }

    public static void init()
    {
        if (hasInit)
        {
            return;
        }
        registerProviderType( 0, WorldProviderSurface.class, true);
        registerProviderType(-1, WorldProviderHell.class,    true);
        registerProviderType( 1, WorldProviderEnd.class,     false);
        registerDimension( 0,  0);
        registerDimension(-1, -1);
        registerDimension( 1,  1);
    }

    public static void registerDimension(int id, int providerType)
    {
        if (!providers.containsKey(providerType))
        {
            throw new IllegalArgumentException(String.format("Failed to register dimensiuon for id %d, provider type %d does not exist", id, providerType));
        }
        if (dimensions.containsKey(id))
        {
            throw new IllegalArgumentException(String.format("Failed to register dimensiuon for id %d, One is already registered", id));
        }
        dimensions.put(id, providerType);
    }

    public static int getProviderType(int dim)
    {
        if (!dimensions.containsKey(dim))
        {
            throw new IllegalArgumentException(String.format("Could not get provider type for dimension %d, does not exist", dim));
        }
        return dimensions.get(dim);
    }

    public static WorldProvider getProvider(int dim)
    {
        return getWorld(dim).provider;
    }

    public static Integer[] getIDs()
    {
        return dimensions.keySet().toArray(new Integer[0]);
    }

    public static void setWorld(int id, WorldServer world)
    {
        worlds.put(id, world);

        ArrayList<WorldServer> tmp = new ArrayList<WorldServer>();
        tmp.add(worlds.get( 0));
        tmp.add(worlds.get(-1));
        tmp.add(worlds.get( 1));

        for (Entry<Integer, WorldServer> entry : worlds.entrySet())
        {
            int dim = entry.getKey();
            if (dim >= -1 && dim <= 1)
            {
                continue;
            }
            tmp.add(entry.getValue());
        }

        MinecraftServer.getServer().worldServers = tmp.toArray(new WorldServer[0]);
        MinecraftServer.getServer().worldTickTimes.put(id, new long[100]);
    }

    public static WorldServer getWorld(int id)
    {
        return worlds.get(id);
    }

    public static WorldServer[] getWorlds()
    {
        return worlds.values().toArray(new WorldServer[0]);
    }

    public static boolean shouldLoadSpawn(int dim)
    {
        int id = getProviderType(dim);
        return spawnSettings.contains(id) && spawnSettings.get(id);
    }

    static
    {
        init();
    }

    public static WorldProvider createProviderFor(int dim)
    {
        try
        {
            if (dimensions.containsKey(dim))
            {
                WorldProvider provider = providers.get(getProviderType(dim)).newInstance();
                provider.setDimension(dim);
                return provider;
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE,String.format("An error occured trying to create an instance of WorldProvider %d (%s)",
                    dim, providers.get(getProviderType(dim)).getSimpleName()),e);
            throw new RuntimeException(e);
        }
    }
}
