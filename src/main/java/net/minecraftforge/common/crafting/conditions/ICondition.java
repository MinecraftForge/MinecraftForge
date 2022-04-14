/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;

import java.util.Collections;
import java.util.Map;

public interface ICondition
{
    ResourceLocation getID();

    default boolean test(IContext context)
    {
        return test();
    }

    /**
     * @deprecated Use {@linkplain #test(IContext) the other more general overload}.
     */
    @Deprecated(forRemoval = true, since = "1.18.2")
    boolean test();

    interface IContext
    {
        IContext EMPTY = new IContext()
        {
            @Override
            public <T> Map<ResourceLocation, Tag<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry)
            {
                return Collections.emptyMap();
            }
        };

        /**
         * Return the requested tag if available, or an empty tag otherwise.
         */
        default <T> Tag<Holder<T>> getTag(TagKey<T> key)
        {
            return getAllTags(key.registry()).getOrDefault(key.location(), Tag.empty());
        }

        /**
         * Return all the loaded tags for the passed registry, or an empty map if none is available.
         * Note that the map and the tags are unmodifiable.
         */
        <T> Map<ResourceLocation, Tag<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry);
    }
}
