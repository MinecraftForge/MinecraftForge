/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.ingredients;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/** Ingredient that matches if all child ingredients match */
public class IntersectionIngredient extends AbstractIngredient {
    /**
     * Gets an intersection ingredient
     * @param ingredients  List of ingredients to match
     * @return  Ingredient that only matches if all the passed ingredients match
     */
    public static Ingredient of(Ingredient... ingredients) {
        if (ingredients.length == 0)
            throw new IllegalArgumentException("Cannot create an IntersectionIngredient with no children, use Ingredient.of() to create an empty ingredient");
        if (ingredients.length == 1)
            return ingredients[0];

        return new IntersectionIngredient(Arrays.asList(ingredients));
    }

    private final List<Ingredient> children;
    private final boolean isSimple;
    private List<Holder<Item>> items = null;

    private IntersectionIngredient(List<Ingredient> children) {
        if (children.size() < 2)
            throw new IllegalArgumentException("Cannot create an IntersectionIngredient with one or no children");
        this.children = Collections.unmodifiableList(children);
        this.isSimple = children.stream().allMatch(Ingredient::isSimple);
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;

        for (Ingredient ingredient : children)
            if (!ingredient.test(stack))
                return false;

        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Stream<Holder<Item>> items() {
        if (this.items == null) {
            var tmp = new ArrayList<Holder<Item>>();

            children.get(0).items().forEach(base -> {
                boolean allMatch = true;
                for (int i = 1; i < children.size(); i++) {
                    boolean match = children.get(i).items().anyMatch(base::is);
                    if (!match) {
                        allMatch = false;
                        break;
                    }
                }
                if (allMatch)
                    tmp.add(base);
            });
            this.items = Collections.unmodifiableList(tmp);
        }
        return items.stream();
    }

    @Override
    public boolean isSimple() {
        return isSimple;
    }

    @Override
    public IIngredientSerializer<IntersectionIngredient> serializer() {
        return SERIALIZER;
    }

    public static final MapCodec<IntersectionIngredient> CODEC = RecordCodecBuilder.mapCodec(builder ->
        builder.group(
            Ingredient.CODEC.listOf().fieldOf("children").forGetter(i -> i.children)
        )
        .apply(builder, IntersectionIngredient::new)
    );

    public static final IIngredientSerializer<IntersectionIngredient> SERIALIZER = new IIngredientSerializer<>() {
        @Override
        public MapCodec<? extends IntersectionIngredient> codec() {
            return CODEC;
        }

        @Override
        public IntersectionIngredient read(RegistryFriendlyByteBuf buffer) {
            var children = buffer.readCollection(ArrayList::new, _ -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            return new IntersectionIngredient(children);
        }

        @Override
        public void write(RegistryFriendlyByteBuf buffer, IntersectionIngredient value) {
            buffer.writeCollection(value.children, (_, child) -> Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, child));
        }
    };
}
