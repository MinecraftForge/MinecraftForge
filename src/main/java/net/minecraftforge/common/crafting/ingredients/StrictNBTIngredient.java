/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.ingredients;

import java.util.stream.Stream;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Ingredient that matches the given stack, performing an exact NBT match. Use {@link PartialNBTIngredient} if you need partial match. */
public class StrictNBTIngredient extends AbstractIngredient {
    public static StrictNBTIngredient of(ItemStack stack) {
        return new StrictNBTIngredient(stack);
    }

    private final ItemStack stack;
    private StrictNBTIngredient(ItemStack stack) {
        super(Stream.of(new Ingredient.ItemValue(stack)));
        this.stack = stack;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null)
            return false;
        //Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
        var inbt = input.get(DataComponents.CUSTOM_DATA);
        var tnbt = this.stack.get(DataComponents.CUSTOM_DATA);
        if(inbt == null || tnbt == null)
            return false;

        return this.stack.getItem() == input.getItem() && tnbt.matchedBy(inbt.copyTag());
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> serializer() {
        return SERIALIZER;
    }

    public static final MapCodec<StrictNBTIngredient> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
        ItemStack.CODEC.fieldOf("stack").forGetter(i -> i.stack)
    ).apply(b, StrictNBTIngredient::new));

    public static final IIngredientSerializer<StrictNBTIngredient> SERIALIZER = new IIngredientSerializer<>() {
        @Override
        public MapCodec<? extends StrictNBTIngredient> codec() {
            return CODEC;
        }

        @Override
        public void write(RegistryFriendlyByteBuf buffer, StrictNBTIngredient value) {
            ItemStack.STREAM_CODEC.encode(buffer, value.stack);
        }

        @Override
        public StrictNBTIngredient read(RegistryFriendlyByteBuf buffer) {
            var stack = ItemStack.STREAM_CODEC.decode(buffer);
            return new StrictNBTIngredient(stack);
        }
    };
}
