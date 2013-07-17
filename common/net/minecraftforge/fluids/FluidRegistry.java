
package net.minecraftforge.fluids;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;

/**
 * Handles Fluid registrations. Fluids MUST be registered in order to function.
 * 
 * @author King Lemming, CovertJaguar (LiquidDictionary)
 * 
 */
public abstract class FluidRegistry
{
    static int maxID = 0;

    static HashMap<String, Fluid> fluids = new HashMap();
    static BiMap<String, Integer> fluidIDs = HashBiMap.create();

    public static final Fluid WATER = new Fluid("water").setBlockID(Block.waterStill.blockID);
    public static final Fluid LAVA = new Fluid("lava").setBlockID(Block.lavaStill.blockID).setLuminosity(15).setDensity(3000).setViscosity(6000);

    public static int renderIdFluid = -1;

    static
    {
        registerFluid(WATER);
        registerFluid(LAVA);
    }

    private FluidRegistry(){}

    /**
     * Called by Forge to prepare the ID map for server -> client sync.
     */
    static void initFluidIDs(BiMap<String, Integer> newfluidIDs)
    {
        maxID = newfluidIDs.size();
        fluidIDs.clear();
        fluidIDs.putAll(newfluidIDs);
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
        if (fluidIDs.containsKey(fluid.getName()))
        {
            return false;
        }
        fluids.put(fluid.getName(), fluid);
        fluidIDs.put(fluid.getName(), ++maxID);

        MinecraftForge.EVENT_BUS.post(new FluidRegisterEvent(fluid.getName(), maxID));
        return true;
    }

    public static boolean isFluidRegistered(Fluid fluid)
    {
        return fluidIDs.containsKey(fluid.getName());
    }

    public static boolean isFluidRegistered(String fluidName)
    {
        return fluidIDs.containsKey(fluidName);
    }

    public static Fluid getFluid(String fluidName)
    {
        return fluids.get(fluidName);
    }

    public static Fluid getFluid(int fluidID)
    {
        return fluids.get(getFluidName(fluidID));
    }

    public static String getFluidName(int fluidID)
    {
        return fluidIDs.inverse().get(fluidID);
    }

    public static String getFluidName(FluidStack stack)
    {
        return getFluidName(stack.fluidID);
    }

    public static int getFluidID(String fluidName)
    {
        return fluidIDs.get(fluidName);
    }

    public static FluidStack getFluidStack(String fluidName, int amount)
    {
        if (!fluidIDs.containsKey(fluidName))
        {
            return null;
        }
        return new FluidStack(getFluidID(fluidName), amount);
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
    public static Map<String, Integer> getRegisteredFluidIDs()
    {
        return ImmutableMap.copyOf(fluidIDs);
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
}
