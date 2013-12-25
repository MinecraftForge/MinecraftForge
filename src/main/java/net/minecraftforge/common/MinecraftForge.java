package net.minecraftforge.common;

import java.lang.reflect.Constructor;
import java.util.*;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks.SeedEntry;
import net.minecraftforge.event.entity.EntityEvent;
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
    public static final String MC_VERSION = "1.7.2";

    private static final ForgeInternalHandler INTERNAL_HANDLER = new ForgeInternalHandler();

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

       EVENT_BUS.register(INTERNAL_HANDLER);
       OreDictionary.getOreName(0);

       if (!ForgeModContainer.disableVersionCheck)
       {
           ForgeVersion.startVersionCheck();
       }

       //Force these classes to be defined, Should prevent derp error hiding.
       new CrashReport("ThisIsFake", new Exception("Not real"));
   }

   public static String getBrandingVersion()
   {
       return "Minecraft Forge "+ ForgeVersion.getVersion();
   }
}
