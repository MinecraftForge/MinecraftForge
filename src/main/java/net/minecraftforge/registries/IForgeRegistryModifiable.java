/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.resources.ResourceLocation;

public interface IForgeRegistryModifiable<V> extends IForgeRegistry<V>
{
    void clear();
    V remove(ResourceLocation key);
    boolean isLocked();
}
