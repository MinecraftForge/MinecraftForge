package net.minecraftforge.fluids;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.RegistryDelegate;

/**
 * Handles Fluid registrations. Fluids MUST be registered in order to function.
 *
 * @author King Lemming, CovertJaguar (LiquidDictionary)
 *
 */
public abstract class FluidRegistry
{
    static int maxID = 0;

    static BiMap<String, Fluid> fluids = HashBiMap.create();
    static BiMap<Fluid, Integer> fluidIDs = HashBiMap.create();
    static BiMap<Integer, String> fluidNames = HashBiMap.create(); //Caching this just makes some other calls faster
    static BiMap<Block, Fluid> fluidBlocks;

    // the globally unique fluid map - only used to associate non-defaults during world/server loading
    static BiMap<String,Fluid> masterFluidReference = HashBiMap.create();
    static BiMap<String,String> defaultFluidName = HashBiMap.create();
    static Map<Fluid,FluidDelegate> delegates = Maps.newHashMap();

    public static final Fluid WATER = new Fluid("water") {
        @Override
        public String getLocalizedName() {
            return StatCollector.translateToLocal("tile.water.name");
        }
    }.setBlock(Blocks.water).setUnlocalizedName(Blocks.water.getUnlocalizedName());

    public static final Fluid LAVA = new Fluid("lava") {
        @Override
        public String getLocalizedName() {
            return StatCollector.translateToLocal("tile.lava.name");
        }
    }.setBlock(Blocks.lava).setLuminosity(15).setDensity(3000).setViscosity(6000).setTemperature(1300).setUnlocalizedName(Blocks.lava.getUnlocalizedName());

    public static int renderIdFluid = -1;

    static
    {
        registerFluid(WATER);
        registerFluid(LAVA);
    }

    private FluidRegistry(){}

    /**
     * Called by Forge to prepare the ID map for server -> client sync.
     * Modders, DO NOT call this.
     */
    public static void initFluidIDs(BiMap<Fluid, Integer> newfluidIDs, Set<String> defaultNames)
    {
        maxID = newfluidIDs.size();
        loadFluidDefaults(newfluidIDs, defaultNames);
    }

    /**
     * Called by forge to load default fluid IDs from the world or from server -> client for syncing
     * DO NOT call this and expect useful behaviour.
     * @param newfluidIDs
     */
    private static void loadFluidDefaults(BiMap<Fluid, Integer> localFluidIDs, Set<String> defaultNames)
    {
        // If there's an empty set of default names, use the defaults as defined locally
        if (defaultNames.isEmpty()) {
            defaultNames.addAll(defaultFluidName.values());
        }
        BiMap<String, Fluid> localFluids = HashBiMap.create(fluids);
        for (String defaultName : defaultNames)
        {
            Fluid fluid = masterFluidReference.get(defaultName);
            if (fluid == null) {
                String derivedName = defaultName.split(":",2)[1];
                String localDefault = defaultFluidName.get(derivedName);
                if (localDefault == null) {
                    FMLLog.getLogger().log(Level.ERROR, "The fluid {} (specified as {}) is missing from this instance - it will be removed", derivedName, defaultName);
                    continue;
                }
                fluid = masterFluidReference.get(localDefault);
                FMLLog.getLogger().log(Level.ERROR, "The fluid {} specified as default is not present - it will be reverted to default {}", defaultName, localDefault);
            }
            FMLLog.getLogger().log(Level.DEBUG, "The fluid {} has been selected as the default fluid for {}", defaultName, fluid.getName());
            Fluid oldFluid = localFluids.put(fluid.getName(), fluid);
            Integer id = localFluidIDs.remove(oldFluid);
            localFluidIDs.put(fluid, id);
        }
        BiMap<Integer, String> localFluidNames = HashBiMap.create();
        for (Entry<Fluid, Integer> e : localFluidIDs.entrySet()) {
            localFluidNames.put(e.getValue(), e.getKey().getName());
        }
        fluidIDs = localFluidIDs;
        fluids = localFluids;
        fluidNames = localFluidNames;
        fluidBlocks = null;
        for (FluidDelegate fd : delegates.values())
        {
            fd.rebind();
        }
    }

