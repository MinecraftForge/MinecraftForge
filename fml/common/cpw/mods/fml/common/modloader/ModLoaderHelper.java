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

import com.google.common.collect.Maps;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

/**
 * @author cpw
 *
 */
public class ModLoaderHelper
{
    public static IModLoaderSidedHelper sidedHelper;
    
    private static Map<Integer, ModLoaderGuiHelper> guiHelpers = Maps.newHashMap();
    
    public static void updateStandardTicks(BaseModProxy mod, boolean enable, boolean useClock)
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

    public static void updateGUITicks(BaseModProxy mod, boolean enable, boolean useClock)
    {
        ModLoaderModContainer mlmc = (ModLoaderModContainer) Loader.instance().activeModContainer();
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

    public static IPacketHandler buildPacketHandlerFor(BaseModProxy mod)
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

    public static IPickupNotifier buildPickupHelper(BaseModProxy mod)
    {
        return new ModLoaderPickupNotifier(mod);
    }

    public static void buildGuiHelper(BaseModProxy mod, int id)
    {
        ModLoaderGuiHelper handler = new ModLoaderGuiHelper(mod, id);
        guiHelpers.put(id, handler);
        NetworkRegistry.instance().registerGuiHandler(mod, handler);
    }

    public static void openGui(int id, EntityPlayer player, Container container, int x, int y, int z)
    {
        ModLoaderGuiHelper helper = guiHelpers.get(id);
        helper.injectContainer(container);
        player.openGui(helper.getMod(), id, player.field_70170_p, x, y, z);
    }
    
    public static Object getClientSideGui(BaseModProxy mod, EntityPlayer player, int ID, int x, int y, int z)
    {
        if (sidedHelper != null)
        {
            return sidedHelper.getClientGui(mod, player, ID, x, y, z);
        }
        return null;
    }
}
