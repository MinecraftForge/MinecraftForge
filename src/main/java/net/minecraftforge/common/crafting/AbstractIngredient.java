/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/**
 * Extension of {@link Ingredient} which makes most methods custom ingredients need to implement abstract, and removes the static constructors
 * Mods are encouraged to extend this class for their custom ingredients
 */
public abstract class AbstractIngredient extends Ingredient
{
    /** Empty constructor, for the sake of dynamic ingredients */
    protected AbstractIngredient()
    {
        super(Stream.of());
    }

    /** Value constructor, for ingredients that have some vanilla representation */
    protected AbstractIngredient(Stream<? extends Value> values)
    {
        super(values);
    }

    @Override
    public abstract boolean isSimple();

    @Override
    public abstract IIngredientSerializer<? extends Ingredient> getSerializer();

    @Override
    public abstract JsonElement toJson();


    /* Hide vanilla ingredient static constructors to reduce errors with constructing custom ingredients */

    /** @deprecated use {@link Ingredient#fromValues(Stream)} */
    @Deprecated
    public static Ingredient fromValues(Stream<? extends Ingredient.Value> values)
    {
        throw new UnsupportedOperationException("Use Ingredient.fromValues()");
    }

    /** @deprecated use {@link Ingredient#of()} */
    @Deprecated
    public static Ingredient of()
    {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    /** @deprecated use {@link Ingredient#of(ItemLike...)} (Stream)} */
    @Deprecated
    public static Ingredient of(ItemLike... items)
    {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    /** @deprecated use {@link Ingredient#of(ItemStack...)} (Stream)} */
    @Deprecated
    public static Ingredient of(ItemStack... stacks)
    {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    /** @deprecated use {@link Ingredient#of(Stream)} (Stream)} */
    @Deprecated
    public static Ingredient of(Stream<ItemStack> stacks)
    {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    /** @deprecated use {@link Ingredient#of(TagKey)} (Stream)} */
    @Deprecated
    public static Ingredient of(TagKey<Item> tag)
    {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    /** @deprecated use {@link Ingredient#fromNetwork(FriendlyByteBuf)} */
    @Deprecated
    public static Ingredient fromNetwork(FriendlyByteBuf buffer)
    {
        throw new UnsupportedOperationException("Use Ingredient.fromNetwork()");
    }

    /** @deprecated use {@link Ingredient#fromJson(JsonElement)} (Stream)} */
    @Deprecated
    public static Ingredient fromJson(@Nullable JsonElement json)
    {
        throw new UnsupportedOperationException("Use Ingredient.fromJson()");
    }
}
