package net.minecraft.src.forge.idrequest;

import net.minecraft.src.BaseMod;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.IRecipe;
import net.minecraft.src.Item;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.World;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.IConnectionHandler;
import net.minecraft.src.forge.ISaveEventHandler;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.Property;
import net.minecraft.src.forge.oredict.OreDictionary;
import net.minecraft.src.forge.packets.PacketConfig;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * Implements a centralized block/item ID registration system.
 * 
 * Mods should register their intention to use an ID in BaseMod.load() by calling
 * IDResolver.requestID - this cannot be done any later.
 * 
 * When a world is loaded or a server is connected to, registerIDsFromConfig is called
 * with the world-specific ID configuration file. It first calls recipe reset callbacks,
 * then registers all IDs specified in the config, then auto-assigns IDs not specified
 * in the config, then calls recipe callbacks.
 * 
 * When loading a server's IDs, IDs not specified in the config will still be
 * autoassigned to prevent problems.
 * 
 * Note: Different IDs can be requested using the same name and
 * mod but different types, as some things require multiple IDs, eg separate block
 * and item IDs.
 * 
 * Ores registered during registerIDsFromConfig (in an IIDCallback or a recipe callback)
 * will be considered to have changeable IDs, and will not be passed to IOreHandlers
 * unless in compatibility mode. They will be usable in ore recipes.
 * 
 */
public class IDRequestRegistry {
    
    public static class DuplicateRequestException extends RuntimeException
    {
        public DuplicateRequestException(RequestedIDKey key)
        {
            super("Duplicate request for ID: " + key);
        }
    }

    /**
     * 
     * A type of ID that can be requested through this API.
     *
     */
    public static enum Type
    {
        BLOCK(Configuration.CATEGORY_BLOCK, 0, 4095),
        ITEM(Configuration.CATEGORY_ITEM, 8192, 31999);
        
        private Type(String configCategory, int min, int max)
        {
            this.configCategory = configCategory;
            this.min = min;
            this.max = max;
        }
        
        public final String configCategory;
        public final int min;
        public final int max;
    }
    
    /**
     * Uniquely identifies an ID request by the requesting mod, the name,
     * and the type of ID.
     */
    private static class RequestedIDKey implements Comparable<RequestedIDKey>
    {
        public final BaseMod mod;
        public final String name;
        public final Type type;
        private String configName;
        
        public RequestedIDKey(BaseMod mod, String name, Type type)
        {
            this.mod = mod;
            this.name = name;
            this.type = type;
            
            String modClassName = mod.getClass().getSimpleName();
            if(modClassName.startsWith("mod_"))
                modClassName = modClassName.substring(4);
            
            configName = modClassName.replace('_', '.') + "." + name;
        }
        
        /**
         * @return The name of the config option that sets this ID.
         */
        public String getConfigName()
        {
            return configName;
        }
        
        @Override
        public boolean equals(Object o)
        {
            try
            {
                RequestedIDKey okey = (RequestedIDKey)o;
                return okey.mod == mod && okey.name.equals(name) && okey.type == type;
            }
            catch(ClassCastException e)
            {
                return false;
            }
        }
        
        @Override
        public int hashCode()
        {
            return mod.hashCode() + (name.hashCode() << 5) + (type.hashCode() << 10);
        }
        
        @Override
        public String toString()
        {
            return type.name()+" "+configName;
        }

        /**
         * If the ID is set in the config, returns it.
         * If not, returns Integer.MIN_VALUE
         */
        public int getFromConfig(Configuration config)
        {
            Map<String, Property> category = config.categories.get(type.configCategory);
            if(category == null)
                return Integer.MIN_VALUE;
            
            Property property = category.get(getConfigName());
            if(property == null)
                return Integer.MIN_VALUE;
            
            int id;
            
            try
            {
                id = Integer.parseInt(property.value);
            }
            catch(NumberFormatException ex)
            {
                return Integer.MIN_VALUE;
            }
            
            if(id < type.min || id > type.max)
                return Integer.MIN_VALUE;
            
            return id;
        }

