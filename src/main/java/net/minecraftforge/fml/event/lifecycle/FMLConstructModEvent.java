/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import org.slf4j.Logger;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class FMLConstructModEvent extends ParallelDispatchEvent {
    private static final Logger LOGGER = LogUtils.getLogger();

    public FMLConstructModEvent(final ModContainer container, final ModLoadingStage stage) {
        super(container, stage);
    }

    public IEventBus getModBus() {
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

}
