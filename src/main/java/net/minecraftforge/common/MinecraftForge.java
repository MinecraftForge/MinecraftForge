/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.DualStackUtils;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MinecraftForge
{
    /**
     * The EventBus for all the Forge Events.
     *
     * Events marked with {@link net.minecraftforge.fml.event.IModBusEvent}
     * belong on the ModBus and not this bus
     */
    public static final IEventBus EVENT_BUS = BusBuilder.builder().startShutdown().useModLauncher().build();

    static final ForgeInternalHandler INTERNAL_HANDLER = new ForgeInternalHandler();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FORGE = MarkerManager.getMarker("FORGE");

   /**
    * Method invoked by FML before any other mods are loaded.
    */
   public static void initialize()
   {
       LOGGER.info(FORGE,"MinecraftForge v{} Initialized", ForgeVersion.getVersion());

       UsernameCache.load();
       TierSortingRegistry.init();
       if (FMLEnvironment.dist == Dist.CLIENT) ClientCommandHandler.init();
       DualStackUtils.initialise();
   }

    /**
     * Register a config screen for the active mod container.
     * @param screenFunction A function that takes the mods screen as an argument and returns your config screen to
     *                       show when the player clicks the config button for your mod on the mods screen.
     *                       <p>You should call {@link Minecraft#setScreen(Screen)} with the provided mods screen for the
     *                       action of your close button, using {@link Screen#minecraft} to get the client instance.</p>
     * @see ModLoadingContext#registerExtensionPoint(Class, Supplier)
     * @see ModLoadingContext#registerConfig(ModConfig.Type, IConfigSpec)
     */
    public static void registerConfigScreen(Function<Screen, Screen> screenFunction) {
        registerConfigScreen((mcClient, modsScreen) -> screenFunction.apply(modsScreen));
    }

    /**
     * Register a config screen for the active mod container.
     * @param screenFunction A function that takes the {@link Minecraft} client instance and the mods screen as
     *                       arguments and returns your config screen to show when the player clicks the config button
     *                       for your mod on the mods screen.
     *                       <p>You should call {@link Minecraft#setScreen(Screen)} with the provided client instance
     *                       and mods screen for the action of your close button.</p>
     * @see ModLoadingContext#registerExtensionPoint(Class, Supplier)
     * @see ModLoadingContext#registerConfig(ModConfig.Type, IConfigSpec)
     */
    public static void registerConfigScreen(BiFunction<Minecraft, Screen, Screen> screenFunction) {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(screenFunction)
        );
    }
}