        @Override
        public int compareTo(RequestedIDKey arg0) {
            if(type != arg0.type)
                return type.ordinal() - arg0.type.ordinal();
            if(!mod.toString().equals(arg0.mod.toString()))
                return mod.toString().compareTo(arg0.mod.toString());
            return name.compareTo(arg0.name);
        }
    }
    
    private static class RequestedIDData
    {
        private final IIDCallback callback;
        public final RequestedIDKey key;
        private int currentID = -1; // -1 = not currently registered
        
        public RequestedIDData(RequestedIDKey key, IIDCallback callback) 
        {
            this.key = key;
            this.callback = callback;
        }
        
        // Checks the blocksList/itemsList arrays to see
        // if there is actually something registered at this ID
        public boolean isActuallyRegistered()
        {
            if(!isRegistered())
                return false;
            switch(key.type)
            {
            case ITEM: case BLOCK:
                return (currentID < Block.blocksList.length && Block.blocksList[currentID] != null) || Item.itemsList[currentID] != null;
            default:
                throw new UnsupportedOperationException("TODO: Implement me for "+key.type.name()+" IDs!");
            }
        }
        
        public boolean isRegistered()
        {
            return currentID != -1;
        }
        public int getID()
        {
            return currentID;
        }
        public void setID(int newID)
        {
            if(currentID == newID)
                return;
            if(currentID != -1)
            {
                callback.unregister(key.name, currentID);
                if(isActuallyRegistered())
                {
                    throw new RuntimeException(key+" didn't unregister properly. Contact the mod author for a fix.");
                }
            }
            currentID = newID;
            if(currentID != -1)
            {
                callback.register(key.name, currentID);
                if(!isActuallyRegistered())
                {
                    String message = "";
                    if(key.type == Type.ITEM)
                        message = " If you are the mod author, did you remember to subtract 256?";
                    throw new RuntimeException(key+" didn't register properly. Contact the mod author for a fix." + message);
                }
            }
        }
    }
    
    private static Map<RequestedIDKey, RequestedIDData> requestedIDList = new TreeMap<RequestedIDKey, RequestedIDData>();
    
    /**
     * Forge internal method - do not call this from a mod.
     * 
     * Disables new requests from mods.
     */
    public static void disableRequesting() {
        cannotRequest = true;
    }
    
    static Configuration idConfig = null;
    private static boolean cannotRequest = false;
    
    public static void requestID(BaseMod mod, String name, Type type, IIDCallback callback)
    {
        if(cannotRequest)
        {
            throw new IllegalStateException("Too late to request IDs now! Use BaseMod.load()");
        }
        if(mod == null)
        {
            throw new NullPointerException("mod");
        }
        if(name == null)
        {
            throw new NullPointerException("name");
        }
        if(type == null)
        {
            throw new NullPointerException("type");
        }
        if(callback == null)
        {
            throw new NullPointerException("callback");
        }
        
        RequestedIDKey key = new RequestedIDKey(mod, name, type);
        
        if(requestedIDList.containsKey(key))
        {
            throw new DuplicateRequestException(key);
        }
        
        requestedIDList.put(key, new RequestedIDData(key, callback));
    }
    
    private static List<Runnable> recipeCallbacks = new LinkedList<Runnable>();
    private static List<Runnable> recipeResetCallbacks = new LinkedList<Runnable>();
    
