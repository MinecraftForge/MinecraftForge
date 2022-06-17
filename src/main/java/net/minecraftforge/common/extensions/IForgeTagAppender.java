/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public interface IForgeTagAppender<T>
{
    private TagsProvider.TagAppender<T> self() {
        return (TagsProvider.TagAppender<T>) this;
    }

    @SuppressWarnings("unchecked")
    default TagsProvider.TagAppender<T> addTags(TagKey<T>... values) {
        TagsProvider.TagAppender<T> builder = self();
        for (TagKey<T> value : values) {
            builder.addTag(value);
        }
        return builder;
    }

    default TagsProvider.TagAppender<T> replace() {
        return replace(true);
    }

    default TagsProvider.TagAppender<T> replace(boolean value) {
        self().getInternalBuilder().replace(value);
        return self();
    }

    /**
     * Adds a registry entry to the tag json's remove list. Callable during datageneration.
     * @param entry The entry to remove
     * @return The builder for chaining
     */
    default TagsProvider.TagAppender<T> remove(final T entry)
    {
        return remove(this.self().registry.getKey(entry));
    }

    /**
     * Adds multiple registry entries to the tag json's remove list. Callable during datageneration.
     * @param entries The entries to remove
     * @return The builder for chaining
     */
    @SuppressWarnings("unchecked")
    default TagsProvider.TagAppender<T> remove(final T first, final T...entries)
    {
        this.remove(first);
        for (T entry : entries)
        {
            this.remove(entry);
        }
        return self();
    }

    /**
     * Adds a single element's ID to the tag json's remove list. Callable during datageneration.
     * @param location The ID of the element to remove
     * @return The builder for chaining
     */
    default TagsProvider.TagAppender<T> remove(final ResourceLocation location)
    {
        TagsProvider.TagAppender<T> builder = self();
        builder.getInternalBuilder().removeElement(location, builder.getModID());
        return builder;
    }

    /**
     * Adds multiple elements' IDs to the tag json's remove list. Callable during datageneration.
     * @param locations The IDs of the elements to remove
     * @return The builder for chaining
     */
    default TagsProvider.TagAppender<T> remove(final ResourceLocation first, final ResourceLocation... locations)
    {
        this.remove(first);
        for (ResourceLocation location : locations)
        {
            this.remove(location);
        }
        return self();
    }

    /**
     * Adds a tag to the tag json's remove list. Callable during datageneration.
     * @param tag The ID of the tag to remove
     * @return The builder for chaining
     */
    default TagsProvider.TagAppender<T> remove(TagKey<T> tag)
    {
        TagsProvider.TagAppender<T> builder = self();
        builder.getInternalBuilder().removeTag(tag.location(), builder.getModID());
        return builder;
    }

    /**
     * Adds multiple tags to the tag json's remove list. Callable during datageneration.
     * @param tags The IDs of the tags to remove
     * @return The builder for chaining
     */
    @SuppressWarnings("unchecked")
    default TagsProvider.TagAppender<T> remove(TagKey<T> first, TagKey<T>...tags)
    {
        this.remove(first);
        for (TagKey<T> tag : tags)
        {
            this.remove(tag);
        }
        return self();
    }
}