    /**
     * Register a new Fluid. If a fluid with the same name already exists, registration the alternative fluid is tracked
     * in case it is the default in another place
     *
     * @param fluid
     *            The fluid to register.
     * @return True if the fluid was registered as the current default fluid, false if it was only registered as an alternative
     */
    public static boolean registerFluid(Fluid fluid)
    {
        masterFluidReference.put(uniqueName(fluid), fluid);
        delegates.put(fluid, new FluidDelegate(fluid, fluid.getName()));
        if (fluids.containsKey(fluid.getName()))
        {
            return false;
        }
        fluids.put(fluid.getName(), fluid);
        maxID++;
        fluidIDs.put(fluid, maxID);
        fluidNames.put(maxID, fluid.getName());
        defaultFluidName.put(fluid.getName(), uniqueName(fluid));

        MinecraftForge.EVENT_BUS.post(new FluidRegisterEvent(fluid.getName(), maxID));
        return true;
    }

    private static String uniqueName(Fluid fluid)
    {
        ModContainer activeModContainer = Loader.instance().activeModContainer();
        String activeModContainerName = activeModContainer == null ? "minecraft" : activeModContainer.getModId();
        return activeModContainerName+":"+fluid.getName();
    }

    /**
     * Is the supplied fluid the current default fluid for it's name
     * @param fluid the fluid we're testing
     * @return if the fluid is default
     */
    public static boolean isFluidDefault(Fluid fluid)
    {
        return fluids.containsValue(fluid);
    }

    /**
     * Does the supplied fluid have an entry for it's name (whether or not the fluid itself is default)
     * @param fluid the fluid we're testing
     * @return if the fluid's name has a registration entry
     */
    public static boolean isFluidRegistered(Fluid fluid)
    {
        return fluid != null && fluids.containsKey(fluid.getName());
    }

    public static boolean isFluidRegistered(String fluidName)
    {
        return fluids.containsKey(fluidName);
    }

    public static Fluid getFluid(String fluidName)
    {
        return fluids.get(fluidName);
    }

    public static Fluid getFluid(int fluidID)
    {
        return fluidIDs.inverse().get(fluidID);
    }

    public static int getFluidID(Fluid fluid)
    {
        return fluidIDs.get(fluid);
    }

    public static int getFluidID(String fluidName)
    {
        return fluidIDs.get(getFluid(fluidName));
    }

    @Deprecated //Remove in 1.8.3
    public static String getFluidName(int fluidID)
    {
        return fluidNames.get(fluidID);
    }

    public static String getFluidName(Fluid fluid)
    {
        return fluids.inverse().get(fluid);
    }

    public static String getFluidName(FluidStack stack)
    {
        return getFluidName(stack.getFluid());
    }

    public static FluidStack getFluidStack(String fluidName, int amount)
    {
        if (!fluids.containsKey(fluidName))
        {
            return null;
        }
        return new FluidStack(getFluid(fluidName), amount);
    }

    /**
     * Returns a read-only map containing Fluid Names and their associated Fluids.
     */
    public static Map<String, Fluid> getRegisteredFluids()
    {
        return ImmutableMap.copyOf(fluids);
    }

    /**
     * Returns a read-only map containing Fluid Names and their associated IDs.
     */
    @Deprecated //Change return type to <Fluid, Integer> in 1.8.3
    public static Map<String, Integer> getRegisteredFluidIDs()
    {
        return ImmutableMap.copyOf(fluidNames.inverse());
    }

    /**
     * Returns a read-only map containing Fluid IDs and their associated Fluids.
     * In 1.8.3, this will change to just 'getRegisteredFluidIDs'
     */
    public static Map<Fluid, Integer> getRegisteredFluidIDsByFluid()
    {
        return ImmutableMap.copyOf(fluidIDs);
    }

