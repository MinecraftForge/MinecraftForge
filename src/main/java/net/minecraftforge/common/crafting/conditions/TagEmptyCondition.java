/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;

public record TagEmptyCondition(TagKey<Item> tag) implements ICondition {
    public static final Codec<TagEmptyCondition> CODEC = RecordCodecBuilder.create(b -> b.group(
        ResourceLocation.CODEC.xmap(loc -> TagKey.create(Registries.ITEM, loc), TagKey::location).fieldOf("tag").forGetter(TagEmptyCondition::tag)
    ).apply(b, TagEmptyCondition::new));


    @Override
    public boolean test(ICondition.IContext context) {
        return context.getTag(tag).isEmpty();
    }

    @Override
    public String toString() {
        return "tag_empty(\"" + tag.location() + "\")";
    }

    @Override
    public Codec<? extends ICondition> codec() {
        return CODEC;
    }
}
