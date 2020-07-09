/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;


/**
 * RegistryEvent supertype.
 */
public class RegistryEvent<T extends IForgeRegistryEntry<T>> extends GenericEvent<T>
{
    RegistryEvent(Class<T> clazz) {
        super(clazz);
    }
    /**
     * Register new registries when you receive this event, through the {@link RecipeBuilder}
     */
    public static class NewRegistry extends net.minecraftforge.eventbus.api.Event
    {
        @Override
        public String toString() {
            return "RegistryEvent.NewRegistry";
        }
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

        public ImmutableList<Mapping<T>> getMappings()
        {
            return ImmutableList.copyOf(this.mappings.stream().filter(e -> e.key.getNamespace().equals(this.activeMod.getModId())).collect(Collectors.toList()));
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

        public static class Mapping<T extends IForgeRegistryEntry<T>>
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
                Validate.isTrue(pool.getKey(target) != null, String.format("The specified entry %s hasn't been registered in registry yet.", target));
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
        }
    }
}
