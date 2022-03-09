/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.tags;

import net.minecraft.tags.TagKey;

import java.util.stream.Stream;

public interface IReverseTag<V>
{
    Stream<TagKey<V>> getTagKeyStream();

    boolean containsTag(TagKey<V> key);

    default boolean containsTag(ITag<V> tag)
    {
        return containsTag(tag.getKey());
    }
}
