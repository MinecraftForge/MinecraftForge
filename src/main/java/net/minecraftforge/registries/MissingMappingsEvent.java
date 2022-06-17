/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS forge bus}.
 */
public class MissingMappingsEvent extends Event
{
    private final ResourceKey<? extends Registry<?>> key;
    private final IForgeRegistry<?> registry;
    private final List<Mapping<?>> mappings;

    public MissingMappingsEvent(ResourceKey<? extends Registry<?>> key, IForgeRegistry<?> registry, Collection<Mapping<?>> missed)
    {
        this.key = key;
        this.registry = registry;
        this.mappings = List.copyOf(missed);
    }

    public ResourceKey<? extends Registry<?>> getKey()
    {
        return this.key;
    }

    public IForgeRegistry<?> getRegistry()
    {
        return this.registry;
    }

    /**
     * @return An immutable list of missing mappings for the given namespace.
     * Empty if the registry key doesn't match {@link #getKey()}.
     */
    @SuppressWarnings("unchecked")
    public <T> List<Mapping<T>> getMappings(ResourceKey<? extends Registry<T>> registryKey, String namespace)
    {
        return registryKey == this.key
                ? (List<Mapping<T>>) (List<?>) this.mappings.stream().filter(e -> e.key.getNamespace().equals(namespace)).toList()
                : List.of();
    }

    /**
     * @return An immutable list of all missing mappings.
     * Empty if the registry key doesn't match {@link #getKey()}.
     */
    @SuppressWarnings("unchecked")
    public <T> List<Mapping<T>> getAllMappings(ResourceKey<? extends Registry<T>> registryKey)
    {
        return registryKey == this.key ? (List<Mapping<T>>) (List<?>) this.mappings : List.of();
    }

    /**
     * Actions you can take with this missing mapping.
     * <ul>
     * <li>{@link #IGNORE} means this missing mapping will be ignored.
     * <li>{@link #WARN} means this missing mapping will generate a warning.
     * <li>{@link #FAIL} means this missing mapping will prevent the world from loading.
     * </ul>
     */
    public enum Action
    {
        /**
         * Take the default action
         */
        DEFAULT,
        /**
         * Ignore this missing mapping. This means the mapping will be abandoned
         */
        IGNORE,
        /**
         * Generate a warning but allow loading to continue
         */
        WARN,
        /**
         * Fail to load
         */
        FAIL,
        /**
         * Remap this name to a new name (add a migration mapping)
         */
        REMAP
    }

    public static class Mapping<T> implements Comparable<Mapping<T>>
    {
        private final IForgeRegistry<T> registry;
        private final IForgeRegistry<T> pool;
        final ResourceLocation key;
        final int id;
        Action action = Action.DEFAULT;
        T target;

        public Mapping(IForgeRegistry<T> registry, IForgeRegistry<T> pool, ResourceLocation key, int id)
        {
            this.registry = registry;
            this.pool = pool;
            this.key = key;
            this.id = id;
        }

        /**
         * Ignore the missing item.
         */
        public void ignore()
        {
            action = Action.IGNORE;
        }

        /**
         * Warn the user about the missing item.
         */
        public void warn()
        {
            action = Action.WARN;
        }

        /**
         * Prevent the world from loading due to the missing item.
         */
        public void fail()
        {
            action = Action.FAIL;
        }

        /**
         * Remap the missing entry to the specified object.
         * <p>
         * Use this if you have renamed an entry.
         * Existing references using the old name will point to the new one.
         *
         * @param target Entry to remap to.
         */
        public void remap(T target)
        {
            Validate.notNull(target, "Remap target can not be null");
            Validate.isTrue(pool.getKey(target) != null,
                    String.format(Locale.ENGLISH, "The specified entry %s hasn't been registered in registry yet.", target));
            action = Action.REMAP;
            this.target = target;
        }

        public IForgeRegistry<T> getRegistry()
        {
            return this.registry;
        }

        public ResourceLocation getKey()
        {
            return key;
        }

        public int getId()
        {
            return this.id;
        }

        @Override
        public int compareTo(Mapping<T> o)
        {
            int ret = this.registry.getRegistryName().compareNamespaced(o.registry.getRegistryName());
            if (ret == 0)
                ret = this.key.compareNamespaced(o.key);
            return ret;
        }
    }
}
