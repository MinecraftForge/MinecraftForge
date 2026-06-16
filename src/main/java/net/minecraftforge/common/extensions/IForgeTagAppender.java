/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

public interface IForgeTagAppender<T> {
    private TagAppender<T> self() {
        return (TagAppender<T>)this;
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

    @SuppressWarnings("unchecked")
    default TagAppender<T> addTags(TagKey<T>... values) {
        var builder = self();
        for (TagKey<T> value : values) {
            builder.addTag(value);
        }
        return builder;
    }

    default TagAppender<T> addOptional(Identifier location) {
        self().getInternalBuilder().addOptionalElement(location);
        return self();
    }

    @SuppressWarnings("unchecked")
    default TagAppender<T> addOptionalTags(TagKey<T>... values) {
        var builder = self();
        for (var value : values)
            builder.addOptionalTag(value);
        return builder;
    }

    default TagAppender<T> replace() {
        return replace(true);
    }

    default TagAppender<T> replace(boolean value) {
        self().getInternalBuilder().setReplace(value);
        return self();
    }

    /**
     * Adds a single element's ID to the tag json's remove list. Only available during data generation.
     * @param location The ID of the element to remove
     * @return The builder for chaining
     */
    default TagAppender<T> remove(final Identifier location) {
        var builder = self();
        builder.getInternalBuilder().removeElement(location, builder.getSourceName());
        return builder;
    }

    /**
     * Adds multiple elements' IDs to the tag json's remove list. Only available during data generation.
     * @param locations The IDs of the elements to remove
     * @return The builder for chaining
     */
    default TagAppender<T> remove(final Identifier first, final Identifier... locations) {
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
    default TagAppender<T> remove(final ResourceKey<T> resourceKey) {
        this.remove(resourceKey.identifier());
        return self();
    }

    /**
     * Adds a tag to the tag json's remove list. Only available during data generation.
     * @param tag The ID of the tag to remove
     * @return The builder for chaining
     */
    default TagAppender<T> remove(TagKey<T> tag) {
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
    default TagAppender<T> remove(TagKey<T> first, TagKey<T>...tags) {
        this.remove(first);
        for (var tag : tags)
            this.remove(tag);
        return self();
    }
}
