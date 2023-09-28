/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import java.util.stream.Stream;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Ingredient that matches the given stack, performing an exact NBT match. Use {@link PartialNBTIngredient} if you need partial match. */
public class StrictNBTIngredient extends Ingredient {
    public static final Codec<StrictNBTIngredient> CODEC = RecordCodecBuilder.create(b -> b.group(
        ItemStack.CODEC.fieldOf("stack").forGetter(i -> i.stack)
    ).apply(b, StrictNBTIngredient::new));

    private final ItemStack stack;
    protected StrictNBTIngredient(ItemStack stack) {
        super(Stream.of(new Ingredient.ItemValue(stack)));
        this.stack = stack;
    }

    /** Creates a new ingredient matching the given stack and tag */
    public static StrictNBTIngredient of(ItemStack stack) {
        return new StrictNBTIngredient(stack);
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null)
            return false;
        //Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
        return this.stack.getItem() == input.getItem() && this.stack.getDamageValue() == input.getDamageValue() && this.stack.areShareTagsEqual(input);
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
