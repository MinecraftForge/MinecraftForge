/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.resources.ResourceLocation;

public class ReverseTagWrapper<T>
{
    private final T target;
    private final Supplier<TagCollection<T>> colSupplier;

    //This map is immutable we track its identity change.
    private Map<ResourceLocation, Tag<T>> colCache;
    private Set<ResourceLocation> cache = null;

    public ReverseTagWrapper(T target, Supplier<TagCollection<T>> colSupplier)
    {
        this.target = target;
        this.colSupplier = colSupplier;
    }

    public Set<ResourceLocation> getTagNames()
    {
        TagCollection<T> collection = colSupplier.get();
        if (cache == null || colCache != collection.getAllTags()) // Identity equals.
        {
            this.cache = Collections.unmodifiableSet(new HashSet<>(collection.getMatchingTags(target)));
            this.colCache = collection.getAllTags();
        }
        return this.cache;
    }
}
