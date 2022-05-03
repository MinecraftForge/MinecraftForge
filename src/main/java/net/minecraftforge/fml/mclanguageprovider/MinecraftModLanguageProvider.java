/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.mclanguageprovider;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.forgespi.language.ILifecycleEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraftforge.fml.Logging.LOADING;

import net.minecraftforge.forgespi.language.IModLanguageProvider.IModLanguageLoader;

public class MinecraftModLanguageProvider implements IModLanguageProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    @Override
    public String name() {
        return "minecraft";
    }

    @Override
    public Consumer<ModFileScanData> getFileVisitor() {
        return (sd)->sd.addLanguageLoader(Collections.singletonMap("minecraft", new MinecraftModTarget()));
    }

    @Override
    public <R extends ILifecycleEvent<R>> void consumeLifecycleEvent(final Supplier<R> consumeEvent) {

    }

    public static class MinecraftModTarget implements IModLanguageLoader {
        @SuppressWarnings("unchecked")
        @Override
        public <T> T loadMod(final IModInfo info, final ClassLoader modClassLoader, final ModFileScanData modFileScanResults) {
            try {
                final Class<?> mcModClass = Class.forName("net.minecraftforge.fml.mclanguageprovider.MinecraftModLanguageProvider$MinecraftModContainer", true, modClassLoader);
                return (T)mcModClass.getConstructor(IModInfo.class).newInstance(info);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                LOGGER.fatal(LOADING,"Unable to load MinecraftModContainer, wut?", e);
                throw new RuntimeException(e);
            }
        }
    }
    public static class MinecraftModContainer extends ModContainer {
        private static final String MCMODINSTANCE = "minecraft, the mod";

        public MinecraftModContainer(final IModInfo info) {
            super(info);
            contextExtension = ()->null;
        }

        @Override
        public boolean matches(final Object mod) {
            return Objects.equals(mod, MCMODINSTANCE);
        }

        @Override
        public Object getMod() {
            return MCMODINSTANCE;
        }
    }
}
