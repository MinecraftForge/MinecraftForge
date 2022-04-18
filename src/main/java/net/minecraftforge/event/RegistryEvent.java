/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * RegistryEvent supertype.
 */
public class RegistryEvent<T extends IForgeRegistryEntry<T>> extends GenericEvent<T> implements IModBusEvent
{
    RegistryEvent(Class<T> clazz) {
        super(clazz);
    }

    /**
     * Register your objects for the appropriate registry type when you receive this event.
     *
     * <code>event.getRegistry().register(...)</code>
     *
     * The registries will be visited in alphabetic order of their name, except blocks and items,
     * which will be visited FIRST and SECOND respectively.
     *
     * ObjectHolders will reload between Blocks and Items, and after all registries have been visited.
     * @param <T> The registry top level type
     */
    public static class Register<T extends IForgeRegistryEntry<T>> extends RegistryEvent<T>
    {
        private final IForgeRegistry<T> registry;
        private final ResourceLocation name;

        public Register(ResourceLocation name, IForgeRegistry<T> registry)
        {
            super(registry.getRegistrySuperType());
            this.name = name;
            this.registry = registry;
        }

        public IForgeRegistry<T> getRegistry()
        {
            return registry;
        }

        public ResourceLocation getName()
        {
            return name;
        }

        @Override
        public String toString() {
            return "RegistryEvent.Register<"+getName()+">";
        }
    }

    public static class MissingMappings<T extends IForgeRegistryEntry<T>> extends RegistryEvent<T>
    {
        private final IForgeRegistry<T> registry;
        private final ResourceLocation name;
        private final ImmutableList<Mapping<T>> mappings;
        private ModContainer activeMod;

        public MissingMappings(ResourceLocation name, IForgeRegistry<T> registry, Collection<Mapping<T>> missed)
        {
            super(registry.getRegistrySuperType());
            this.registry = registry;
            this.name = name;
            this.mappings = ImmutableList.copyOf(missed);
        }

        public void setModContainer(ModContainer mod)
        {
            this.activeMod = mod;
        }

        public ResourceLocation getName()
        {
            return this.name;
        }

        public IForgeRegistry<T> getRegistry()
        {
            return this.registry;
        }

        /*
         * This used to be fired on the Mod specific bus, and we could tell which mod was asking for mappings.
         * It no longer is, so this method is useless and just returns getAllMappings.
         * TODO: Ask cpw how if he wants to re-enable the ModBus rethrow.
         */
        @Deprecated
        public ImmutableList<Mapping<T>> getMappings()
        {
            return this.activeMod == null ? getAllMappings() : getMappings(this.activeMod.getModId());
        }

        public ImmutableList<Mapping<T>> getMappings(String modid)
        {
            return ImmutableList.copyOf(this.mappings.stream().filter(e -> e.key.getNamespace().equals(modid)).collect(Collectors.toList()));
        }

        public ImmutableList<Mapping<T>> getAllMappings()
        {
            return this.mappings;
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

        public static class Mapping<T extends IForgeRegistryEntry<T>> implements Comparable<Mapping<T>>
        {
            public final IForgeRegistry<T> registry;
            private final IForgeRegistry<T> pool;
            public final ResourceLocation key;
            public final int id;
            private Action action = Action.DEFAULT;
            private T target;

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
             *
             * Use this if you have renamed an entry.
             * Existing references using the old name will point to the new one.
             *
             * @param target Entry to remap to.
             */
            public void remap(T target)
            {
                Validate.notNull(target, "Remap target can not be null");
                Validate.isTrue(pool.getKey(target) != null, String.format(Locale.ENGLISH, "The specified entry %s hasn't been registered in registry yet.", target));
                action = Action.REMAP;
                this.target = target;
            }

            // internal
            public Action getAction()
            {
                return this.action;
            }

            public T getTarget()
            {
                return target;
            }

            @Override
            public int compareTo(Mapping<T> o)
            {
                int ret = this.registry.getRegistryName().compareNamespaced(o.registry.getRegistryName());
                if (ret ==0) ret = this.key.compareNamespaced(o.key);
                return ret;
            }
        }
    }

    /**
     * Called whenever the ID mapping might have changed. If you register for this event, you
     * will be called back whenever the client or server loads an ID set. This includes both
     * when the ID maps are loaded from disk, as well as when the ID maps revert to the initial
     * state.
     *
     * Note: you cannot change the IDs that have been allocated, but you might want to use
     * this event to update caches or other in-mod artifacts that might be impacted by an ID
     * change.
     *
     * Fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
     */
    public static class IdMappingEvent extends Event
    {
        public static class ModRemapping
        {
            public final ResourceLocation registry;
            public final ResourceLocation key;
            public final int oldId;
            public final int newId;

            private ModRemapping(ResourceLocation registry, ResourceLocation key, int oldId, int newId)
            {
                this.registry = registry;
                this.key = key;
                this.oldId = oldId;
                this.newId = newId;
            }
        }

        private final Map<ResourceLocation, ImmutableList<ModRemapping>> remaps;
        private final ImmutableSet<ResourceLocation> keys;

        public final boolean isFrozen;
        public IdMappingEvent(Map<ResourceLocation, Map<ResourceLocation, Integer[]>> remaps, boolean isFrozen)
        {
            this.isFrozen = isFrozen;
            this.remaps = Maps.newHashMap();
            remaps.forEach((name, rm) ->
            {
                List<ModRemapping> tmp = Lists.newArrayList();
                rm.forEach((key, value) -> tmp.add(new ModRemapping(name, key, value[0], value[1])));
                tmp.sort(Comparator.comparingInt(o -> o.newId));
                this.remaps.put(name, ImmutableList.copyOf(tmp));
            });
            this.keys = ImmutableSet.copyOf(this.remaps.keySet());
        }

        public ImmutableSet<ResourceLocation> getRegistries()
        {
            return this.keys;
        }

        public ImmutableList<ModRemapping> getRemaps(ResourceLocation registry)
        {
            return this.remaps.get(registry);
        }
    }
}
