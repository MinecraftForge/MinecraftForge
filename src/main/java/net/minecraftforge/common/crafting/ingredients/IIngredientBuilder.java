/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.ingredients;

import java.util.Arrays;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface IIngredientBuilder {
    default PartialNBTIngredient.Builder partialNbt() {
        return PartialNBTIngredient.builder();
    }

    default Ingredient strictNbt(ItemStack value) {
        return StrictNBTIngredient.of(value);
    }

    default Ingredient compound(Ingredient... values) {
        return CompoundIngredient.of(values);
    }

    default Ingredient intersection(@SuppressWarnings("rawtypes") TagKey... values) {
        return intersection(Arrays.stream(values).map(Ingredient::of).toArray(Ingredient[]::new));
    }

    default Ingredient intersection(Ingredient... values) {
        return IntersectionIngredient.of(values);
    }

    default Ingredient difference(TagKey<Item> base, TagKey<Item> subtracted) {
        return difference(Ingredient.of(base), Ingredient.of(subtracted));
    }

    default Ingredient difference(Ingredient base, Ingredient subtracted) {
        return DifferenceIngredient.of(base, subtracted);
    }
}
