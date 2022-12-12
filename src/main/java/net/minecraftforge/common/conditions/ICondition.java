/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * An {@link ICondition} represents any type of loading condition that can be applied to a JSON object.
 */
public interface ICondition extends Predicate<ICondition.IContext>
{
    /**
     * @return The ID of the serializer that is responsible for this condition.
     */
    ResourceLocation getSerializerId();

    /**
     * Tests if this condition is met, given the context.
     * @param context The {@linkplain ICondition.IContext Condition Context}
     * @return True if this condition is met in this context.
     */
    @Override
    boolean test(IContext context);

    /**
     * The {@link IContext} object holds data that may be useful for evaluating conditions, but may be difficult to access otherwise.
     */
    interface IContext
    {
        /**
         * The empty context is used during parsing when a context is not available for any reason.
         */
        IContext EMPTY = new IContext()
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
        IContext TAGS_INVALID = new IContext()
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
}
