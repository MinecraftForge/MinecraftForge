/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks.SeedEntry;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;

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
    public static final IEventBus EVENT_BUS = IEventBus.create();
    public static final IEventBus TERRAIN_GEN_BUS = IEventBus.create();
    public static final IEventBus ORE_GEN_BUS = IEventBus.create();

    static final ForgeInternalHandler INTERNAL_HANDLER = new ForgeInternalHandler();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FORGE = MarkerManager.getMarker("FORGE");

    /**
     * Register a new seed to be dropped when breaking tall grass.
     *
     * @param seed The item to drop as a seed.
     * @param weight The relative probability of the seeds,
     *               where wheat seeds are 10.
     *
     * Note: These functions may be going away soon, we're looking into loot tables....
     */
    public static void addGrassSeed(@Nonnull ItemStack seed, int weight)
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
       LOGGER.info(FORGE,"MinecraftForge v{} Initialized", ForgeVersion.getVersion());

       UsernameCache.load();
       // Load before all the mods, so MC owns the MC fluids
       FluidRegistry.validateFluidRegistry();
       ForgeHooks.initTools();

       //For all the normal CrashReport classes to be defined. We're in MC's classloader so this should all be fine
       new CrashReport("ThisIsFake", new Exception("Not real"));
   }




/*
   public static void preloadCrashClasses(ASMDataTable table, String modID, Set<String> classes)
   {
       //Find all ICrashReportDetail's handlers and preload them.
       List<String> all = Lists.newArrayList();
       for (ASMData asm : table.getAll(ICrashReportDetail.class.getName().replace('.', '/')))
           all.add(asm.getClassName());
       for (ASMData asm : table.getAll(ICrashCallable.class.getName().replace('.', '/')))
           all.add(asm.getClassName());

       all.retainAll(classes);

       if (all.size() == 0)
        return;

       ForgeMod.log.debug("Preloading CrashReport Classes");
       Collections.sort(all); //Sort it because I like pretty output ;)
       for (String name : all)
       {
           ForgeMod.log.debug("\t{}", name);
           try
           {
               Class.forName(name.replace('/', '.'), false, MinecraftForge.class.getClassLoader());
           }
           catch (Exception e)
           {
               FMLLog.log.error("Could not find class for name '{}'.", name, e);
           }
       }
   }
*/
}
