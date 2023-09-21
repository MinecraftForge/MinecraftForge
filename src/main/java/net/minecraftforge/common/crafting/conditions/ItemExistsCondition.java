/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public record ItemExistsCondition(ResourceLocation item) implements ICondition {
    public static final Codec<ItemExistsCondition> CODEC = RecordCodecBuilder.create(b -> b.group(
        ResourceLocation.CODEC.fieldOf("item").forGetter(ItemExistsCondition::item)
    ).apply(b, ItemExistsCondition::new));

    @Override
    public boolean test(IContext context) {
        return ForgeRegistries.ITEMS.containsKey(item);
    }

    @Override
    public String toString() {
        return "item_exists(\"" + item + "\")";
    }

    @Override
    public Codec<? extends ICondition> codec() {
        return CODEC;
    }
}