    public static Fluid lookupFluidForBlock(Block block)
    {
        if (fluidBlocks == null)
        {
            BiMap<Block, Fluid> tmp = HashBiMap.create();
            for (Fluid fluid : fluids.values())
            {
                if (fluid.canBePlacedInWorld() && fluid.getBlock() != null)
                {
                    tmp.put(fluid.getBlock(), fluid);
                }
            }
            fluidBlocks = tmp;
        }
        return fluidBlocks.get(block);
    }

    public static class FluidRegisterEvent extends Event
    {
        public final String fluidName;
        public final int fluidID;

        public FluidRegisterEvent(String fluidName, int fluidID)
        {
            this.fluidName = fluidName;
            this.fluidID = fluidID;
        }
    }

    public static int getMaxID()
    {
        return maxID;
    }

    public static String getDefaultFluidName(Fluid key)
    {
        String name = masterFluidReference.inverse().get(key);
        if (Strings.isNullOrEmpty(name)) {
            FMLLog.getLogger().log(Level.ERROR, "The fluid registry is corrupted. A fluid {} {} is not properly registered. The mod that registered this is broken", key.getClass().getName(), key.getName());
            throw new IllegalStateException("The fluid registry is corrupted");
        }
        return name;
    }

    public static void loadFluidDefaults(NBTTagCompound tag)
    {
        Set<String> defaults = Sets.newHashSet();
        if (tag.hasKey("DefaultFluidList",9))
        {
            FMLLog.getLogger().log(Level.DEBUG, "Loading persistent fluid defaults from world");
            NBTTagList tl = tag.getTagList("DefaultFluidList", 8);
            for (int i = 0; i < tl.tagCount(); i++)
            {
                defaults.add(tl.getStringTagAt(i));
            }
        }
        else
        {
            FMLLog.getLogger().log(Level.DEBUG, "World is missing persistent fluid defaults - using local defaults");
        }
        loadFluidDefaults(HashBiMap.create(fluidIDs), defaults);
    }

    public static void writeDefaultFluidList(NBTTagCompound forgeData)
    {
        NBTTagList tagList = new NBTTagList();

        for (Entry<String, Fluid> def : fluids.entrySet())
        {
            tagList.appendTag(new NBTTagString(getDefaultFluidName(def.getValue())));
        }

        forgeData.setTag("DefaultFluidList", tagList);
    }

    public static void validateFluidRegistry()
    {
        Set<Fluid> illegalFluids = Sets.newHashSet();
        for (Fluid f : fluids.values())
        {
            if (!masterFluidReference.containsValue(f))
            {
                illegalFluids.add(f);
            }
        }

        if (!illegalFluids.isEmpty())
        {
            FMLLog.getLogger().log(Level.FATAL, "The fluid registry is corrupted. Something has inserted a fluid without registering it");
            FMLLog.getLogger().log(Level.FATAL, "There is {} unregistered fluids", illegalFluids.size());
            for (Fluid f: illegalFluids)
            {
                FMLLog.getLogger().log(Level.FATAL, "  Fluid name : {}, type: {}", f.getName(), f.getClass().getName());
            }
            FMLLog.getLogger().log(Level.FATAL, "The mods that own these fluids need to register them properly");
            throw new IllegalStateException("The fluid map contains fluids unknown to the master fluid registry");
        }
    }

    static RegistryDelegate<Fluid> makeDelegate(Fluid fl)
    {
        return delegates.get(fl);
    }


    private static class FluidDelegate implements RegistryDelegate<Fluid>
    {
        private String name;
        private Fluid fluid;

        FluidDelegate(Fluid fluid, String name)
        {
            this.fluid = fluid;
            this.name = name;
        }

        @Override
        public Fluid get()
        {
            return fluid;
        }

        @Override
        public String name()
        {
            return name;
        }

        @Override
        public Class<Fluid> type()
        {
            return Fluid.class;
        }

        void rebind()
        {
            fluid = fluids.get(name);
        }
    }
}