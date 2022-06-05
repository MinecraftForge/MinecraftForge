/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Register new registries when you receive this event through {@link RegistryBuilder} and {@link #create(RegistryBuilder)}.
 */
public class NewRegistryEvent extends Event implements IModBusEvent
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private final List<RegistryData<?>> registries = new ArrayList<>();

    public NewRegistryEvent() {}

    /**
     * Adds a registry builder to be created.
     *
     * @param builder The builder to turn into a {@link IForgeRegistry}
     * @return A supplier of the {@link IForgeRegistry} created by the builder. Resolving too early will return null.
     */
    public <V extends IForgeRegistryEntry<V>> Supplier<IForgeRegistry<V>> create(RegistryBuilder<V> builder)
    {
        return create(builder, null);
    }

    /**
     * Adds a registry builder to be created.
     *
     * @param builder The builder to turn into a {@link IForgeRegistry}
     * @param onFill  Called when the returned supplier is filled with the registry
     * @return a supplier of the {@link IForgeRegistry} created by the builder. Resolving too early will return null.
     */
    public <V extends IForgeRegistryEntry<V>> Supplier<IForgeRegistry<V>> create(RegistryBuilder<V> builder, @Nullable Consumer<IForgeRegistry<V>> onFill)
    {
        RegistryHolder<V> registryHolder = new RegistryHolder<>();

        registries.add(new RegistryData<>(builder, registryHolder, onFill));

        return registryHolder;
    }

    @SuppressWarnings("rawtypes")
    void fill()
    {
        Map<RegistryBuilder<?>, IForgeRegistry<?>> builtRegistries = new IdentityHashMap<>();
        RuntimeException aggregate = new RuntimeException("Failed to create some forge registries, see suppressed exceptions for details");
        for (RegistryData<?> registryData : this.registries)
        {
            RegistryBuilder<?> builder = registryData.builder();
            try
            {
                builtRegistries.put(builder, builder.create());
            } catch (Exception e)
            {
                aggregate.addSuppressed(e);
            }
        }

        builtRegistries.forEach((builder, reg) -> {
            try
            {
                RegistryAccess.RegistryData<?> dataPackRegistryData = builder.getDataPackRegistryData();
                if (dataPackRegistryData != null) // if this is a datapack registry
                {
                    if (!BuiltinRegistries.REGISTRY.containsKey(reg.getRegistryName()))
                    {
                        DataPackRegistriesHooks.addRegistryCodec(dataPackRegistryData);
                        RegistryManager.registerToBuiltinRegistry((ForgeRegistry<?>) reg);
                    }
                }
                else if (builder.getHasWrapper() && !Registry.REGISTRY.containsKey(reg.getRegistryName()))
                    RegistryManager.registerToRootRegistry((ForgeRegistry<?>) reg);
            } catch (Exception e)
            {
                aggregate.addSuppressed(e);
            }
        });

        for (RegistryData data : this.registries)
        {
            IForgeRegistry<?> registry = builtRegistries.get(data.builder);
            if (registry != null)
            {
                data.registryHolder.registry = registry;
                try
                {
                    if (data.onFill != null)
                        data.onFill.accept(registry);
                } catch (Exception e)
                {
                    aggregate.addSuppressed(e);
                }
            }
        }

        if (aggregate.getSuppressed().length > 0)
        {
            // Something had an exception, log and propagate with a throw for when FML eventually logs this too
            LOGGER.error("", aggregate);
            throw aggregate;
        }
    }

    private record RegistryData<V extends IForgeRegistryEntry<V>>(
            RegistryBuilder<V> builder,
            RegistryHolder<V> registryHolder,
            Consumer<IForgeRegistry<V>> onFill
    ) {}

    private static class RegistryHolder<V extends IForgeRegistryEntry<V>> implements Supplier<IForgeRegistry<V>>
    {
        IForgeRegistry<V> registry = null;

        @Override
        public IForgeRegistry<V> get()
        {
            return this.registry;
        }
    }

    @Override
    public String toString()
    {
        return "RegistryEvent.NewRegistry";
    }
}
