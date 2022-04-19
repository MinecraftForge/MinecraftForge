/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;

public interface IForgeRawTagBuilder
{
    default Tag.Builder getRawBuilder()
    {
        return (Tag.Builder)this;
    }

    /**
     * internal, called when a raw builder is written to json to add forge additions (e.g. the remove list)
     */
    default void serializeTagAdditions(final JsonObject tagJson)
    {
        Tag.Builder rawBuilder = this.getRawBuilder();
        Stream<Tag.BuilderEntry> removeEntries = rawBuilder.getRemoveEntries();
        JsonArray removeEntriesAsJsonArray = new JsonArray();
        removeEntries.forEach(proxy ->proxy.entry().serializeTo(removeEntriesAsJsonArray));
        if (removeEntriesAsJsonArray.size() > 0)
        {
            tagJson.add("remove", removeEntriesAsJsonArray);
        }

    }

    /**
     * Adds a tag entry to the remove list.
     * @param tagEntry The tag entry to add to the remove list
     * @param source The source of the caller for logging purposes (generally a modid)
     * @return The builder for chaining purposes
     */
    default Tag.Builder remove(final Tag.Entry tagEntry, final String source)
    {
        return this.getRawBuilder().remove(new Tag.BuilderEntry(tagEntry,source));
    }

    /**
     * Adds a single-element entry to the remove list.
     * @param elementID The ID of the element to add to the remove list
     * @param source The source of the caller for logging purposes (generally a modid)
     * @return The builder for chaining purposes
     */
    default Tag.Builder removeElement(final ResourceLocation elementID, final String source)
    {
        return this.remove(new Tag.ElementEntry(elementID), source);
    }


    /**
     * Adds a tag to the remove list.
     * @param tagID The ID of the tag to add to the remove list
     * @param source The source of the caller for logging purposes (generally a modid)
     * @return The builder for chaining purposes
     */
    default Tag.Builder removeTag(final ResourceLocation tagID, final String source)
    {
        return this.remove(new Tag.TagEntry(tagID), source);
    }
}
