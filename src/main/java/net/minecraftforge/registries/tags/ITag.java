/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.tags;

import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

/**
 * A tag is a collection of elements with an identifying {@link TagKey tag key}.
 * For Forge, these are bound on world load.
 * Tags will always be empty until they are bound.
 * A tag instance provided for a given {@link TagKey} from a given {@link ITagManager} will always return the same instance on future invocations.
 * This means that the same tag instance will be rebound across reloads assuming the same registry instance is in use.
 * It is safe to store instances of this class for long periods of time.
 */
public interface ITag<V> extends Iterable<V>
{
    TagKey<V> getKey();

    Stream<V> stream();

    boolean isEmpty();

    int size();

    boolean contains(V value);

    Optional<V> getRandomElement(RandomSource random);

    /**
     * @return {@code true} if this tag was loaded with a value (including empty),
     * otherwise the tag is always empty and this returns {@code false}
     */
    boolean isBound();
}
