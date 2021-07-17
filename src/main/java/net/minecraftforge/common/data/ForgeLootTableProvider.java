/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.AlternativeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

/**
 * Currently used only for replacing shears item to shears tag
 */
public final class ForgeLootTableProvider extends LootTableProvider {

    public ForgeLootTableProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        // do not validate against all registered loot tables
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return super.getTables().stream().map(pair -> {
            // provides new consumer with filtering only changed loot tables and replacing condition item to condition tag
            return new Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>(() -> replaceAndFilterChangesOnly(pair.getFirst().get()), pair.getSecond());
        }).collect(Collectors.toList());
    }

    private Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> replaceAndFilterChangesOnly(Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> consumer) {
        return (newConsumer) -> consumer.accept((resourceLocation, builder) -> {
            if (findAndReplaceInLootTableBuilder(builder, Items.SHEARS, Tags.Items.SHEARS)) {
                newConsumer.accept(resourceLocation, builder);
            }
        });
    }

    private boolean findAndReplaceInLootTableBuilder(LootTable.Builder builder, Item from, Tag.Named<Item> to) {
        List<LootPool> lootPools = ObfuscationReflectionHelper.getPrivateValue(LootTable.Builder.class, builder, "f_7915" + "6_");
        boolean found = false;

        if (lootPools == null) {
            throw new IllegalStateException(LootTable.Builder.class.getName() + " is missing field f_7915" + "6_");
        }

        for (LootPool lootPool : lootPools) {
            if (findAndReplaceInLootPool(lootPool, from, to)) {
                found = true;
            }
        }

        return found;
    }

    private boolean findAndReplaceInLootPool(LootPool lootPool, Item from, Tag.Named<Item> to) {
        List<LootPoolEntryContainer> lootEntries = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, lootPool, "f_7902" +"3_");
        List<LootItemCondition> lootConditions = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, lootPool, "f_7902" + "4_");
        boolean found = false;

        if (lootEntries == null) {
            throw new IllegalStateException(LootPool.class.getName() + " is missing field f_7902" + "3_");
        }

        for (LootPoolEntryContainer lootEntry : lootEntries) {
            if (lootEntry instanceof CompositeEntryBase) {
                if (findAndReplaceInParentedLootEntry((CompositeEntryBase) lootEntry, from, to)) {
                    found = true;
                }
            }
        }

        if (lootConditions == null) {
            throw new IllegalStateException(LootPool.class.getName() + " is missing field f_7902" + "4_");
        }

        for (int i = 0; i < lootConditions.size(); i++) {
            LootItemCondition lootCondition = lootConditions.get(i);
            if (lootCondition instanceof MatchTool && checkMatchTool((MatchTool) lootCondition, from)) {
                lootConditions.set(i, MatchTool.toolMatches(ItemPredicate.Builder.item().of(to)).build());
                found = true;
            } else if (lootCondition instanceof InvertedLootItemCondition) {
                LootItemCondition invLootCondition = ObfuscationReflectionHelper.getPrivateValue(InvertedLootItemCondition.class, (InvertedLootItemCondition) lootCondition, "f_8168" + "1_");

                if (invLootCondition instanceof MatchTool && checkMatchTool((MatchTool) invLootCondition, from)) {
                    lootConditions.set(i, InvertedLootItemCondition.invert(MatchTool.toolMatches(ItemPredicate.Builder.item().of(to))).build());
                    found = true;
                } else if (invLootCondition instanceof AlternativeLootItemCondition && findAndReplaceInAlternative((AlternativeLootItemCondition) invLootCondition, from, to)) {
                    found = true;
                }
            }
        }

        return found;
    }

    private boolean findAndReplaceInParentedLootEntry(CompositeEntryBase entry, Item from, Tag.Named<Item> to) {
        LootPoolEntryContainer[] lootEntries = ObfuscationReflectionHelper.getPrivateValue(CompositeEntryBase.class, entry, "f_7942" + "8_");
        boolean found = false;

        if (lootEntries == null) {
            throw new IllegalStateException(CompositeEntryBase.class.getName() + " is missing field f_7942" + "8_");
        }

        for (LootPoolEntryContainer lootEntry : lootEntries) {
            if (findAndReplaceInLootEntry(lootEntry, from, to)) {
                found = true;
            }
        }

        return found;
    }

    private boolean findAndReplaceInLootEntry(LootPoolEntryContainer entry, Item from, Tag.Named<Item> to) {
        LootItemCondition[] lootConditions = ObfuscationReflectionHelper.getPrivateValue(LootPoolEntryContainer.class, entry, "f_7963" + "6_");
        boolean found = false;

        if (lootConditions == null) {
            throw new IllegalStateException(LootPoolEntryContainer.class.getName() + " is missing field f_7963" + "6_");
        }

        for (int i = 0; i < lootConditions.length; i++) {
            if (lootConditions[i] instanceof AlternativeLootItemCondition && findAndReplaceInAlternative((AlternativeLootItemCondition) lootConditions[i], from, to)) {
                found = true;
            } else if (lootConditions[i] instanceof MatchTool && checkMatchTool((MatchTool) lootConditions[i], from)) {
                lootConditions[i] = MatchTool.toolMatches(ItemPredicate.Builder.item().of(to)).build();
                found = true;
            }
        }

        return found;
    }

    private boolean findAndReplaceInAlternative(AlternativeLootItemCondition alternative, Item from, Tag.Named<Item> to) {
        LootItemCondition[] lootConditions = ObfuscationReflectionHelper.getPrivateValue(AlternativeLootItemCondition.class, alternative, "f_8146" + "8_");
        boolean found = false;

        if (lootConditions == null) {
            throw new IllegalStateException(AlternativeLootItemCondition.class.getName() + " is missing field f_8146" + "8_");
        }

        for (int i = 0; i < lootConditions.length; i++) {
            if (lootConditions[i] instanceof MatchTool && checkMatchTool((MatchTool) lootConditions[i], from)) {
                lootConditions[i] = MatchTool.toolMatches(ItemPredicate.Builder.item().of(to)).build();
                found = true;
            }
        }

        return found;
    }

    private boolean checkMatchTool(MatchTool lootCondition, Item expected) {
        ItemPredicate predicate = ObfuscationReflectionHelper.getPrivateValue(MatchTool.class, lootCondition, "f_8199" + "3_");
        Set<Item> items = ObfuscationReflectionHelper.getPrivateValue(ItemPredicate.class, predicate, "f_15142" + "7_");
        return items != null && items.contains(expected);
    }
}
