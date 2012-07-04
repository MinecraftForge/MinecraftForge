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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.TickType;

/**
 * @author cpw
 *
 */
public class ModLoaderHelper
{
    private static Map<BaseMod, ModLoaderModContainer> notModCallbacks=new HashMap<BaseMod, ModLoaderModContainer>();

    public static void updateStandardTicks(BaseMod mod, boolean enable, boolean useClock)
    {
        ModLoaderModContainer mlmc = findOrBuildModContainer(mod);
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
            ticks.add(TickType.GAME);
            ticks.add(TickType.WORLDLOAD);
        } else {
            ticks.remove(TickType.GAME);
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
            ticks.add(TickType.GUILOAD);
        } else {
            ticks.remove(TickType.RENDER);
        }
        // If we're enabled but we want clock ticks, or we're server side we get world ticks
        if (enable && useClock) {
            ticks.add(TickType.GAME);
            ticks.add(TickType.GUILOAD);
            ticks.add(TickType.WORLDLOAD);
        } else {
            ticks.remove(TickType.GAME);
            ticks.remove(TickType.GUILOAD);
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

    public static ModLoaderModContainer registerRenderHelper(BaseMod mod) {
        ModLoaderModContainer mlmc=findOrBuildModContainer(mod);
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
}
