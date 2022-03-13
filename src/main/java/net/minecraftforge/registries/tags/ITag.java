/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.tags;

import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

/**
 * A tag is a collection of elements with an identifying {@link TagKey tag key}.
 * For Forge, these are bound on world load.
 * Tags will always be empty until they are bound.
 * A tag instance provided for a given {@link TagKey} from a given {@link ITagManager} will always return the same instance on future invocations.
 */
public interface ITag<V> extends Iterable<V>
{
    TagKey<V> getKey();

    Stream<V> stream();

    boolean isEmpty();

    int size();

    boolean contains(V value);

    Optional<V> getRandomElement(Random random);

    /**
     * @return true if this tag was loaded with a value (including empty), otherwise the tag is always empty and this returns false
     */
    boolean isBound();
}
