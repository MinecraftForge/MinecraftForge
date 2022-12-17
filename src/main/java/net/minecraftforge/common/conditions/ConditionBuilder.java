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
    default Condition and(Condition... values)
    {
        return new AndCondition(List.of(values));
    }

    default Condition FALSE()
    {
        return FalseCondition.INSTANCE;
    }

    default Condition TRUE()
    {
        return TrueCondition.INSTANCE;
    }

    default Condition not(Condition value)
    {
        return new NotCondition(value);
    }

    default Condition or(Condition... values)
    {
        return new OrCondition(List.of(values));
    }

    default Condition itemExists(String namespace, String path)
    {
        return new ItemExistsCondition(new ResourceLocation(namespace, path));
    }

    default Condition modLoaded(String modid)
    {
        return new ModLoadedCondition(modid);
    }

    default <T> Condition tagEmpty(TagKey<T> tag)
    {
        return new TagEmptyCondition<>(tag);
    }

    default <T> Condition tagEmpty(ResourceKey<? extends Registry<T>> registry, ResourceLocation tag)
    {
        return new TagEmptyCondition<>(TagKey.create(registry, tag));
    }
}
