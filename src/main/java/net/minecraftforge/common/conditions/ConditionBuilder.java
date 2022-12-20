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

public interface ConditionBuilder
{
    default LoadingCondition and(LoadingCondition... values)
    {
        return new AndCondition(List.of(values));
    }

    default LoadingCondition FALSE()
    {
        return FalseCondition.INSTANCE;
    }

    default LoadingCondition TRUE()
    {
        return TrueCondition.INSTANCE;
    }

    default LoadingCondition not(LoadingCondition value)
    {
        return new NotCondition(value);
    }

    default LoadingCondition or(LoadingCondition... values)
    {
        return new OrCondition(List.of(values));
    }

    default LoadingCondition itemExists(String namespace, String path)
    {
        return new ItemExistsCondition(new ResourceLocation(namespace, path));
    }

    default LoadingCondition modLoaded(String modid)
    {
        return new ModLoadedCondition(modid);
    }

    default <T> LoadingCondition tagEmpty(TagKey<T> tag)
    {
        return new TagEmptyCondition<>(tag);
    }

    default <T> LoadingCondition tagEmpty(ResourceKey<? extends Registry<T>> registry, ResourceLocation tag)
    {
        return new TagEmptyCondition<>(TagKey.create(registry, tag));
    }
}
