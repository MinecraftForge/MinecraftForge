/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public interface IForgeIntrinsicHolderTagAppender<T> extends IForgeTagAppender<T>
{
    private IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> self() {
        return (IntrinsicHolderTagsProvider.IntrinsicTagAppender<T>) this;
    }

    ResourceKey<T> getKey(T value);

    /**
     * Adds a registry entry to the tag json's remove list. Callable during datageneration.
     *
     * @param entry The entry to remove
     * @return The builder for chaining
     */
    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(final T entry)
    {
        return remove(this.getKey(entry));
    }

    /**
     * Adds multiple registry entries to the tag json's remove list. Callable during datageneration.
     *
     * @param entries The entries to remove
     * @return The builder for chaining
     */
    @SuppressWarnings("unchecked")
    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(final T first, final T...entries)
    {
        this.remove(first);
        for (T entry : entries)
        {
            this.remove(entry);
        }
        return self();
    }

    @Override
    @SuppressWarnings("unchecked")
    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> addTags(TagKey<T>... values)
    {
        IForgeTagAppender.super.addTags(values);
        return self();
    }

    @Override
    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> replace()
    {
        IForgeTagAppender.super.replace();
        return self();
    }

    @Override
    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> replace(boolean value)
    {
        IForgeTagAppender.super.replace(value);
        return self();
    }

    @Override
    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(final ResourceLocation location)
    {
        IForgeTagAppender.super.remove(location);
        return self();
    }

    @Override
    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(final ResourceLocation first, final ResourceLocation... locations)
    {
        IForgeTagAppender.super.remove(first, locations);
        return self();
    }

    @Override
    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(final ResourceKey<T> resourceKey)
    {
        IForgeTagAppender.super.remove(resourceKey);
        return self();
    }

    @Override
    @SuppressWarnings("unchecked")
    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(final ResourceKey<T> firstResourceKey, final ResourceKey<T>... resourceKeys)
    {
        IForgeTagAppender.super.remove(firstResourceKey, resourceKeys);
        return self();
    }

    @Override
    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(TagKey<T> tag)
    {
        IForgeTagAppender.super.remove(tag);
        return self();
    }

    @Override
    @SuppressWarnings("unchecked")
    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(TagKey<T> first, TagKey<T>...tags)
    {
        IForgeTagAppender.super.remove(first, tags);
        return self();
    }
}
