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

public record TagExistsCondition<T>(TagKey<T> tag) implements Condition
{
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static TagExistsCondition<?> from(ResourceKey<?> registry, ResourceLocation tag)
    {
        return new TagExistsCondition<>(TagKey.create((ResourceKey) registry, tag));
    }

    @Override
    public boolean test(Condition.IContext context)
    {
        return !context.getTag(tag).isEmpty();
    }

    @Override
    public String toString()
    {
        return "tag_exists(\"" + tag.location() + "\")";
    }

    @Override
    public Codec<? extends Condition> codec()
    {
        return ForgeMod.TAG_EXISTS_CONDITION_TYPE.get();
    }
}
