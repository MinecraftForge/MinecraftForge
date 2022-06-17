/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.tags;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A tag manager holds information about all tags currently bound to a forge registry.
 * This should be preferred to any {@link Holder}-related methods.
 */
public interface ITagManager<V> extends Iterable<ITag<V>>
{
    /**
     * Queries this tag manager for a tag with the given tag key.
     * If it does not exist, this will create an empty tag and return it.
     *
     * @apiNote This method guarantees that all future calls to this method on the same tag manager instance with the same tag key will return the same tag.
     * @see #isKnownTagName(TagKey)
     */
    @NotNull ITag<V> getTag(@NotNull TagKey<V> name);

    /**
     * Queries a reverse tag for a given value from the forge registry linked to this tag manager.
     * A reverse tag stores all tags that the given {@code value} is contained in.
     *
     * @param value A value currently registered to the forge registry linked to this tag manager
     * @return A reverse tag for the given value, or an empty optional if the value is not registered
     */
    @NotNull Optional<IReverseTag<V>> getReverseTag(@NotNull V value);

    /**
     * Checks whether the given tag key exists in this tag manager and is bound.
     * Unlike {@link #getTag(TagKey)}, this method will <b>not</b> create the tag if it does not exist.
     *
     * @see ITag#isBound()
     */
    boolean isKnownTagName(@NotNull TagKey<V> name);

    /**
     * @return A stream of all tags stored in this tag manager, bound or unbound.
     */
    @NotNull Stream<ITag<V>> stream();

    /**
     * @return A stream of all tag keys stored in this tag manager, bound or unbound.
     */
    @NotNull Stream<TagKey<V>> getTagNames();

    /**
     * Creates a tag key based on the location and the forge registry linked to this tag manager.
     * Custom registries can use {@link DeferredRegister#createTagKey(ResourceLocation)} to create tag keys before the tag manager is created.
     *
     * @see #createOptionalTagKey(ResourceLocation, Set)
     * @see DeferredRegister#createTagKey(ResourceLocation)
     */
    @NotNull TagKey<V> createTagKey(@NotNull ResourceLocation location);

    /**
     * Creates a tag key that will use the set of defaults if the tag is not loaded from any datapacks.
     * Useful on the client side when a server may not provide a specific tag.
     * Custom registries can use {@link DeferredRegister#addOptionalTagDefaults(TagKey, Set)} to create tag keys before the tag manager is created.
     *
     * @see #createTagKey(ResourceLocation)
     * @see #addOptionalTagDefaults(TagKey, Set)
     * @see DeferredRegister#createOptionalTagKey(ResourceLocation, Set)
     */
    @NotNull TagKey<V> createOptionalTagKey(@NotNull ResourceLocation location, @NotNull Set<? extends Supplier<V>> defaults);

    /**
     * Adds defaults to an existing tag key.
     * The set of defaults will be bound to the tag if the tag is not loaded from any datapacks.
     * Useful on the client side when a server may not provide a specific tag.
     * Custom registries can use {@link DeferredRegister#addOptionalTagDefaults(TagKey, Set)} to add defaults before the tag manager is created.
     *
     * @see #createOptionalTagKey(ResourceLocation, Set)
     * @see DeferredRegister#addOptionalTagDefaults(TagKey, Set)
     */
    void addOptionalTagDefaults(@NotNull TagKey<V> name, @NotNull Set<? extends Supplier<V>> defaults);
}