    /**
     * Forge internal method - do not call this from a mod.
     * 
     * Does the ID assignment and registration.
     * 
     * First all existing registered IDs are deregistered.
     * Next all IDs that are listed in the config are registered.
     * Then all IDs that are not listed in the config are allocated and registered,
     * if isSlave is false.
     * Last, all recipe and recipe reset callbacks are called.
     * 
     * @param idConfig The configuration file to use.
     * @param isSlave True if we cannot change the configuration - for example if it was downloaded from a server.
     */
    public static void registerIDs(Configuration idConfig, boolean isSlave)
    {
        assert cannotRequest : "registerIDs called at the wrong time?";
        
        Set<RequestedIDData> pending = new HashSet<RequestedIDData>(requestedIDList.values());
        Iterator<RequestedIDData> iter;
        
        OreDictionary.setDynamicRegistration(true);
        OreDictionary.clearDynamicOres();
        
        // unregister everything
        iter = pending.iterator();
        while(iter.hasNext())
        {
            RequestedIDData thing = iter.next();
            if(thing.isRegistered())
            {
                thing.setID(-1);
            }
        }
        
        // register things that are already set in the config
        iter = pending.iterator();
        while(iter.hasNext())
        {
            RequestedIDData thing = iter.next();
            int id = thing.key.getFromConfig(idConfig);
            
            if(id != Integer.MIN_VALUE && !isIDOccupied(thing.key.type, id))
            {
                thing.setID(id);
                iter.remove();
            }
        }
        
        // Now assign IDs for things that still don't have one.
        // First, make a list of all block and item IDs in the config, 
        // used or not, and try to avoid them - maybe they're from mods 
        // that are currently uninstalled but will be reinstalled later.
        Set<Integer> avoidIDs = new HashSet<Integer>();
        for(Property prop : idConfig.blockProperties.values())
        {
            try
            {
                avoidIDs.add(Integer.parseInt(prop.value));
            }
            catch(NumberFormatException e) {}
        }
        for(Property prop : idConfig.itemProperties.values())
        {
            try
            {
                avoidIDs.add(Integer.parseInt(prop.value));
            }
            catch(NumberFormatException e) {}
        }
        
        // TODO: would it would be practical to not register
        // things that don't exist on the server? Maybe when
        // we have dynamic mod unloading...
        //if(!isSlave)
        {
            // Now assign an ID to everything that doesn't have one
            iter = pending.iterator();
            while(iter.hasNext())
            {
                RequestedIDData thing = iter.next();
                
                int newID = allocateNewID(thing.key.type, avoidIDs);
                thing.setID(newID);
                
                Property property = new Property();
                property.name = thing.key.getConfigName();
                property.value = String.valueOf(newID);
                
                Map<String, Property> category = idConfig.categories.get(thing.key.type.configCategory);
                if(category == null)
                {
                    category = new TreeMap<String, Property>();
                    idConfig.categories.put(thing.key.type.configCategory, category);
                }
                
                category.put(property.name, property);
            }
            
            pending.clear();
        }
        
        if(!isSlave)
            idConfig.save();
        
        // Now do recipes. First reset the recipe list by calling each reset callback.
        for(Runnable r : recipeResetCallbacks)
        {
            r.run();
        }
        // then add recipes by calling each recipe callback.
        for(Runnable r : recipeCallbacks)
        {
            r.run();
        }
        
        OreDictionary.setDynamicRegistration(false);
    }
    
    private static boolean isIDOccupied(Type type, int id)
    {
        switch(type)
        {
        case BLOCK: case ITEM:
            if(Item.itemsList[id] != null)
                return true;
            if(id < Block.blocksList.length && Block.blocksList[id] != null)
                return true;
            return false;
        
        default:
            throw new RuntimeException("isIDOccupied not implemented for "+type.name()+" IDs! This is a forge bug.");
        }
    }

    private static int allocateNewID(Type type, Set<Integer> avoidIDs)
    {
        // First look for IDs not in avoidIDs
        for(int testID = type.max; testID >= type.min; testID--)
            if(!avoidIDs.contains(testID) && !isIDOccupied(type, testID))
                return testID;
        
        // Otherwise, look for any ID
        for(int testID = type.max; testID >= type.min; testID--)
            if(!isIDOccupied(type, testID))
                return testID;
        
        outOfIDs(type);
        return -1;
    }
    
    private static void outOfIDs(Type type)
    {
        // TODO: replace with better error message (on the client,
        // probably something like GuiConflictWarning with a clear
        // error message)
        // Although this should be unlikely now that we have 4096 block IDs.
        throw new RuntimeException("Out of "+type.name()+" IDs!");
    }

    /**
     * Registers a callback to be called when recipes are created.
     * Forge will need to reload recipes whenever block or item IDs change.
     * Must be called in load().
     * 
     * Note that recipe callbacks are not limited to adding recipes,
     * and should register anything which is specific to an ID.
     * 
     * @param runnable The callback object whose run() method will be called to add recipes.
     */
    public static void addRecipeCallback(Runnable runnable)
    {
        if(cannotRequest)
        {
            throw new IllegalStateException("Too late to add recipe callbacks! Use BaseMod.load()");
        }
        recipeCallbacks.add(runnable);
    }
    
