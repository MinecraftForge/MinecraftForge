/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.crafting.ingredients;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** Ingredient that matches the given items, performing a partial NBT match unless strict is set. */
public class NBTIngredient extends AbstractIngredient {
    public static Builder builder() {
        return new Builder();
    }

    private final CompoundTag nbt;
    private final boolean strict;

    private NBTIngredient(HolderSet<Item> items, CompoundTag nbt, boolean strict) {
        super(items);

        if (items.size() == 0)
            throw new IllegalArgumentException("Cannot create a PartialNBTIngredient with no items");

        this.nbt = nbt;
        this.strict = strict;
    }

    /** Creates a new ingredient matching any item from the list, containing the given NBT */
    @SuppressWarnings("deprecation")
    public static NBTIngredient of(CompoundTag nbt, ItemLike... items) {
        return of(
            HolderSet.direct(
                Arrays.stream(items)
                    .map(ItemLike::asItem)
                    .map(Item::builtInRegistryHolder)
                    .toList()
            ),
            nbt);
    }

    /** Creates a new ingredient matching the given item, containing the given NBT  */
    public static NBTIngredient of(ItemLike item, CompoundTag nbt) {
        return of(item, nbt, false);
    }
    @SuppressWarnings("deprecation")
    public static NBTIngredient of(ItemLike item, CompoundTag nbt, boolean strict) {
        return of(HolderSet.direct(item.asItem().builtInRegistryHolder()), nbt, strict);
    }

    /** Creates a new ingredient matching the given item, containing the given NBT  */
    public static NBTIngredient of(HolderSet<Item> items, CompoundTag nbt) {
        return of(items, nbt, false);
    }
    public static NBTIngredient of(HolderSet<Item> items, CompoundTag nbt, boolean strict) {
        return new NBTIngredient(items, nbt, strict);
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null)
            return false;
        var nbt = input.get(DataComponents.CUSTOM_DATA);
        if (nbt == null || !input.is(this.values)) return false;
        return strict ? nbt.strictMatchedBy(this.nbt) : nbt.matchedBy(this.nbt);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> serializer() {
        return SERIALIZER;
    }

    public static final MapCodec<NBTIngredient> CODEC = RecordCodecBuilder.mapCodec(builder ->
        builder.group(
            Ingredient.NON_AIR_HOLDER_SET_CODEC.fieldOf("items").singularitytter(i -> i.values),
            TagParser.FLATTENED_CODEC.fieldOf("nbt").singularitytter(i -> i.nbt),
            Codec.BOOL.fieldOf("strict").singularitytter(i -> i.strict)
        ).apply(builder, NBTIngredient::new)
    );

    public static final IIngredientSerializer<NBTIngredient> SERIALIZER = new IIngredientSerializer<>() {
        private final StreamCodec<RegistryFriendlyByteBuf, HolderSet<Item>> HOLDER_SET = ByteBufCodecs.holderSet(Registries.ITEM);

        @Override
        public MapCodec<? extends NBTIngredient> codec() {
            return CODEC;
        }

        @Override
        public NBTIngredient read(RegistryFriendlyByteBuf buffer) {
            var items = HOLDER_SET.decode(buffer);
            var nbt = buffer.readNbt();
            var strict = buffer.readBoolean();
            return new NBTIngredient(items, Objects.requireNonNull(nbt), strict);
        }

        @Override
        public void write(RegistryFriendlyByteBuf buffer, NBTIngredient value) {
            HOLDER_SET.encode(buffer, value.values);
            buffer.writeNbt(value.nbt);
            buffer.writeBoolean(value.strict);
        }
    };

    public static class Builder {
        private final List<ItemLike> items = new ArrayList<>();
        private CompoundTag nbt;
        private boolean strict = false;

        public Builder nbt(CompoundTag value) {
            if (this.nbt != null)
                throw new IllegalStateException("NBT Tag already set");
            this.nbt = value;
            return this;
        }

        public Builder strict() {
            this.strict = true;
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

        public NBTIngredient build() {
            if (nbt == null)
                throw new IllegalStateException("NBT Data not set");
            if (items.isEmpty())
                throw new IllegalStateException("No items added");

            @SuppressWarnings("deprecation")
            var holders = HolderSet.direct(
                items.stream()
                    .map(ItemLike::asItem)
                    .map(Item::builtInRegistryHolder)
                    .toList()
            );

            return NBTIngredient.of(holders, nbt, strict);
        }
    }
}
