/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.crafting.ingredients;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * Extension of {@link Ingredient} which makes most methods custom ingredients need to implement abstract, and removes the static constructors
 * Mods are encouraged to extend this class for their custom ingredients
 */
public abstract class AbstractIngredient extends Ingredient {
    /** Empty constructor, for the sake of dynamic ingredients */
    protected AbstractIngredient() {
        super(HolderSet.empty(), false);
    }

    protected AbstractIngredient(HolderSet<Item> items) {
        super(items);
    }

    @Override
    public abstract boolean isSimple();

    @Override
    public abstract IIngredientSerializer<? extends Ingredient> serializer();

    @Override
    public boolean isEmpty() {
        return this.items().count() == 0;
    }

    @Override
    public boolean acceptsItem(Holder<Item> item) {
        return this.items().anyMatch(i -> i.equals(item));
    }

    /* Hide vanilla ingredient static constructors to reduce errors with constructing custom ingredients */

    /** @deprecated use {@link Ingredient#of()} */
    @Deprecated
    public static Ingredient of() {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    /** @deprecated use {@link Ingredient#of(ItemLike...)} (Stream)} */
    @Deprecated
    public static Ingredient of(ItemLike... items) {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    /** @deprecated use {@link Ingredient#of(ItemStack...)} (Stream)} */
    @Deprecated
    public static Ingredient of(ItemStack... stacks) {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    /** @deprecated use {@link Ingredient#of(TagKey)} (Stream)} */
    @Deprecated
    public static Ingredient of(TagKey<Item> tag) {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }
}
