/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;

public interface IForgeRawTagBuilder {
    default TagBuilder getRawBuilder() {
        return (TagBuilder)this;
    }

    /**
     * @deprecated Never used, tags use a Codec now, so remove this later.
     */
    @Deprecated(forRemoval = true, since = "1.20.1")
    default void serializeTagAdditions(final JsonObject tagJson) {}

    /**
     * Adds a tag entry to the remove list.
     * @param tagEntry The tag entry to add to the remove list
     * @param source The source of the caller for logging purposes (generally a modid)
     * @return The builder for chaining purposes
     */
    default TagBuilder remove(final TagEntry tagEntry, final String source) {
        return this.getRawBuilder().remove(tagEntry);
    }

    /**
     * Adds a single-element entry to the remove list.
     * @param elementID The ID of the element to add to the remove list
     * @param source The source of the caller for logging purposes (generally a modid)
     * @return The builder for chaining purposes
     */
    default TagBuilder removeElement(final ResourceLocation elementID, final String source) {
        return this.remove(TagEntry.element(elementID), source);
    }

    /**
     * Adds a tag to the remove list.
     * @param tagID The ID of the tag to add to the remove list
     * @param source The source of the caller for logging purposes (generally a modid)
     * @return The builder for chaining purposes
     */
    default TagBuilder removeTag(final ResourceLocation tagID, final String source) {
        return this.remove(TagEntry.tag(tagID), source);
    }
}
