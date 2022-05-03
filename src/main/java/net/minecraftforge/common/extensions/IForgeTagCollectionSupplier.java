/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.Map;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IForgeTagCollectionSupplier
{
    /**
     * Gets the tag map of registry names to tag collections for the various custom tag types.
     *
     * @apiNote Prefer using one of the getCustomTypeCollection methods
     */
    default Map<ResourceLocation, ITagCollection<?>> getCustomTagTypes()
    {
        return ForgeTagHandler.getCustomTagTypes();
    }

    /**
     * Gets the {@link ITagCollection} for a forge registry with the given name, or throws an exception if the registry doesn't support custom tag types.
     * @param regName Name of the forge registry
     * @return The tag collection
     */
    default ITagCollection<?> getCustomTypeCollection(ResourceLocation regName)
    {
        if (!ForgeTagHandler.getCustomTagTypeNames().contains(regName)) throw new IllegalArgumentException("Registry " + regName + ", does not support custom tag types");
        return getCustomTagTypes().get(regName);
    }

    /**
     * Gets the {@link ITagCollection} for a forge registry, or throws an exception if the registry doesn't support custom tag types.
     * @param reg Forge registry
     * @return The tag collection
     */
    default <T extends IForgeRegistryEntry<T>> ITagCollection<T> getCustomTypeCollection(IForgeRegistry<T> reg)
    {
        return (ITagCollection<T>) getCustomTypeCollection(reg.getRegistryName());
    }
}