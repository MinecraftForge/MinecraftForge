/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.registries;

import net.minecraft.resources.Identifier;

public interface IForgeRegistryInternal<V> extends IForgeRegistry<V> {
    <T> void setSlaveMap(SlaveKey<T> name, T obj);

    void register(int id, Identifier key, V value);
    V getValue(int id);
}
