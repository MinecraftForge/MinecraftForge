package net.minecraftforge.common;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.Level;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ObjectArrays;
import com.google.common.io.Resources;

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
     */
    public static void addGrassSeed(ItemStack seed, int weight)
    {
        ForgeHooks.seedList.add(new SeedEntry(seed, weight));
    }

   /**
    * Method invoked by FML before any other mods are loaded.
    */
   public static void initialize()
   {
        FMLLog.info("MinecraftForge v%s Initialized", ForgeVersion.getVersion());

        OreDictionary.getOreName(0);

        // Force these classes to be defined, Should prevent derp error hiding.
        CrashReport fake = new CrashReport("ThisIsFake", new Exception("Not real"));
        // Lets init World's crash report inner classes to prevent them from
        // hiding errors.
        try
        {
            Side side = FMLCommonHandler.instance().getSide();
            Iterable<String> lines = Splitter.on('\n').trimResults().split(Resources.toString(MinecraftForge.class.getResource("/preload_classes.cfg"), Charsets.UTF_8));
            Splitter clSplitter = Splitter.on(':');
            for (String line : lines)
            {
                if (line.startsWith("#"))
                    continue;
                List<String> split = clSplitter.splitToList(line);
                if (split.size() != 2)
                    continue;

                String lineSide = split.get(0);
                String className = split.get(1);
                if (lineSide.equals("COM") || lineSide.equals("CLI") == side.isClient())
                {
                    try
                    {
                        Class<?> cls = Class.forName(className, false, MinecraftForge.class.getClassLoader());
                        if (cls != null && !Callable.class.isAssignableFrom(cls))
                        {
                            // FMLLog.info("\t% s is not a instance of callable!",
                            // s);
                        }
                    } catch (Exception e) {}
                }
            }
        } catch (Throwable ex)
        {
            FMLLog.log(Level.ERROR, ex, "Failed to preload crash-report callables!");
        }

        UsernameCache.load();
        // Load before all the mods, so MC owns the MC fluids
        FluidRegistry.validateFluidRegistry();
   }

   public static String getBrandingVersion()
   {
       return "Minecraft Forge "+ ForgeVersion.getVersion();
   }
}
