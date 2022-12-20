/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

/**
 * The {@link IConditionContext} object holds data that may be useful for evaluating conditions, but may be difficult to access otherwise.
 */
public interface IConditionContext
{
    /**
     * The empty context is used during parsing when a context is not available for any reason.
     */
    IConditionContext EMPTY = new IConditionContext()
    {
        @Override
        public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry)
        {
            return Collections.emptyMap();
        }
    };

    /**
     * The tags invalid context is used when tags will never be available, and attempting to access them is indicative of an error condition.<p>
     * An example of this is using conditions in dynamic registries, which load before tags.
     */
    IConditionContext TAGS_INVALID = new IConditionContext()
    {
        @Override
        public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry)
        {
            throw new UnsupportedOperationException("Usage of tag-based conditions is not permitted in this context!");
        }
    };

    /**
     * Return the requested tag if available, or an empty tag otherwise.
     * @param <T> The type of the tag being requested.
     * @param key The ID of the tag being requested.
     * @return The tag, or empty if unavailable.
     * @throws IllegalStateException If the tags have not been loaded yet.
     * @throws UnsupportedOperationException If tags are never supported in this context.
     */
    default <T> Collection<Holder<T>> getTag(TagKey<T> key)
    {
        return getAllTags(key.registry()).getOrDefault(key.location(), Set.of());
    }

    /**
     * Return all the loaded tags for the passed registry, or an empty map if none is available.
     * Note that the map and the tags are unmodifiable.
     * @param <T> The underlying type of the tag collection.
     * @param registry The ID of the registry.
     * @return An immutable map containing all loaded tags for the given registry (may be empty).
     * @throws IllegalStateException If the tags have not been loaded yet.
     * @throws UnsupportedOperationException If tags are never supported in this context.
     */
    <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry);
}