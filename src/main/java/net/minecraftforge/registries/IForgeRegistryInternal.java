/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.resources.ResourceLocation;

public interface IForgeRegistryInternal<V extends IForgeRegistryEntry<V>> extends IForgeRegistry<V>
{
    void setSlaveMap(ResourceLocation name, Object obj);
}
