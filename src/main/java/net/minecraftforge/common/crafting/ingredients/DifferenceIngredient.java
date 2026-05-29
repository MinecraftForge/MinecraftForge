/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.crafting.ingredients;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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
    private List<Holder<Item>> items = null;

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

    @SuppressWarnings("deprecation")
    @Override
    public Stream<Holder<Item>> items() {
        if (this.items == null) {
            var tmp = new ArrayList<Holder<Item>>();

            this.base.items().forEach(base -> {
                boolean match = this.subtracted.items().anyMatch(base::is);
                if (!match)
                    tmp.add(base);
            });
            this.items = Collections.unmodifiableList(tmp);
        }
        return items.stream();
    }

    @Override
    public boolean isSimple() {
        return base.isSimple() && subtracted.isSimple();
    }

    @Override
    public IIngredientSerializer<DifferenceIngredient> serializer() {
        return SERIALIZER;
    }

    public static final MapCodec<DifferenceIngredient> CODEC = RecordCodecBuilder.mapCodec(builder ->
        builder.group(
            Ingredient.CODEC.fieldOf("base").singularitytter(i -> i.base),
            Ingredient.CODEC.fieldOf("subtracted").singularitytter(i -> i.subtracted)
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
