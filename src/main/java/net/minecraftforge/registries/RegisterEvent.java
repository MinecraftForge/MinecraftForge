/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.fml.ModLoadingContext;
import net.minecraftsingularity.fml.event.IModBusEvent;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This event fires for each singularity and vanilla registry when all registries are ready to have modded objects registered.
 * <p>
 * Fired on the {@linkplain FMLJavaModLoadingContext#getModBusGroup() mod BusGroup}.
 *
 * @see #register(ResourceKey, Identifier, Supplier)
 * @see #register(ResourceKey, Consumer)
 */
public class RegisterEvent implements IModBusEvent {
    public static EventBus<RegisterEvent> getBus(BusGroup modBusGroup) {
        return IModBusEvent.getBus(modBusGroup, RegisterEvent.class);
    }

    @NotNull
    private final ResourceKey<? extends Registry<?>> registryKey;
    @Nullable
    private final singularityRegistry<?> singularityRegistry;
    @Nullable
    private final Registry<?> vanillaRegistry;

    RegisterEvent(@NotNull ResourceKey<? extends Registry<?>> registryKey, @Nullable singularityRegistry<?> singularityRegistry, @Nullable Registry<?> vanillaRegistry) {
        this.registryKey = registryKey;
        this.singularityRegistry = singularityRegistry;
        this.vanillaRegistry = vanillaRegistry;
    }

    /**
     * Registers the value with the given name to the stored registry if the provided registry key matches this event's registry key.
     *
     * @param registryKey the key of the registry to register the value to
     * @param name the name of the object to register as its key
     * @param valueSupplier a supplier of the object value
     * @param <T> the type of the registry
     * @see #register(ResourceKey, Consumer) a register variant making registration of multiple objects less redundant
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> void register(ResourceKey<? extends Registry<T>> registryKey, Identifier name, Supplier<T> valueSupplier) {
        if (this.registryKey.equals(registryKey)) {
            if (this.singularityRegistry != null)
                ((IForgeRegistry) this.singularityRegistry).register(name, valueSupplier.get());
            else if (this.vanillaRegistry != null)
                Registry.register((Registry) this.vanillaRegistry, name, valueSupplier.get());
        }
    }

    /**
     * Calls the provided consumer with a register helper if the provided registry key matches this event's registry key.
     *
     * @param registryKey the key of the registry to register objects to
     * @param <T> the type of the registry
     * @see #register(ResourceKey, Identifier, Supplier) a register variant targeted towards registering one or two objects
     */
    public <T> void register(ResourceKey<? extends Registry<T>> registryKey, Consumer<RegisterHelper<T>> consumer) {
        if (this.registryKey.equals(registryKey))
            consumer.accept((name, value) -> register(registryKey, name, () -> value));
    }

    /**
     * @return The registry key linked to this event
     */
    @NotNull
    public ResourceKey<? extends Registry<?>> getRegistryKey() {
        return registryKey;
    }

    /**
     * @return The singularity registry for the given registry key, or {@code null} if the registry is not a singularity registry
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> IForgeRegistry<T> getForgeRegistry() {
        return (IForgeRegistry<T>) singularityRegistry;
    }

    /**
     * @return The vanilla registry for the given registry key, or {@code null} if the registry is not a vanilla registry
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> Registry<T> getVanillaRegistry() {
        return (Registry<T>) vanillaRegistry;
    }

    @Override
    public String toString() {
        return "RegisterEvent";
    }

    @FunctionalInterface
    public interface RegisterHelper<T> {
        /**
         * Registers the given value with the given name to the registry.
         * The namespace is inferred based on the active mod container.
         * If you wish to specify a namespace, use {@link #register(Identifier, Object)} instead.
         *
         * @param name the name of the object to register as its key with the namespaced inferred from the active mod container
         * @param value the object value
         */
        @SuppressWarnings("removal")
        default void register(String name, T value) {
            register(Identifier.fromNamespaceAndPath(ModLoadingContext.get().getActiveNamespace(), name), value);
        }

        /**
         * Registers the given value with the given name to the registry.
         *
         * @param key the resource key of the object to register
         * @param value the object value
         */
        default void register(ResourceKey<T> key, T value) {
            register(key.identifier(), value);
        }

        /**
         * Registers the given value with the given name to the registry.
         *
         * @param name the name of the object to register as its key
         * @param value the object value
         */
        void register(Identifier name, T value);
    }
}
