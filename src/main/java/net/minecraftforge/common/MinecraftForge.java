package net.minecraftforge.common;

import java.util.concurrent.Callable;

import com.google.common.collect.ObjectArrays;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.crash.CrashReport;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks.SeedEntry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class MinecraftForge
{
    /**
     * The core Forge EventBusses, all events for Forge will be fired on these,
     * you should use this to register all your listeners.
     * This replaces every register*Handler() function in the old version of Forge.
     * TERRAIN_GEN_BUS for terrain gen events
     * ORE_GEN_BUS for ore gen events
     * EVENT_BUS for everything else
     */
    public static final EventBus EVENT_BUS = new EventBus();
    public static final EventBus TERRAIN_GEN_BUS = new EventBus();
    public static final EventBus ORE_GEN_BUS = new EventBus();
    public static final String MC_VERSION = Loader.MC_VERSION;

    static final ForgeInternalHandler INTERNAL_HANDLER = new ForgeInternalHandler();

    /**
     * Register a new seed to be dropped when breaking tall grass.
     *
     * @param seed The item to drop as a seed.
     * @param weight The relative probability of the seeds,
     *               where wheat seeds are 10.
     *
     * Note: These functions may be going away soon, we're looking into loot tables....
     */
    public static void addGrassSeed(ItemStack seed, int weight)
    {
        addGrassSeed(new SeedEntry(seed, weight));
    }
    public static void addGrassSeed(SeedEntry seed)
    {
        ForgeHooks.seedList.add(seed);
    }

   /**
    * Method invoked by FML before any other mods are loaded.
    */
   public static void initialize()
   {
       FMLLog.info("MinecraftForge v%s Initialized", ForgeVersion.getVersion());

       OreDictionary.getOreName(0);

       UsernameCache.load();
       // Load before all the mods, so MC owns the MC fluids
       FluidRegistry.validateFluidRegistry();
   }
}
