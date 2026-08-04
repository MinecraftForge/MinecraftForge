/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.common.registry;

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
