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
import java.util.concurrent.Callable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

import net.minecraft.command.ICommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.src.BaseMod;
import net.minecraft.src.TradeEntry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IDispenseHandler;
import cpw.mods.fml.common.IDispenserHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.IChatListener;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import cpw.mods.fml.common.registry.VillagerRegistry;

/**
 * @author cpw
 *
 */
@SuppressWarnings("deprecation")
public class ModLoaderHelper
{
    public static IModLoaderSidedHelper sidedHelper;

    private static Map<BaseModProxy, ModLoaderGuiHelper> guiHelpers = Maps.newHashMap();
    private static Map<Integer, ModLoaderGuiHelper> guiIDs = Maps.newHashMap();

    public static void updateStandardTicks(BaseModProxy mod, boolean enable, boolean useClock)
    {
        ModLoaderModContainer mlmc = (ModLoaderModContainer) Loader.instance().getReversedModObjectList().get(mod);
        if (mlmc==null)
        {
            mlmc = (ModLoaderModContainer) Loader.instance().activeModContainer();
        }
        if (mlmc == null)
        {
            FMLLog.severe("Attempted to register ModLoader ticking for invalid BaseMod %s",mod);
            return;
        }
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
        ModLoaderModContainer mlmc = (ModLoaderModContainer) Loader.instance().getReversedModObjectList().get(mod);
        if (mlmc==null)
        {
            mlmc = (ModLoaderModContainer) Loader.instance().activeModContainer();
        }
        if (mlmc == null)
        {
            FMLLog.severe("Attempted to register ModLoader ticking for invalid BaseMod %s",mod);
            return;
        }
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
        ModLoaderGuiHelper handler = guiHelpers.get(mod);
        if (handler == null)
        {
            handler = new ModLoaderGuiHelper(mod);
            guiHelpers.put(mod,handler);
            NetworkRegistry.instance().registerGuiHandler(mod, handler);
        }
        handler.associateId(id);
        guiIDs.put(id, handler);
    }

    public static void openGui(int id, EntityPlayer player, Container container, int x, int y, int z)
    {
        ModLoaderGuiHelper helper = guiIDs.get(id);
        helper.injectContainerAndID(container, id);
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

    public static IDispenserHandler buildDispenseHelper(BaseModProxy mod)
    {
        return new ModLoaderDispenseHelper(mod);
    }


    public static void buildEntityTracker(BaseModProxy mod, Class<? extends Entity> entityClass, int entityTypeId, int updateRange, int updateInterval,
            boolean sendVelocityInfo)
    {
        EntityRegistration er = EntityRegistry.registerModLoaderEntity(mod, entityClass, entityTypeId, updateRange, updateInterval, sendVelocityInfo);
        er.setCustomSpawning(new ModLoaderEntitySpawnCallback(mod, er), EntityDragon.class.isAssignableFrom(entityClass) || IAnimals.class.isAssignableFrom(entityClass));
    }

    private static ModLoaderVillageTradeHandler[] tradeHelpers = new ModLoaderVillageTradeHandler[6];

    public static void registerTrade(int profession, TradeEntry entry)
    {
        assert profession < tradeHelpers.length : "The profession is out of bounds";
        if (tradeHelpers[profession] == null)
        {
            tradeHelpers[profession] = new ModLoaderVillageTradeHandler();
            VillagerRegistry.instance().registerVillageTradeHandler(profession, tradeHelpers[profession]);
        }

        tradeHelpers[profession].addTrade(entry);
    }

    public static void addCommand(ICommand command)
    {
        ModLoaderModContainer mlmc = (ModLoaderModContainer) Loader.instance().activeModContainer();
        if (mlmc!=null)
        {
            mlmc.addServerCommand(command);
        }
    }

    public static IChatListener buildChatListener(BaseModProxy mod)
    {
        return new ModLoaderChatListener(mod);
    }
}
