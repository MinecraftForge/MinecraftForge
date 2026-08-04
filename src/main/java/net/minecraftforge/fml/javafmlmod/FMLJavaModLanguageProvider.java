/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.forgespi.language.ILifecycleEvent;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.*;

public class FMLJavaModLanguageProvider implements IModLanguageProvider
{

    private static final Logger LOGGER = LogManager.getLogger();

    private static class FMLModTarget implements IModLanguageProvider.IModLanguageLoader {
        private static final Logger LOGGER = FMLJavaModLanguageProvider.LOGGER;
        private final String className;
        private final String modId;

        private FMLModTarget(String className, String modId)
        {
            this.className = className;
            this.modId = modId;
        }

        public String getModId()
        {
            return modId;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T loadMod(final IModInfo info, final ClassLoader modClassLoader, final ModFileScanData modFileScanResults)
        {
            // This language class is loaded in the system level classloader - before the game even starts
            // So we must treat container construction as an arms length operation, and load the container
            // in the classloader of the game - the context classloader is appropriate here.
            try
            {
                final Class<?> fmlContainer = Class.forName("net.minecraftforge.fml.javafmlmod.FMLModContainer", true, Thread.currentThread().getContextClassLoader());
                LOGGER.debug(LOADING, "Loading FMLModContainer from classloader {} - got {}", Thread.currentThread().getContextClassLoader(), fmlContainer.getClassLoader());
                final Constructor<?> constructor = fmlContainer.getConstructor(IModInfo.class, String.class, ClassLoader.class, ModFileScanData.class);
                return (T)constructor.newInstance(info, className, modClassLoader, modFileScanResults);
            }
            catch (NoSuchMethodException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e)
            {
                LOGGER.fatal(LOADING,"Unable to load FMLModContainer, wut?", e);
                throw new RuntimeException(e);
            }
        }
    }

    public static final Type MODANNOTATION = Type.getType("Lnet/minecraftforge/fml/common/Mod;");

    @Override
    public String name()
    {
        return "javafml";
    }

    @Override
    public Consumer<ModFileScanData> getFileVisitor() {
        return scanResult -> {
            final Map<String, FMLModTarget> modTargetMap = scanResult.getAnnotations().stream()
                    .filter(ad -> ad.getAnnotationType().equals(MODANNOTATION))
                    .peek(ad -> LOGGER.debug(SCAN, "Found @Mod class {} with id {}", ad.getClassType().getClassName(), ad.getAnnotationData().get("value")))
                    .map(ad -> new FMLModTarget(ad.getClassType().getClassName(), (String)ad.getAnnotationData().get("value")))
                    .collect(Collectors.toMap(FMLModTarget::getModId, Function.identity(), (a,b)->a));
            scanResult.addLanguageLoader(modTargetMap);
        };
    }

    @Override
    public <R extends ILifecycleEvent<R>> void consumeLifecycleEvent(final Supplier<R> consumeEvent) {

    }
}
