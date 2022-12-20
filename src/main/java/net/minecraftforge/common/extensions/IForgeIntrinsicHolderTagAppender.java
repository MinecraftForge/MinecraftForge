/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;

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
    default TagsProvider.TagAppender<T> remove(final T entry)
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
    default TagsProvider.TagAppender<T> remove(final T first, final T...entries)
    {
        this.remove(first);
        for (T entry : entries)
        {
            this.remove(entry);
        }
        return self();
    }
}
