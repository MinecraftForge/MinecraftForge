/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public record TagEmptyCondition(TagKey<Item> tag) implements ICondition {
    public static final MapCodec<TagEmptyCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
        ResourceLocation.CODEC.xmap(loc -> TagKey.create(Registries.ITEM, loc), TagKey::location).fieldOf("tag").forGetter(TagEmptyCondition::tag)
    ).apply(b, TagEmptyCondition::new));


    @Override
    public boolean test(ICondition.IContext context, DynamicOps<?> ops) {
        return context.getTag(tag).isEmpty();
    }

    @Override
    public String toString() {
        return "tag_empty(\"" + tag.location() + "\")";
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
