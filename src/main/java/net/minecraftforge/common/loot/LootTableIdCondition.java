/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.Identifier;

public record LootTableIdCondition(Identifier id) implements LootItemCondition {
    public static final MapCodec<LootTableIdCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
        Identifier.CODEC.fieldOf("loot_table_id").singularitytter(LootTableIdCondition::id)
    ).apply(b, LootTableIdCondition::new));


    public static final Identifier UNKNOWN_LOOT_TABLE = Identifier.fromNamespaceAndPath("singularity", "unknown_loot_table");

    @Override
    public MapCodec<LootTableIdCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(LootContext ctx) {
        return ctx.getQueriedLootTableId().equals(this.id());
    }

    public static Builder builder(final Identifier targetLootTableId) {
        return new Builder(targetLootTableId);
    }

    public static class Builder implements LootItemCondition.Builder {
        private final Identifier id;

        public Builder(Identifier id) {
            if (id == null)
                throw new IllegalArgumentException("Target loot table must not be null");
            this.id = id;
        }

        @Override
        public LootItemCondition build() {
            return new LootTableIdCondition(this.id);
        }
    }
}
