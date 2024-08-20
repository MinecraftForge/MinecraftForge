/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import org.slf4j.Logger;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * used in your mods constructor to get access to ModBus and various mod-specific objects.
 */
public class FMLConstructModEvent extends ParallelDispatchEvent {
    private static final Logger LOGGER = LogUtils.getLogger();

    public FMLConstructModEvent(final ModContainer container, final ModLoadingStage stage) {
        super(container, stage);
    }

    public IEventBus getModEventBus() {
        if (getContainer() instanceof FMLModContainer modContainer)
            return modContainer.getEventBus();
        return null;
    }

    /**
     * Register an {@link IExtensionPoint} with the mod container.
     * @param point The extension point to register
     * @param extension An extension operator
     * @param <T> The type signature of the extension operator
     */
    public <T extends Record & IExtensionPoint<T>> void registerExtensionPoint(Class<? extends IExtensionPoint<T>> point, Supplier<T> extension) {
        getContainer().registerExtensionPoint(point, extension);
    }

    /**
     * Register a {@link IExtensionPoint.DisplayTest} with the mod container.
     * <p>A shorthand for registering a DisplayTest with {@link #registerExtensionPoint(Class, Supplier)}.</p>
     * @param displayTest The {@link IExtensionPoint.DisplayTest} to register
     */
    public void registerDisplayTest(IExtensionPoint.DisplayTest displayTest) {
        getContainer().registerDisplayTest(() -> displayTest);
    }

    /**
     * Register a {@link IExtensionPoint.DisplayTest} with the mod container.
     * <p>A shorthand for registering a DisplayTest supplier with {@link #registerExtensionPoint(Class, Supplier)}.</p>
     * @param displayTest The {@link Supplier<IExtensionPoint.DisplayTest>} to register
     */
    public void registerDisplayTest(Supplier<IExtensionPoint.DisplayTest> displayTest) {
        getContainer().registerDisplayTest(displayTest);
    }

    /**
     * Register a {@link IExtensionPoint.DisplayTest} with the mod container.
     * <p>A shorthand for registering a DisplayTest with {@link #registerExtensionPoint(Class, Supplier)} that also
     * creates the DisplayTest instance for you using the provided parameters.</p>
     * @see IExtensionPoint.DisplayTest#DisplayTest(String, BiPredicate)
     */
    public void registerDisplayTest(String version, BiPredicate<String, Boolean> remoteVersionTest) {
        getContainer().registerDisplayTest(new IExtensionPoint.DisplayTest(version, remoteVersionTest));
    }

    /**
     * Register a {@link IExtensionPoint.DisplayTest} with the mod container.
     * <p>A shorthand for registering a DisplayTest with {@link #registerExtensionPoint(Class, Supplier)} that also
     * creates the DisplayTest instance for you using the provided parameters.</p>
     * @see IExtensionPoint.DisplayTest#DisplayTest(Supplier, BiPredicate)
     */
    public void registerDisplayTest(Supplier<String> suppliedVersion, BiPredicate<String, Boolean> remoteVersionTest) {
        getContainer().registerDisplayTest(new IExtensionPoint.DisplayTest(suppliedVersion, remoteVersionTest));
    }

    public void registerConfig(ModConfig.Type type, IConfigSpec<?> spec) {
        if (spec.isEmpty())
        {
            // This handles the case where a mod tries to register a config, without any options configured inside it.
            LOGGER.debug("Attempted to register an empty config for type {} on mod {}", type, getContainer().getModId());
            return;
        }

        getContainer().addConfig(new ModConfig(type, spec, getContainer()));
    }

    public void registerConfig(ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        if (spec.isEmpty())
        {
            // This handles the case where a mod tries to register a config, without any options configured inside it.
            LOGGER.debug("Attempted to register an empty config for type {} on mod {} using file name {}", type, getContainer().getModId(), fileName);
            return;
        }

        getContainer().addConfig(new ModConfig(type, spec, getContainer(), fileName));
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
    public void registerConfigScreen(Function<Screen, Screen> screenFunction) {
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
    public void registerConfigScreen(BiFunction<Minecraft, Screen, Screen> screenFunction) {
         getContainer().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(screenFunction)
        );
    }
}
