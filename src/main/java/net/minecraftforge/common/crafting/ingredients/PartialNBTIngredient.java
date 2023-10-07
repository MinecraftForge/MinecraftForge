/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.ingredients;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Ingredient that matches the given items, performing a partial NBT match. Use {@link StrictNBTIngredient} if you want exact match on NBT */
public class PartialNBTIngredient extends AbstractIngredient {
    public static Builder builder() {
        return new Builder();
    }

    private final List<Item> items;
    private final CompoundTag nbt;
    private final NbtPredicate predicate;

    private PartialNBTIngredient(List<Item> items, CompoundTag nbt) {
        super(items.stream().map(item -> {
            var stack = new ItemStack(item);
            // copy NBT to prevent the stack from modifying the original, as capabilities or vanilla item durability will modify the tag
            stack.setTag(nbt.copy());
            return new Ingredient.ItemValue(stack);
        }));

        if (items.isEmpty())
            throw new IllegalArgumentException("Cannot create a PartialNBTIngredient with no items");

        this.items = Collections.unmodifiableList(items);
        this.nbt = nbt;
        this.predicate = new NbtPredicate(nbt);
    }

    /** Creates a new ingredient matching any item from the list, containing the given NBT */
    public static PartialNBTIngredient of(CompoundTag nbt, ItemLike... items) {
        return new PartialNBTIngredient(Arrays.stream(items).map(ItemLike::asItem).toList(), nbt);
    }

    /** Creates a new ingredient matching the given item, containing the given NBT  */
    public static PartialNBTIngredient of(ItemLike item, CompoundTag nbt) {
        return new PartialNBTIngredient(List.of(item.asItem()), nbt);
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null)
            return false;
        return items.contains(input.getItem()) && predicate.matches(input.getShareTag());
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> serializer() {
        return SERIALIZER;
    }

    public static final Codec<PartialNBTIngredient> CODEC = RecordCodecBuilder.create(builder ->
        builder.group(
            ForgeRegistries.ITEMS.getCodec().listOf().fieldOf("items").forGetter(i -> i.items),
            TagParser.AS_CODEC.fieldOf("nbt").forGetter(i -> i.nbt)
        ).apply(builder, PartialNBTIngredient::new)
    );

    public static final IIngredientSerializer<PartialNBTIngredient> SERIALIZER = new IIngredientSerializer<>() {
        @Override
        public Codec<? extends PartialNBTIngredient> codec() {
            return CODEC;
        }

        @Override
        public PartialNBTIngredient read(FriendlyByteBuf buffer) {
            var items = buffer.readList(b -> b.readRegistryIdUnsafe(ForgeRegistries.ITEMS));
            var nbt = buffer.readNbt();
            return new PartialNBTIngredient(items, Objects.requireNonNull(nbt));
        }

        @Override
        public void write(FriendlyByteBuf buffer, PartialNBTIngredient value) {
            buffer.writeCollection(value.items, (b, item) -> b.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item));
            buffer.writeNbt(value.nbt);
        }
    };

    public static class Builder {
        private List<ItemLike> items = new ArrayList<>();
        private CompoundTag nbt;

        public Builder nbt(CompoundTag value) {
            if (this.nbt != null)
                throw new IllegalStateException("NBT Tag already set");
            this.nbt = value;
            return this;
        }

        public Builder item(ItemLike item) {
            this.items.add(item);
            return this;
        }

        public Builder items(ItemLike... values) {
            for (var item : values)
                this.items.add(item);
            return this;
        }

        public PartialNBTIngredient build() {
            if (nbt == null)
                throw new IllegalStateException("NBT Data not set");
            if (items.isEmpty())
                throw new IllegalStateException("No items added");

            return PartialNBTIngredient.of(nbt, items.stream().toArray(ItemLike[]::new));
        }
    }
}
