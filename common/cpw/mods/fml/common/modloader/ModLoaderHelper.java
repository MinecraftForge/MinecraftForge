/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package cpw.mods.fml.common.modloader;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.src.BaseMod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;

/**
 * @author cpw
 *
 */
public class ModLoaderHelper
{
    private static Map<BaseMod, ModLoaderModContainer> notModCallbacks=new HashMap<BaseMod, ModLoaderModContainer>();

    public static IModLoaderSidedHelper sidedHelper;
    public static void updateStandardTicks(BaseMod mod, boolean enable, boolean useClock)
    {
        ModLoaderModContainer mlmc = (ModLoaderModContainer) Loader.instance().activeModContainer();
        BaseModTicker ticker = mlmc.getGameTickHandler();
        EnumSet<TickType> ticks = ticker.ticks();
        // If we're enabled we get render ticks
        if (enable && !useClock) {
            ticks.add(TickType.RENDER);
        } else {
            ticks.remove(TickType.RENDER);
        }
        // If we're enabled but we want clock ticks, or we're server side we get game ticks
        if (enable && (useClock || FMLCommonHandler.instance().getSide().isServer())) {
            ticks.add(TickType.CLIENT);
            ticks.add(TickType.WORLDLOAD);
        } else {
            ticks.remove(TickType.CLIENT);
            ticks.remove(TickType.WORLDLOAD);
        }
    }

    public static void updateGUITicks(BaseMod mod, boolean enable, boolean useClock)
    {
        ModLoaderModContainer mlmc = findOrBuildModContainer(mod);
        EnumSet<TickType> ticks = mlmc.getGUITickHandler().ticks();
        // If we're enabled and we don't want clock ticks we get render ticks
        if (enable && !useClock) {
            ticks.add(TickType.RENDER);
        } else {
            ticks.remove(TickType.RENDER);
        }
        // If we're enabled but we want clock ticks, or we're server side we get world ticks
        if (enable && useClock) {
            ticks.add(TickType.CLIENT);
            ticks.add(TickType.WORLDLOAD);
        } else {
            ticks.remove(TickType.CLIENT);
            ticks.remove(TickType.WORLDLOAD);
        }
    }

    /**
     * @param mod
     * @return
     */
    private static ModLoaderModContainer findOrBuildModContainer(BaseMod mod)
    {
        ModLoaderModContainer mlmc=(ModLoaderModContainer) FMLCommonHandler.instance().findContainerFor(mod);
        if (mlmc==null) {
            mlmc=notModCallbacks.get(mod);
            if (mlmc==null) {
                mlmc=new ModLoaderModContainer(mod);
                notModCallbacks.put(mod, mlmc);
            }
        }
        return mlmc;
    }

    /**
     * @param mod
     * @return
     */
    public static ModLoaderModContainer registerKeyHelper(BaseMod mod)
    {
        ModLoaderModContainer mlmc=findOrBuildModContainer(mod);
        return mlmc;
    }

    public static IPacketHandler buildPacketHandlerFor(BaseMod mod)
    {
        return new ModLoaderPacketHandler(mod);
    }

    public static IWorldGenerator buildWorldGenHelper(BaseModProxy mod)
    {
        return new ModLoaderWorldGenerator(mod);
    }

    public static IFuelHandler buildFuelHelper(BaseModProxy mod)
    {
        return new ModLoaderFuelHelper(mod);
    }

    public static ICraftingHandler buildCraftingHelper(BaseModProxy mod)
    {
        return new ModLoaderCraftingHelper(mod);
    }

    public static void finishModLoading(ModLoaderModContainer mc)
    {
        if (sidedHelper != null)
        {
            sidedHelper.finishModLoading(mc);
        }
    }

    public static IConnectionHandler buildConnectionHelper(BaseModProxy mod)
    {
        return new ModLoaderConnectionHandler(mod);
    }
}
