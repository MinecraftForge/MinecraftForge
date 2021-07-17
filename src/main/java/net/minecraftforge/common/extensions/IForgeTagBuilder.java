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

import net.minecraft.data.TagsProvider;
import net.minecraft.tags.ITag;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IForgeTagBuilder<T>
{

    default TagsProvider.Builder<T> getBuilder() {
        return (TagsProvider.Builder<T>) this;
    }

    @SuppressWarnings("unchecked")
    default TagsProvider.Builder<T> addTags(ITag.INamedTag<T>... values) {
        TagsProvider.Builder<T> builder = getBuilder();
        for (ITag.INamedTag<T> value : values) {
            builder.addTag(value);
        }
        return builder;
    }

    default TagsProvider.Builder<T> add(RegistryKey<T>... keys) {
        TagsProvider.Builder<T> builder = getBuilder();
        for (RegistryKey<T> key : keys) {
            builder.getInternalBuilder().addElement(key.location(), getBuilder().getModID());
        }
        return builder;
    }

    default TagsProvider.Builder<T> replace() {
        return replace(true);
    }

    default TagsProvider.Builder<T> replace(boolean value) {
        getBuilder().getInternalBuilder().replace(value);
        return getBuilder();
    }

    default TagsProvider.Builder<T> addOptional(final ResourceLocation location)
    {
        return getBuilder().add(new ITag.OptionalItemEntry(location));
    }

    default TagsProvider.Builder<T> addOptionalTag(final ResourceLocation location)
    {
        return getBuilder().add(new ITag.OptionalTagEntry(location));
    }
    
    /**
     * Adds a registry entry to the tag json's remove list. Callable during datageneration.
     * @param element The entry to remove
     * @return The builder for chaining
     */
    default TagsProvider.Builder<T> removeRegistryEntry(final IForgeRegistryEntry<T> entry)
    {
        return removeElementByID(entry.getRegistryName());
    }
    
    /**
     * Adds multiple registry entries to the tag json's remove list. Callable during datageneration.
     * @param entries The entries to remove
     * @return The builder for chaining
     */
    @SuppressWarnings("unchecked")
    default TagsProvider.Builder<T> removeRegistryEntries(final IForgeRegistryEntry<T>...entries)
    {
        for (IForgeRegistryEntry<T> entry : entries)
        {
            this.removeRegistryEntry(entry);
        }
        return getBuilder();
    }
    
    /**
     * Adds a single element's ID to the tag json's remove list. Callable during datageneration.
     * @param location The ID of the element to remove
     * @return The builder for chaining
     */
    default TagsProvider.Builder<T> removeElementByID(final ResourceLocation location)
    {
        TagsProvider.Builder<T> builder = getBuilder();
        builder.getInternalBuilder().removeElementByID(location, builder.getModID());
        return builder;
    }
    
    /**
     * Adds multiple elements' IDs to the tag json's remove list. Callable during datageneration.
     * @param locations The IDs of the elements to remove
     * @return The builder for chaining
     */
    default TagsProvider.Builder<T> removeElementsByID(final ResourceLocation... locations)
    {
        for (ResourceLocation location : locations)
        {
            this.removeElementByID(location);
        }
        return getBuilder();
    }
    
    /**
     * Adds a tag to the tag json's remove list. Callable during datageneration.
     * @param tag The ID of the tag to remove
     * @return The builder for chaining
     */
    default TagsProvider.Builder<T> removeTag(ITag.INamedTag<T> tag)
    {
        TagsProvider.Builder<T> builder = getBuilder();
        builder.getInternalBuilder().removeTagByID(tag.getName(), builder.getModID());
        return builder;
    }
    
    /**
     * Adds multiple tags to the tag json's remove list. Callable during datageneration.
     * @param tags The IDs of the tags to remove
     * @return The builder for chaining
     */
    @SuppressWarnings("unchecked")
    default TagsProvider.Builder<T> removeTags(ITag.INamedTag<T>...tags)
    {
        for (ITag.INamedTag<T> tag : tags)
        {
            this.removeTag(tag);
        }
        return getBuilder();
    }
}
