/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import java.util.ArrayList;
import java.util.List;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class SimpleCraftingContainer implements CraftingContainer {
    private final int width;
    private final int height;
    private final NonNullList<ItemStack> items;

    public static Builder builder() {
        return new Builder();
    }

    public SimpleCraftingContainer(int width, int height) {
        this(width, height, NonNullList.withSize(width * height, ItemStack.EMPTY));
    }

    public SimpleCraftingContainer(int width, int height, NonNullList<ItemStack> items) {
        this.width = width;
        this.height = height;
        this.items = items;
        if (items.size() != (width * height))
            throw new IllegalArgumentException("Invalid item list, must be same size inventory width * height, received " + items.size() + " expected " + (width * height));
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().noneMatch(p -> !p.isEmpty());
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= this.items.size() ? ItemStack.EMPTY : this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        return ContainerHelper.removeItem(this.items, slot, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public void fillStackedContents(StackedContents stacked) {
        this.items.forEach(stacked::accountSimpleStack);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public List<ItemStack> getItems() {
        return List.copyOf(this.items);
    }

    public static class Builder {
        private final List<String> rows = new ArrayList<>();
        private final Char2ObjectMap<ItemStack> keys = new Char2ObjectOpenHashMap<>();

        private Builder() {
            this.define(' ', ItemStack.EMPTY);
        }

        public Builder pattern(String row) {
            if (!this.rows.isEmpty() && row.length() != this.rows.get(0).length())
                throw new IllegalArgumentException("Pattern must be the same width on every line");
            this.rows.add(row);
            return this;
        }

        public Builder pattern(String... rows) {
            for (var row : rows)
                pattern(row);
            return this;
        }

        public Builder define(char key, ItemLike item) {
            return define(key, new ItemStack(item));
        }

        public Builder define(char key, ItemStack stack) {
            if (this.keys.containsKey(key))
                throw new IllegalArgumentException("key '" + key + "' is already defined.");
            this.keys.put(key, stack);
            return this;
        }

        public SimpleCraftingContainer build() {
            var unseen = new CharArraySet(this.keys.keySet());
            unseen.remove(' ');

            int height = this.rows.size();
            if (height == 0)
                throw new IllegalStateException("Invalid builder, empty inventory");
            int width = this.rows.get(0).length();
            var items = NonNullList.withSize(width * height, ItemStack.EMPTY);

            int idx = 0;
            for (var row : this.rows) {
                for (int x = 0; x < width; x++) {
                    char key = row.charAt(x);
                    var stack = this.keys.get(key);
                    if (stack == null)
                        throw new IllegalStateException("Invalid builder pattern, missing value for key '" + key + "'");
                    unseen.remove(key);
                    items.set(idx++, stack.copy());
                }
            }

            if (!unseen.isEmpty())
                throw new IllegalStateException("Invalid builder, missing usage of defined keys: " + unseen);

            return new SimpleCraftingContainer(width, height, items);
        }
    }
}
