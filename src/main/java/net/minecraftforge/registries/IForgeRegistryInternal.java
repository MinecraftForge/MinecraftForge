/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.util.ResourceLocation;

public interface IForgeRegistryInternal<V extends IForgeRegistryEntry<V>> extends IForgeRegistry<V>
{
    void setSlaveMap(ResourceLocation name, Object obj);
}
