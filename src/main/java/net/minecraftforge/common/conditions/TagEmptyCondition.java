/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.conditions;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.ForgeMod;

public record TagEmptyCondition<T>(TagKey<T> tag) implements LoadingCondition
{
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static TagEmptyCondition<?> from(ResourceKey<?> registry, ResourceLocation tag)
    {
        return new TagEmptyCondition<>(TagKey.create((ResourceKey) registry, tag));
    }

    @Override
    public boolean test(IConditionContext context)
    {
        return context.getTag(tag).isEmpty();
    }

    @Override
    public String toString()
    {
        return "tag_empty(\"" + tag.location() + "\")";
    }

    @Override
    public Codec<? extends LoadingCondition> codec()
    {
        return ForgeMod.TAG_EMPTY_CONDITION_TYPE.get();
    }
}
