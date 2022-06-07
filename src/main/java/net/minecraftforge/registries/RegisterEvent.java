/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This event fires for each forge and vanilla registry when all registries are ready to have modded objects registered.
 * <p>
 * Fired on the {@link IModBusEvent mod bus}.
 *
 * @see #register(ResourceKey, ResourceLocation, Supplier)
 * @see #register(ResourceKey, Consumer)
 */
public class RegisterEvent extends Event implements IModBusEvent
{
    @NotNull
    private final ResourceKey<? extends Registry<?>> registryKey;
    @Nullable
    final ForgeRegistry<?> forgeRegistry;
    @Nullable
    private final Registry<?> vanillaRegistry;

    RegisterEvent(@NotNull ResourceKey<? extends Registry<?>> registryKey, @Nullable ForgeRegistry<?> forgeRegistry, @Nullable Registry<?> vanillaRegistry)
    {
        this.registryKey = registryKey;
        this.forgeRegistry = forgeRegistry;
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
    public <T> void register(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation name, Supplier<T> valueSupplier)
    {
        if (this.registryKey.equals(registryKey))
        {
            if (this.forgeRegistry != null)
                ((IForgeRegistry) this.forgeRegistry).register(name, valueSupplier.get());
            else if (this.vanillaRegistry != null)
                Registry.register((Registry) this.vanillaRegistry, name, valueSupplier.get());
        }
    }

    /**
     * Calls the provided consumer with a register helper if the provided registry key matches this event's registry key.
     *
     * @param registryKey the key of the registry to register objects to
     * @param <T> the type of the registry
     * @see #register(ResourceKey, ResourceLocation, Supplier) a register variant targeted towards registering one or two objects
     */
    public <T> void register(ResourceKey<? extends Registry<T>> registryKey, Consumer<RegisterHelper<T>> consumer)
    {
        if (this.registryKey.equals(registryKey))
        {
            consumer.accept((name, value) -> register(registryKey, name, () -> value));
        }
    }

    /**
     * @return The registry key linked to this event
     */
    @NotNull
    public ResourceKey<? extends Registry<?>> getRegistryKey()
    {
        return registryKey;
    }

    /**
     * @return The forge registry for the given registry key, or {@code null} if the registry is not a forge registry
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> IForgeRegistry<T> getForgeRegistry()
    {
        return (IForgeRegistry<T>) forgeRegistry;
    }

    /**
     * @return The vanilla registry for the given registry key, or {@code null} if the registry is not a vanilla registry
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> Registry<T> getVanillaRegistry()
    {
        return (Registry<T>) vanillaRegistry;
    }

    @Override
    public String toString()
    {
        return "RegisterEvent";
    }

    @FunctionalInterface
    public interface RegisterHelper<T>
    {
        /**
         * Registers the given value with the given name to the registry.
         * The namespace is inferred based on the active mod container.
         * If you wish to specify a namespace, use {@link #register(ResourceLocation, Object)} instead.
         *
         * @param name the name of the object to register as its key with the namespaced inferred from the active mod container
         * @param value the object value
         */
        default void register(String name, T value)
        {
            register(new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name), value);
        }

        /**
         * Registers the given value with the given name to the registry.
         *
         * @param key the resource key of the object to register
         * @param value the object value
         */
        default void register(ResourceKey<T> key, T value)
        {
            register(key.location(), value);
        }

        /**
         * Registers the given value with the given name to the registry.
         *
         * @param name the name of the object to register as its key
         * @param value the object value
         */
        void register(ResourceLocation name, T value);
    }
}
