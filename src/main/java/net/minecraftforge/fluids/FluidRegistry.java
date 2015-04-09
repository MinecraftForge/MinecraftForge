package net.minecraftforge.fluids;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.Event;

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
    public static void initFluidIDs(BiMap<Fluid, Integer> newfluidIDs)
    {
        maxID = newfluidIDs.size();
        fluidIDs.clear();
        fluidIDs.putAll(newfluidIDs);
        fluidNames.clear();
        for (Entry<Fluid, Integer> e : fluidIDs.entrySet())
            fluidNames.put(e.getValue(), e.getKey().getName());
    }

    /**
     * Register a new Fluid. If a fluid with the same name already exists, registration is denied.
     *
     * @param fluid
     *            The fluid to register.
     * @return True if the fluid was successfully registered; false if there is a name clash.
     */
    public static boolean registerFluid(Fluid fluid)
    {
        if (fluids.containsKey(fluid.getName()))
        {
            FMLLog.bigWarning("Duplicate registration attempt for fluid %s (type %s) has occurred. This is not a problem itself, but subsequent failed FluidStacks might be a result if not handled properly", fluid.getName(), fluid.getClass().getName());
            fluidIDs.put(fluid, getFluidID(fluid.getName()));
            return false;
        }
        fluids.put(fluid.getName(), fluid);
        fluidIDs.put(fluid, ++maxID);
        fluidNames.put(maxID, fluid.getName());

        MinecraftForge.EVENT_BUS.post(new FluidRegisterEvent(fluid.getName(), maxID));
        return true;
    }

    public static boolean isFluidRegistered(Fluid fluid)
    {
        return fluids.containsKey(fluid.getName());
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
        return getFluidName(stack.fluid);
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
}
