/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.Collection;
import org.jetbrains.annotations.Nullable;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.resource.DelegatingPackResources;

public interface IForgePackResources
{
    /**
     * {@return {@code true} if the pack should be hidden from any user interfaces}
     */
    default boolean isHidden()
    {
        return false;
    }

    /**
     * Gets a collection of {@code PackResource} instances nested inside this pack.
     * Used to merge several packs into one entry in the resource pack selection UI without
     * losing the ability for each pack to return a resource in
     * {@link ResourceManager#getResourceStack(ResourceLocation)}
     * @return Collection of nested {@code PackResource}, or null if this pack has no children
     * @see DelegatingPackResources
     */
    @Nullable default Collection<PackResources> getChildren() { return null; }
}
