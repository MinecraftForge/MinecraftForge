/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import java.util.List;

/**
 * Factory for creation of all default condition types.
 * Designed to be used via FQN or static import.
 * If you want to access these methods without using either FQN or static imports, implement {@link IConditionBuilder}
 */
public class ConditionBuilder
{
    /**
     * Returns a new condition that is the logical and of the passed values.
     */
    public static ICondition and(ICondition... values)
    {
        return new AndCondition(List.of(values));
    }

    /**
     * Returns the false condition.
     */
    public static ICondition FALSE()
    {
        return FalseCondition.INSTANCE;
    }

    /**
     * Returns the true condition.
     */
    public static ICondition TRUE()
    {
        return TrueCondition.INSTANCE;
    }

    /**
     * Returns a new condition that is the inverse of the passed condition.
     */
    public static ICondition not(ICondition value)
    {
        return new NotCondition(value);
    }

    /**
     * Returns a new condition that is the logical or of the passed values.
     */
    public static ICondition or(ICondition... values)
    {
        return new OrCondition(List.of(values));
    }

    /**
     * Returns a new condition that checks if a specific item exists.
     */
    public static ICondition itemExists(String namespace, String path)
    {
        return new ItemExistsCondition(new ResourceLocation(namespace, path));
    }

    /**
     * Returns a new condition that checks if a specific mod is loaded.
     */
    public static ICondition modLoaded(String modid)
    {
        return new ModLoadedCondition(modid);
    }

    /**
     * Returns a new condition that returns true if a given tag is empty.
     */
    public static <T> ICondition tagEmpty(TagKey<T> tag)
    {
        return new TagEmptyCondition<>(tag);
    }

    /**
     * Returns a new condition that returns true if a given tag is empty.
     */
    public static <T> ICondition tagEmpty(ResourceKey<? extends Registry<T>> registry, ResourceLocation tag)
    {
        return new TagEmptyCondition<>(TagKey.create(registry, tag));
    }

    /**
     * Returns a new condition that returns true if a given tag exists (is not empty).
     */
    public static <T> ICondition tagExists(TagKey<T> tag)
    {
        return new TagExistsCondition<>(tag);
    }

    /**
     * Returns a new condition that returns true if a given tag exists (is not empty).
     */
    public static <T> ICondition tagExists(ResourceKey<? extends Registry<T>> registry, ResourceLocation tag)
    {
        return new TagExistsCondition<>(TagKey.create(registry, tag));
    }

    /**
     * Interface implementation of {@link ConditionBuilder}.
     * Allows for access to factory methods without FQN's or static imports.
     */
    public static interface IConditionBuilder
    {
        /**
         * @see ConditionBuilder#and
         */
        default ICondition and(ICondition... values)
        {
            return ConditionBuilder.and(values);
        }

        /**
         * @see ConditionBuilder#FALSE
         */
        default ICondition FALSE()
        {
            return ConditionBuilder.FALSE();
        }

        /**
         * @see ConditionBuilder#TRUE
         */
        default ICondition TRUE()
        {
            return ConditionBuilder.TRUE();
        }

        /**
         * @see ConditionBuilder#not
         */
        default ICondition not(ICondition value)
        {
            return ConditionBuilder.not(value);
        }

        /**
         * @see ConditionBuilder#or
         */
        default ICondition or(ICondition... values)
        {
            return ConditionBuilder.or(values);
        }

        /**
         * @see ConditionBuilder#itemExis
         */
        default ICondition itemExists(String namespace, String path)
        {
            return ConditionBuilder.itemExists(namespace, path);
        }

        /**
         * @see ConditionBuilder#modLoaded
         */
        default ICondition modLoaded(String modid)
        {
            return ConditionBuilder.modLoaded(modid);
        }

        /**
         * @see ConditionBuilder#tagEmpty
         */
        default <T> ICondition tagEmpty(TagKey<T> tag)
        {
            return ConditionBuilder.tagEmpty(tag);
        }

        /**
         * @see ConditionBuilder#tagEmpty(ResourceKey, ResourceLocation)
         */
        default <T> ICondition tagEmpty(ResourceKey<? extends Registry<T>> registry, ResourceLocation tag)
        {
            return ConditionBuilder.tagEmpty(registry, tag);
        }

        /**
         * @see ConditionBuilder#tagExists
         */
        default <T> ICondition tagExists(TagKey<T> tag)
        {
            return ConditionBuilder.tagExists(tag);
        }

        /**
         * @see ConditionBuilder#tagExists(ResourceKey, ResourceLocation)
         */
        default <T> ICondition tagExists(ResourceKey<? extends Registry<T>> registry, ResourceLocation tag)
        {
            return ConditionBuilder.tagExists(registry, tag);
        }
    }
}