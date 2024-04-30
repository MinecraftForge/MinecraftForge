/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.ingredients;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/** Ingredient that matches everything from the first ingredient that is not included in the second ingredient */
public class DifferenceIngredient extends AbstractIngredient {
    /**
     * Gets the difference from the two ingredients
     * @param base        Ingredient the item must match
     * @param subtracted  Ingredient the item must not match
     * @return  Ingredient that matches anything in {@code base} that is not in {@code subtracted}
     */
    public static DifferenceIngredient of(Ingredient base, Ingredient subtracted) {
        return new DifferenceIngredient(base, subtracted);
    }

    private final Ingredient base;
    private final Ingredient subtracted;
    private ItemStack[] filteredMatchingStacks;
    private IntList packedMatchingStacks;

    private DifferenceIngredient(Ingredient base, Ingredient subtracted) {
        this.base = base;
        this.subtracted = subtracted;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        return base.test(stack) && !subtracted.test(stack);
    }

    @Override
    public ItemStack[] getItems() {
        if (this.filteredMatchingStacks == null)
            this.filteredMatchingStacks = Arrays.stream(base.getItems())
                                                .filter(stack -> !subtracted.test(stack))
                                                .toArray(ItemStack[]::new);
        return filteredMatchingStacks;
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public boolean isSimple() {
        return base.isSimple() && subtracted.isSimple();
    }

    @Override
    protected void invalidate() {
        super.invalidate();
        this.filteredMatchingStacks = null;
        this.packedMatchingStacks = null;
    }

    @Override
    public IntList getStackingIds() {
        if (this.packedMatchingStacks == null || checkInvalidation()) {
            markValid();
            var matchingStacks = getItems();
            this.packedMatchingStacks = new IntArrayList(matchingStacks.length);
            for (var stack : matchingStacks)
                this.packedMatchingStacks.add(StackedContents.getStackingIndex(stack));

            this.packedMatchingStacks.sort(IntComparators.NATURAL_COMPARATOR);
        }
        return packedMatchingStacks;
    }

    @Override
    public IIngredientSerializer<DifferenceIngredient> serializer() {
        return SERIALIZER;
    }

    public static final MapCodec<DifferenceIngredient> CODEC = RecordCodecBuilder.mapCodec(builder ->
        builder.group(
            Ingredient.CODEC.fieldOf("base").forGetter(i -> i.base),
            Ingredient.CODEC.fieldOf("subtracted").forGetter(i -> i.subtracted)
        ).apply(builder, DifferenceIngredient::new)
    );

    public static final IIngredientSerializer<DifferenceIngredient> SERIALIZER = new IIngredientSerializer<>() {
        @Override
        public MapCodec<? extends DifferenceIngredient> codec() {
            return CODEC;
        }

        @Override
        public DifferenceIngredient read(RegistryFriendlyByteBuf buffer) {
            Ingredient base = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient without = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            return new DifferenceIngredient(base, without);
        }

        @Override
        public void write(RegistryFriendlyByteBuf buffer, DifferenceIngredient ingredient) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient.base);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient.subtracted);
        }
    };
}
