/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.data.TagsProvider;
import net.minecraft.tags.ITag;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;

//TODO, Tag removal support.
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
}
