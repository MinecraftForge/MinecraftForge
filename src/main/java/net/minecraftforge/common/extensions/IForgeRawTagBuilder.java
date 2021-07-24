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

import java.util.List;

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
     * @apiNote internal, called when a raw builder is written to json to add forge additions (e.g. the remove list)
     */
    default void serializeTagAdditions(final JsonObject tagJson)
    {
        Tag.Builder rawBuilder = this.getRawBuilder();
        List<Tag.BuilderEntry> removeEntries = rawBuilder.getRemoveEntries();
        if (!removeEntries.isEmpty())
        {
            JsonArray removeEntriesAsJsonArray = new JsonArray();
            for (Tag.BuilderEntry proxy : removeEntries)
            {
                proxy.getEntry().serializeTo(removeEntriesAsJsonArray);
            }
            tagJson.add("remove", removeEntriesAsJsonArray);
        }
    }
    
    /**
     * Adds a tag proxy to the remove list to generate a json with
     * @param proxy Tag proxy (either an item or another tag, usually)
     * @return builder for chaining
     * @apiNote internal; the other methods are preferable to call
     */
    default Tag.Builder removeProxy(final Tag.BuilderEntry proxy)
    {
        Tag.Builder rawBuilder = this.getRawBuilder();
        rawBuilder.getRemoveEntries().add(proxy);
        return rawBuilder;
    }
    
    /**
     * Adds a tag entry to the remove list.
     * @param entry The tag entry to add to the remove list
     * @param source The source of the caller for logging purposes (generally a modid)
     * @return The builder for chaining purposes
     */
    default Tag.Builder removeTagEntry(final Tag.Entry tagEntry, final String source)
    {
        return this.removeProxy(new Tag.BuilderEntry(tagEntry,source));
    }
    
    /**
     * Adds a single-element entry to the remove list.
     * @param elementID The ID of the element to add to the remove list
     * @param source The source of the caller for logging purposes (generally a modid)
     * @return The builder for chaining purposes
     */
    default Tag.Builder removeElementByID(final ResourceLocation elementID, final String source)
    {
        return this.removeTagEntry(new Tag.ElementEntry(elementID), source);
    }

    
    /**
     * Adds a tag to the remove list.
     * @param tagID The ID of the tag to add to the remove list
     * @param source The source of the caller for logging purposes (generally a modid)
     * @return The builder for chaining purposes
     */
    default Tag.Builder removeTagByID(final ResourceLocation tagID, final String source)
    {
        return this.removeTagEntry(new Tag.TagEntry(tagID), source);
    }
}
