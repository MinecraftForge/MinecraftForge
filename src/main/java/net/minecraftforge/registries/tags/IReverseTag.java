/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.tags;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;

import java.util.stream.Stream;

/**
 * A reverse tag is an object aware of what tags it is contained in.
 * {@link Holder}s implement this interface.
 * A reverse tag makes no guarantees about its persistence relative to a registry value.
 * Modders should look up a reverse tag every time they need it from a {@link ITagManager} rather than storing it somewhere.
 */
public interface IReverseTag<V>
{
    Stream<TagKey<V>> getTagKeys();

    boolean containsTag(TagKey<V> key);

    default boolean containsTag(ITag<V> tag)
    {
        return containsTag(tag.getKey());
    }
}
