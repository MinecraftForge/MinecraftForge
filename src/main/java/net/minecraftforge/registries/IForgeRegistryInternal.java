/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.resources.ResourceLocation;

public interface IForgeRegistryInternal<V> extends IForgeRegistry<V>
{
    void setSlaveMap(ResourceLocation name, Object obj);

    void register(int id, ResourceLocation key, V value);
    V getValue(int id);
}
