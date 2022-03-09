/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.tags;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A tag manager holds information about all tags currently bound to a forge registry.
 * This should be preferred to any {@link Holder}-related methods.
 */
public interface ITagManager<V extends IForgeRegistryEntry<V>> extends Iterable<ITag<V>>
{
    /**
     * Will create an empty tag if it does not exist.
     */
    @NotNull ITag<V> getTag(TagKey<V> name);

    @NotNull Optional<IReverseTag<V>> getReverseTag(V value);

    boolean isKnownTagName(TagKey<V> name);

    @NotNull Stream<ITag<V>> stream();

    @NotNull Stream<TagKey<V>> getTagNames();

    @NotNull TagKey<V> createTagKey(ResourceLocation location);

    /**
     * Creates a tag key that will use the set of defaults if no tag JSON is found.
     * Useful on the client side when a server may not provide a specific tag.
     */
    @NotNull TagKey<V> createOptionalTagKey(ResourceLocation location, @NotNull Set<Supplier<V>> defaults);
}
