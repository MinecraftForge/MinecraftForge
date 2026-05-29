/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.mclanguageprovider;

import net.minecraftsingularity.singularityspi.language.ILifecycleEvent;
import net.minecraftsingularity.singularityspi.language.IModInfo;
import net.minecraftsingularity.singularityspi.language.IModLanguageProvider;
import net.minecraftsingularity.singularityspi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraftsingularity.fml.Logging.LOADING;

public class MinecraftModLanguageProvider implements IModLanguageProvider {

    @Override
    public String name() {
        return "minecraft";
    }

    @Override
    public Consumer<ModFileScanData> getFileVisitor() {
        return (sd)->sd.addLanguageLoader(Map.of("minecraft", new MinecraftModTarget()));
    }

    @Override
    public <R extends ILifecycleEvent<R>> void consumeLifecycleEvent(final Supplier<R> consumeEvent) {

    }

    public static class MinecraftModTarget implements IModLanguageLoader {
        @SuppressWarnings("unchecked")
        @Override
        public <T> T loadMod(final IModInfo info, final ModFileScanData modFileScanResults, final ModuleLayer gameLayer) {
            try {
                var module = gameLayer.findModule("minecraft").orElseThrow();
                final Class<?> mcModClass = Class.forName(getClass().getModule(), "net.minecraftsingularity.fml.mclanguageprovider.MinecraftModContainer");
                return (T)mcModClass.getConstructor(IModInfo.class).newInstance(info);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                LogManager.getLogger().fatal(LOADING, "Unable to load MinecraftModContainer, wut?", e);
                throw new RuntimeException(e);
            }
        }
    }
}