    /**
     * Registers a callback to be called to reset custom recipes.
     * Called before recipe callbacks.
     * Recipes existing before the callback is first called should be saved
     * then restored on subsequent calls, for compatibility with mods that
     * do not use the ID request API.
     * 
     * (Eg: BuildCraft refinery recipes, Forestry carpenter recipes, RP2 alloy furnace recipes)
     * @param runnable The callback object whose run() method will be called to reset recipes.
     */
    public static void addRecipeResetCallback(Runnable runnable)
    {
        if(cannotRequest)
        {
            throw new IllegalStateException("Too late to add recipe reset callbacks! Use BaseMod.load()");
        }
        recipeResetCallbacks.add(runnable);
    }
    
    private static Configuration currentConfig = null;
    
    /**
     * Forge internal method - do not call this from a mod.
     * 
     * Currently adds the recipe reset callbacks for smelting
     * and crafting recipes.
     */
    public static void onForgeLoad() {
        // Crafting recipes
        addRecipeResetCallback(new Runnable()
        {
            List<IRecipe> nonCallbackRecipes = null;
            public void run()
            {
                List<IRecipe> recipeList = (List<IRecipe>)CraftingManager.getInstance().getRecipeList();
                if(nonCallbackRecipes == null)
                    nonCallbackRecipes = new LinkedList<IRecipe>(recipeList);
                else
                {
                    recipeList.clear();
                    recipeList.addAll(nonCallbackRecipes);
                }
            }
        });
        
        // Smelting recipes
        addRecipeResetCallback(new Runnable()
        {
            List<Map.Entry> nonCallbackRecipes = null;
            public void run()
            {
                Map smelting = FurnaceRecipes.smelting().getSmeltingList();
                if(nonCallbackRecipes == null)
                    nonCallbackRecipes = new LinkedList<Map.Entry>(smelting.entrySet());
                else
                {
                    smelting.clear();
                    for(Map.Entry e : nonCallbackRecipes)
                        smelting.put(e.getKey(), e.getValue());
                }
            }
        });
        
        MinecraftForge.registerSaveHandler(new ISaveEventHandler() {
            
            private File currentWorld = null;
            
            @Override
            public void onWorldSave(World world) {
            }
            
            @Override
            public void onWorldLoad(World world) {
                File dummyMapFile = world.getSaveHandler().getMapFileFromName("");
                if(dummyMapFile == null)
                {
                    // client world
                    return;
                }
                
                File worldFolder = dummyMapFile.getParentFile().getParentFile();
                
                if(currentWorld == null || !currentWorld.equals(worldFolder))
                {
                    currentWorld = worldFolder;
                    
                    File idConfigFile = new File(dummyMapFile, "../../forge-ids.txt");
                    
                    currentConfig = new Configuration(idConfigFile);
                    currentConfig.load();
                    registerIDs(currentConfig, false);
                }
            }
            
            @Override
            public void onChunkUnload(World world, Chunk chunk) {
            }
            
            @Override
            public void onChunkSaveData(World world, Chunk chunk, NBTTagCompound data) {
            }
            
            @Override
            public void onChunkLoadData(World world, Chunk chunk, NBTTagCompound data) {
            }
            
            @Override
            public void onChunkLoad(World world, Chunk chunk) {
            }
        });
        
        
        if(!MinecraftForge.isClient())
        {
            // Server needs to send IDs to anyone who joins
            MinecraftForge.registerConnectionHandler(new IConnectionHandler()
            {
                @Override
                public void onConnect(NetworkManager network) {
                }

                @Override
                public void onLogin(NetworkManager network, Packet1Login login) {
                    PacketConfig packet = new PacketConfig();
                    packet.name = "forge-ids";
                    packet.config = currentConfig;
                    network.addToSendQueue(packet.getPacket());
                }

                @Override
                public void onDisconnect(NetworkManager network, String message, Object[] args) {
                }
            });
        }

    }
}
