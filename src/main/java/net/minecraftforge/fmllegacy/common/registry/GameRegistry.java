/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fmllegacy.common.registry;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;


public class GameRegistry
{
    /**
     * Retrieves the registry associated with this super class type.
     * If the return is non-null it is HIGHLY recommended that modders cache this
     * value as the return will never change for a given type in a single run of Minecraft once set.
     *
     * @param registryType The base class of items in this registry.
     * @return The registry, Null if none is registered.
     */
    public static <K extends IForgeRegistryEntry<K>> IForgeRegistry<K> findRegistry(Class<K> registryType)
    {
        return RegistryManager.ACTIVE.getRegistry(registryType);
    }
}
