/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.tags;

import net.minecraft.tags.TagKey;

import java.util.stream.Stream;

public interface ITag<V> extends Iterable<V>
{
    TagKey<V> getKey();

    Stream<V> stream();

    boolean contains(V value);

    /**
     * @return true if this tag was loaded with a value (including empty), otherwise the tag is always empty and this returns false
     */
    boolean isBound();
}
