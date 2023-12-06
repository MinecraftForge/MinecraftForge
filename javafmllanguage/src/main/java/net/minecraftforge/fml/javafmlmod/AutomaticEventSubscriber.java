/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import net.minecraftforge.forgespi.language.ModFileScanData.EnumData;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Automatic eventbus subscriber - reads {@link EventBusSubscriber}
 * annotations and passes the class instances to the {@link nBus}
 * defined by the annotation. Defaults to {@code MinecraftForge#EVENT_BUS}
 */
public class AutomaticEventSubscriber {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Type AUTO_SUBSCRIBER = Type.getType(EventBusSubscriber.class);
    private static final Type MOD_TYPE = Type.getType(Mod.class);

    public static void inject(ModContainer mod, ModFileScanData scanData, ClassLoader loader) {
        if (scanData == null) return;
        LOGGER.debug(Logging.LOADING, "Attempting to inject @EventBusSubscriber classes into the eventbus for {}", mod.getModId());

        var targets = scanData.getAnnotations().stream()
            .filter(data -> AUTO_SUBSCRIBER.equals(data.annotationType()))
            .toList();

        var modids = scanData.getAnnotations().stream()
            .filter(data -> MOD_TYPE.equals(data.annotationType()))
            .collect(Collectors.toMap(a -> a.clazz().getClassName(), a -> (String)a.annotationData().get("value")));

        var defaultSides = List.of(new EnumData(null, "CLIENT"), new EnumData(null, "DEDICATED_SERVER"));
        var defaultBus = new EnumData(null, "FORGE");

        for (var data : targets) {
            var modId = modids.getOrDefault(data.clazz().getClassName(), mod.getModId());
            modId = value(data, "modid", modId);

            var sidesValue = value(data, "value", defaultSides);
            var sides = sidesValue.stream()
                .map(EnumData::value)
                .map(Dist::valueOf)
                .collect(Collectors.toSet());

            var busName = value(data, "bus", defaultBus).value();
            var busTarget = Bus.valueOf(busName);
            if (Objects.equals(mod.getModId(), modId) && sides.contains(FMLEnvironment.dist)) {
                try {
                    LOGGER.debug(Logging.LOADING, "Auto-subscribing {} to {}", data.clazz().getClassName(), busTarget);
                    busTarget.bus().get().register(Class.forName(data.clazz().getClassName(), true, loader));
                } catch (ClassNotFoundException e) {
                    LOGGER.fatal(Logging.LOADING, "Failed to load mod class {} for @EventBusSubscriber annotation", data.clazz(), e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <R> R value(AnnotationData data, String key, R value) {
        return (R)data.annotationData().getOrDefault(key, value);
    }
}
