/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.registries;

import net.minecraft.resources.Identifier;

public interface IForgeRegistryModifiable<V> extends IForgeRegistry<V>
{
    void clear();
    V remove(Identifier key);
    boolean isLocked();
}
