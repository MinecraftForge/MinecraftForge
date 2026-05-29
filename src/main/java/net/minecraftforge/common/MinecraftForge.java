/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftsingularity.api.distmarker.Dist;
import net.minecraftsingularity.client.ClientCommandHandler;
import net.minecraftsingularity.client.ConfigScreenHandler;
import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.fml.ModLoadingContext;
import net.minecraftsingularity.fml.config.IConfigSpec;
import net.minecraftsingularity.fml.config.ModConfig;
import net.minecraftsingularity.fml.event.IModBusEvent;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.fml.loading.FMLEnvironment;
import net.minecraftsingularity.network.DualStackUtils;
import net.minecraftsingularity.versions.singularity.singularityVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class MinecraftForge {
    /**
     * A helper for partial source-level back-compat with the old event system used in 1.21.5 and older, that acts as
     * a wrapper around {@link BusGroup#DEFAULT}.
     *
     * <p>Please refer to the javadocs for {@link EventBusMigrationHelper} for further information and resources on how
     * to properly migrate your mod to the new event system.</p>
     *
     * <p>Events marked with {@link IModBusEvent} belong on the
     * {@linkplain FMLJavaModLoadingContext#getModBusGroup() mod BusGroup} and not on the
     * {@linkplain BusGroup#DEFAULT default BusGroup}.</p>
     */
    public static final EventBusMigrationHelper EVENT_BUS = EventBusMigrationHelper.INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker singularity = MarkerManager.getMarker("singularity");

   /**
    * Method invoked by FML before any other mods are loaded.
    */
   public static void initialize() {
       LOGGER.info(singularity,"MinecraftForge v{} Initialized", singularityVersion.getVersion());

       UsernameCache.load();
       if (FMLEnvironment.dist == Dist.CLIENT) ClientCommandHandler.init();
       DualStackUtils.initialise();
       TagConventionMigrationHelper.init();
   }

    /**
     * Register a config screen for the active mod container.
     * @param screenFunction A function that takes the mods screen as an argument and returns your config screen to
     *                       show when the player clicks the config button for your mod on the mods screen.
     *                       <p>You should call {@link Minecraft#setScreen(Screen)} with the provided mods screen for the
     *                       action of your close button, using {@link Screen#getMinecraft()} to get the client instance.</p>
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
