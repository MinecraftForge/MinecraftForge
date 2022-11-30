/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public interface ICondition
{

    Decoder<Boolean> DECODER = new Decoder<>() {
        @Override
        public <T> DataResult<Pair<Boolean, T>> decode(DynamicOps<T> ops, T input)
        {
            if(input instanceof JsonObject obj && obj.has("forge:conditions"))
            {
                try
                {
                    boolean result = CraftingHelper.processConditions(obj, "forge:conditions", IContext.TAGS_INVALID);    
                    return DataResult.success(Pair.of(result, input));
                } 
                catch(Exception ex)
                {
                    ex.printStackTrace();
                    return DataResult.success(Pair.of(false, input)); // Print stacktrace and do not load errored conditions.
                }
            }
            return DataResult.success(Pair.of(true, input));
        }
    };

    ResourceLocation getID();

    boolean test(IContext context);

    interface IContext
    {
        IContext EMPTY = new IContext()
        {
            @Override
            public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry)
            {
                return Collections.emptyMap();
            }
        };

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
         */
        default <T> Collection<Holder<T>> getTag(TagKey<T> key)
        {
            return getAllTags(key.registry()).getOrDefault(key.location(), Set.of());
        }

        /**
         * Return all the loaded tags for the passed registry, or an empty map if none is available.
         * Note that the map and the tags are unmodifiable.
         */
        <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry);
    }
}
