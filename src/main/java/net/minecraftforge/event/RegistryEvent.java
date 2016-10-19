/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;
import net.minecraftforge.fml.common.eventhandler.IContextSetter;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;


/**
 * RegistryEvent supertype.
 */
public class RegistryEvent<T extends IForgeRegistryEntry<T>> extends GenericEvent<T> implements IContextSetter
{
    RegistryEvent(Class<T> clazz) {
        super(clazz);
    }
    /**
     * Register new registries when you receive this event, through the {@link PersistentRegistryManager}
     */
    public static class NewRegistry extends Event {
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
    public static class Register<T extends IForgeRegistryEntry<T>> extends RegistryEvent<T> {
        private final IForgeRegistry<T> registry;
        private final ResourceLocation location;

        public Register(ResourceLocation location, IForgeRegistry<T> registry)
        {
            super(registry.getRegistrySuperType());
            this.location = location;
            this.registry = registry;
        }

        public IForgeRegistry<T> getRegistry()
        {
            return registry;
        }

        public ResourceLocation getLocation()
        {
            return location;
        }

    }
}
