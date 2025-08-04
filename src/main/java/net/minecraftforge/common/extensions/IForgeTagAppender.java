/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

public interface IForgeTagAppender<E, T> {
    private TagAppender<E, T> self() {
        return (TagAppender<E, T>)this;
    }

    /**
     * Gets the backing TagBuilder, will throw an exception if its not set.
     * This is only available during data generation, and if they use TagAppender.forBuilder
     */
    default TagBuilder getInternalBuilder() {
        throw new IllegalStateException("Could not determine internal tag builder");
    }

    default String getSourceName() {
        return "unknown";
    }

    /**
     * Adds a registry entry to the tag json's remove list. Callable during datageneration.
     *
     * @param entry The entry to remove
     * @return The builder for chaining
     */
    default TagAppender<E, T> remove(final E entry) {
        throw new UnsupportedOperationException("TagAppender.remove is not implemented in class: " + this.getClass());
    }

    /**
     * Adds multiple registry entries to the tag json's remove list. Callable during datageneration.
     *
     * @param entries The entries to remove
     * @return The builder for chaining
     */
    @SuppressWarnings("unchecked")
    default TagAppender<E, T> remove(final E first, final E...entries) {
        this.remove(first);
        for (E entry : entries)
            this.remove(entry);
        return self();
    }


    @SuppressWarnings("unchecked")
    default TagAppender<E, T> addTags(TagKey<T>... values) {
        var builder = self();
        for (TagKey<T> value : values) {
            builder.addTag(value);
        }
        return builder;
    }

    default TagAppender<E, T> addOptional(ResourceLocation location) {
        self().getInternalBuilder().addOptionalElement(location);
        return self();
    }

    @SuppressWarnings("unchecked")
    default TagAppender<E, T>addOptionalTags(TagKey<T>... values) {
        var builder = self();
        for (var value : values)
            builder.addOptionalTag(value);
        return builder;
    }

    default TagAppender<E, T> replace() {
        return replace(true);
    }

    default TagAppender<E, T> replace(boolean value) {
        self().getInternalBuilder().replace(value);
        return self();
    }

    /**
     * Adds a single element's ID to the tag json's remove list. Only available during data generation.
     * @param location The ID of the element to remove
     * @return The builder for chaining
     */
    default TagAppender<E, T> remove(final ResourceLocation location) {
        var builder = self();
        builder.getInternalBuilder().removeElement(location, builder.getSourceName());
        return builder;
    }

    /**
     * Adds multiple elements' IDs to the tag json's remove list. Only available during data generation.
     * @param locations The IDs of the elements to remove
     * @return The builder for chaining
     */
    default TagAppender<E, T> remove(final ResourceLocation first, final ResourceLocation... locations) {
        this.remove(first);
        for (var location : locations)
            this.remove(location);
        return self();
    }

    /**
     * Adds a resource key to the tag json's remove list. Only available during data generation.
     *
     * @param resourceKey The resource key of the element to remove
     * @return The appender for chaining
     */
    default TagAppender<E, T> remove(final ResourceKey<T> resourceKey) {
        this.remove(resourceKey.location());
        return self();
    }

    /**
     * Adds a tag to the tag json's remove list. Only available during data generation.
     * @param tag The ID of the tag to remove
     * @return The builder for chaining
     */
    default TagAppender<E, T> remove(TagKey<T> tag) {
        var builder = self();
        builder.getInternalBuilder().removeTag(tag.location(), builder.getSourceName());
        return builder;
    }

    /**
     * Adds multiple tags to the tag json's remove list. Only available during data generation.
     * @param tags The IDs of the tags to remove
     * @return The builder for chaining
     */
    @SuppressWarnings("unchecked")
    default TagAppender<E, T> remove(TagKey<T> first, TagKey<T>...tags) {
        this.remove(first);
        for (var tag : tags)
            this.remove(tag);
        return self();
    }
}
