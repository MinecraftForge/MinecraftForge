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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.ItemLike;

public class SimpleCraftingContainer {
    public static Builder builder() {
        return new Builder();
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

        public CraftingInput build() {
            var unseen = new CharArraySet(this.keys.keySet());
            unseen.remove(' ');

            int height = this.rows.size();
            if (height == 0)
                throw new IllegalStateException("Invalid builder, empty inventory");
            int width = this.rows.getFirst().length();
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

            return CraftingInput.of(width, height, items);
        }
    }
}
