/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;

public class ReverseTagWrapper<T>
{
    private final T target;
    private final Supplier<ITagCollection<T>> colSupplier;

    //This map is immutable we track its identity change.
    private Map<ResourceLocation, ITag<T>> colCache;
    private Set<ResourceLocation> cache = null;

    public ReverseTagWrapper(T target, Supplier<ITagCollection<T>> colSupplier)
    {
        this.target = target;
        this.colSupplier = colSupplier;
    }

    public Set<ResourceLocation> getTagNames()
    {
        ITagCollection<T> collection = colSupplier.get();
        if (cache == null || colCache != collection.getAllTags()) // Identity equals.
        {
            this.cache = Collections.unmodifiableSet(new HashSet<>(collection.getMatchingTags(target)));
            this.colCache = collection.getAllTags();
        }
        return this.cache;
    }
}
