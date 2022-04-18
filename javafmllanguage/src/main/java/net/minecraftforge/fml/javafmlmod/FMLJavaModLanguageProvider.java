/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.javafmlmod;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingStage;
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

import static net.minecraftforge.fml.Logging.SCAN;
import static net.minecraftforge.fml.Logging.LOADING;

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
        public <T> T loadMod(final IModInfo info, final ModFileScanData modFileScanResults, ModuleLayer gameLayer)
        {
            // This language class is loaded in the system level classloader - before the game even starts
            // So we must treat container construction as an arms length operation, and load the container
            // in the classloader of the game - the context classloader is appropriate here.
            try
            {
                final Class<?> fmlContainer = Class.forName("net.minecraftforge.fml.javafmlmod.FMLModContainer", true, Thread.currentThread().getContextClassLoader());
                LOGGER.debug(LOADING, "Loading FMLModContainer from classloader {} - got {}", Thread.currentThread().getContextClassLoader(), fmlContainer.getClassLoader());
                final Constructor<?> constructor = fmlContainer.getConstructor(IModInfo.class, String.class, ModFileScanData.class, ModuleLayer.class);
                return (T)constructor.newInstance(info, className, modFileScanResults, gameLayer);
            }
            catch (InvocationTargetException e) {
                LOGGER.fatal(LOADING, "Failed to build mod", e);
                if (e.getTargetException() instanceof ModLoadingException mle) {
                    throw mle;
                } else {
                    throw new ModLoadingException(info, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmodclass", e);
                }
            }
            catch (NoSuchMethodException | ClassNotFoundException | InstantiationException | IllegalAccessException e)
            {
                LOGGER.fatal(LOADING,"Unable to load FMLModContainer, wut?", e);
                final Class<RuntimeException> mle = (Class<RuntimeException>)LamdbaExceptionUtils.uncheck(()->Class.forName("net.minecraftforge.fml.ModLoadingException", true, Thread.currentThread().getContextClassLoader()));
                final Class<ModLoadingStage> mls = (Class<ModLoadingStage>) LamdbaExceptionUtils.uncheck(()->Class.forName("net.minecraftforge.fml.ModLoadingStage", true, Thread.currentThread().getContextClassLoader()));
                throw LamdbaExceptionUtils.uncheck(()->LamdbaExceptionUtils.uncheck(()->mle.getConstructor(IModInfo.class, mls, String.class, Throwable.class)).newInstance(info, Enum.valueOf(mls, "CONSTRUCT"), "fml.modloading.failedtoloadmodclass", e));
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
                    .filter(ad -> ad.annotationType().equals(MODANNOTATION))
                    .peek(ad -> LOGGER.debug(SCAN, "Found @Mod class {} with id {}", ad.clazz().getClassName(), ad.annotationData().get("value")))
                    .map(ad -> new FMLModTarget(ad.clazz().getClassName(), (String)ad.annotationData().get("value")))
                    .collect(Collectors.toMap(FMLModTarget::getModId, Function.identity(), (a,b)->a));
            scanResult.addLanguageLoader(modTargetMap);
        };
    }

    @Override
    public <R extends ILifecycleEvent<R>> void consumeLifecycleEvent(final Supplier<R> consumeEvent) {

    }
}
