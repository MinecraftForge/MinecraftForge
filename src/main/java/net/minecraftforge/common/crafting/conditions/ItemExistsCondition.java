/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import java.util.Optional;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public record ItemExistsCondition(ResourceLocation item) implements ICondition {
    public static final MapCodec<ItemExistsCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
        ResourceLocation.CODEC.fieldOf("item").forGetter(ItemExistsCondition::item)
    ).apply(b, ItemExistsCondition::new));

    @Override
    public boolean test(IContext context, DynamicOps<?> ops) {
        // Check the actual deserialization context if possible
        if (ops instanceof RegistryOps<?> reg) {
            Optional<HolderGetter<Item>> items = reg.getter(Registries.ITEM);
            if (items.isPresent()) {
                var key = ResourceKey.create(Registries.ITEM, this.item);
                return items.get().get(key).isPresent();
            }
        }
        // Default to the active registry if its missing
        return ForgeRegistries.ITEMS.containsKey(item);
    }

    @Override
    public String toString() {
        return "item_exists(\"" + item + "\")";
    }

    @Override
    public  MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
