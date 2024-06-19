/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.Type;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public final class CapabilityManager {
    @Deprecated(forRemoval = true, since = "1.21")
    public static final CapabilityManager INSTANCE = new CapabilityManager();

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Retrieves the 'root' capability for the specified type. This is the generic one that is used when the API
     * registers the specified type. It is also safe to assume that if your child token is a valid return when
     * this Capability is requested.
     */
    public static <T> Capability<T> get(CapabilityToken<T> type) {
        return get(type.getType(), null, false);
    }

    /**
     * Retrieves a named capability of the specified type. This is useful when providers supply multiple instances of the same interface and want to
     * distinguish between them. It is generally not safe to return your generic implementation for the root type when this is requested.
     */
    public static <T> Capability<T> get(CapabilityToken<T> type, String modid, String name) {
        return get(type, ResourceLocation.fromNamespaceAndPath(modid, name));
    }

    /**
     * Retrieves a named capability of the specified type. This is useful when providers supply multiple instances of the same interface and want to
     * distinguish between them. It is generally not safe to return your generic implementation for the root type when this is requested.
     */
    public static <T> Capability<T> get(CapabilityToken<T> type, ResourceLocation name) {
        return get(type.getType(), name, false);
    }

    @SuppressWarnings("unchecked")
    static <T> Capability<T> get(String type, ResourceLocation name, boolean registering) {
        Capability<T> cap;

        synchronized (providers) {
            final var parent = (Capability<T>)providers.computeIfAbsent(new Key(type, null), k -> new Capability<>(type.intern()));

            if (name == null) {
                cap = parent;
            } else  { // A Named child
                cap = (Capability<T>)providers.computeIfAbsent(new Key(type, name), k -> {
                    var ret = new Capability<>((parent.getName() + '#' + name.toString()).intern());
                    parent.addListener(p -> ret.onRegister());
                    return ret;
                });
            }
        }

        if (registering) {
            if (name != null)
                error("Cannot register capability type " + type + " + with a name: " + name);

            synchronized (cap) {
                if (cap.isRegistered())
                    error("Cannot register capability type " + type + " multiple times");

                cap.onRegister();
            }
        }

        return cap;
    }

    private static final Type AUTO_REGISTER = Type.getType(AutoRegisterCapability.class);
    private record Key(String type, ResourceLocation name) {};
    private static final Map<Key, Capability<?>> providers = new HashMap<>();

    @ApiStatus.Internal
    public static void injectCapabilities(ModList modlist) {
        var autos = modlist.getAllScanData().stream()
            .flatMap(e -> e.getAnnotations().stream())
            .filter(a -> AUTO_REGISTER.equals(a.annotationType()))
            .map(a -> a.clazz())
            .distinct()
            .sorted(Comparator.comparing(Type::toString))
            .toList();

        for (var auto : autos) {
            LOGGER.debug(Logging.CAPABILITIES, "Attempting to automatically register: " + auto);
            get(auto.getInternalName(), null, true);
        }

        @SuppressWarnings("removal")
        var event = new RegisterCapabilitiesEvent();
        ModLoader.get().postEvent(event);
    }

    private static void error(String message) {
        LOGGER.error(Logging.CAPABILITIES, message);
        throw new IllegalStateException(message);
    }
}
