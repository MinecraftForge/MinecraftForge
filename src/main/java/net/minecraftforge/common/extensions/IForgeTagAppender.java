/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.extensions;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface IForgeTagAppender<T>
{
    private TagsProvider.TagAppender<T> self() {
        return (TagsProvider.TagAppender<T>) this;
    }

    @SuppressWarnings("unchecked")
    default TagsProvider.TagAppender<T> addTags(Tag.Named<T>... values) {
        TagsProvider.TagAppender<T> builder = self();
        for (Tag.Named<T> value : values) {
            builder.addTag(value);
        }
        return builder;
    }

    default TagsProvider.TagAppender<T> add(ResourceKey<T>... keys) {
        TagsProvider.TagAppender<T> builder = self();
        for (ResourceKey<T> key : keys) {
            builder.getInternalBuilder().addElement(key.location(), self().getModID());
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

    default TagsProvider.TagAppender<T> addOptional(final ResourceLocation location)
    {
        return self().add(new Tag.OptionalElementEntry(location));
    }

    default TagsProvider.TagAppender<T> addOptionalTag(final ResourceLocation location)
    {
        return self().add(new Tag.OptionalTagEntry(location));
    }
    
    /**
     * Adds a registry entry to the tag json's remove list. Callable during datageneration.
     * @param element The entry to remove
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
    default TagsProvider.TagAppender<T> remove(Tag.Named<T> tag)
    {
        TagsProvider.TagAppender<T> builder = self();
        builder.getInternalBuilder().removeTag(tag.getName(), builder.getModID());
        return builder;
    }
    
    /**
     * Adds multiple tags to the tag json's remove list. Callable during datageneration.
     * @param tags The IDs of the tags to remove
     * @return The builder for chaining
     */
    @SuppressWarnings("unchecked")
    default TagsProvider.TagAppender<T> remove(Tag.Named<T> first, Tag.Named<T>...tags)
    {
        this.remove(first);
        for (Tag.Named<T> tag : tags)
        {
            this.remove(tag);
        }
        return self();
    }
}
