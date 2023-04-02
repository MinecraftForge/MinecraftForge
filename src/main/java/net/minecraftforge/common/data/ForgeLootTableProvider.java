/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.loot.CanToolPerformAction;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

/**
 * Currently used only for replacing shears item to shears_dig tool action
 */
public final class ForgeLootTableProvider extends LootTableProvider {
    public ForgeLootTableProvider(PackOutput packOutput) {
        super(packOutput, Set.of(), VanillaLootTableProvider.create(packOutput).getTables());
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationcontext) {
        // Do not validate against all registered loot tables
    }

    @Override
    public List<LootTableProvider.SubProviderEntry> getTables() {
        return super.getTables().stream().map(entry -> {
            // Provides new sub provider with filtering only changed loot tables and replacing condition item to condition tag
            return new LootTableProvider.SubProviderEntry(() -> replaceAndFilterChangesOnly(entry.provider().get()), entry.paramSet());
        }).collect(Collectors.toList());
    }

    private LootTableSubProvider replaceAndFilterChangesOnly(LootTableSubProvider subProvider) {
        return newConsumer -> subProvider.generate((resourceLocation, builder) -> {
            if (findAndReplaceInLootTableBuilder(builder, Items.SHEARS, ToolActions.SHEARS_DIG)) {
                newConsumer.accept(resourceLocation, builder);
            }
        });
    }

    private boolean findAndReplaceInLootTableBuilder(LootTable.Builder builder, Item from, ToolAction toolAction) {
        List<LootPool> lootPools = ObfuscationReflectionHelper.getPrivateValue(LootTable.Builder.class, builder, "f_7915" + "6_");
        boolean found = false;

        if (lootPools == null) {
            throw new IllegalStateException(LootTable.Builder.class.getName() + " is missing field f_7915" + "6_");
        }

        for (LootPool lootPool : lootPools) {
            if (findAndReplaceInLootPool(lootPool, from, toolAction)) {
                found = true;
            }
        }

        return found;
    }

    private boolean findAndReplaceInLootPool(LootPool lootPool, Item from, ToolAction toolAction) {
        LootPoolEntryContainer[] lootEntries = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, lootPool, "f_7902" +"3_");
        LootItemCondition[] lootConditions = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, lootPool, "f_7902" + "4_");
        boolean found = false;

        if (lootEntries == null) {
            throw new IllegalStateException(LootPool.class.getName() + " is missing field f_7902" + "3_");
        }

        for (LootPoolEntryContainer lootEntry : lootEntries) {
            if (findAndReplaceInLootEntry(lootEntry, from, toolAction)) {
                found = true;
            }
            if (lootEntry instanceof CompositeEntryBase) {
                if (findAndReplaceInParentedLootEntry((CompositeEntryBase) lootEntry, from, toolAction)) {
                    found = true;
                }
            }
        }

        if (lootConditions == null) {
            throw new IllegalStateException(LootPool.class.getName() + " is missing field f_7902" + "4_");
        }

        for (int i = 0; i < lootConditions.length; i++) {
            LootItemCondition lootCondition = lootConditions[i];
            if (lootCondition instanceof MatchTool && checkMatchTool((MatchTool) lootCondition, from)) {
                lootConditions[i] = CanToolPerformAction.canToolPerformAction(toolAction).build();
                found = true;
            } else if (lootCondition instanceof InvertedLootItemCondition) {
                LootItemCondition invLootCondition = ObfuscationReflectionHelper.getPrivateValue(InvertedLootItemCondition.class, (InvertedLootItemCondition) lootCondition, "f_8168" + "1_");

                if (invLootCondition instanceof MatchTool && checkMatchTool((MatchTool) invLootCondition, from)) {
                    lootConditions[i] = InvertedLootItemCondition.invert(CanToolPerformAction.canToolPerformAction(toolAction)).build();
                    found = true;
                } else if (invLootCondition instanceof CompositeLootItemCondition compositeLootItemCondition && findAndReplaceInComposite(compositeLootItemCondition, from, toolAction)) {
                    found = true;
                }
            }
        }

        return found;
    }

    private boolean findAndReplaceInParentedLootEntry(CompositeEntryBase entry, Item from, ToolAction toolAction) {
        LootPoolEntryContainer[] lootEntries = ObfuscationReflectionHelper.getPrivateValue(CompositeEntryBase.class, entry, "f_7942" + "8_");
        boolean found = false;

        if (lootEntries == null) {
            throw new IllegalStateException(CompositeEntryBase.class.getName() + " is missing field f_7942" + "8_");
        }

        for (LootPoolEntryContainer lootEntry : lootEntries) {
            if (findAndReplaceInLootEntry(lootEntry, from, toolAction)) {
                found = true;
            }
        }

        return found;
    }

    private boolean findAndReplaceInLootEntry(LootPoolEntryContainer entry, Item from, ToolAction toolAction) {
        LootItemCondition[] lootConditions = ObfuscationReflectionHelper.getPrivateValue(LootPoolEntryContainer.class, entry, "f_7963" + "6_");
        boolean found = false;

        if (lootConditions == null) {
            throw new IllegalStateException(LootPoolEntryContainer.class.getName() + " is missing field f_7963" + "6_");
        }

        for (int i = 0; i < lootConditions.length; i++) {
            if (lootConditions[i] instanceof CompositeLootItemCondition composite && findAndReplaceInComposite(composite, from, toolAction)) {
                found = true;
            } else if (lootConditions[i] instanceof MatchTool && checkMatchTool((MatchTool) lootConditions[i], from)) {
                lootConditions[i] = CanToolPerformAction.canToolPerformAction(toolAction).build();
                found = true;
            }
        }

        return found;
    }

    private boolean findAndReplaceInComposite(CompositeLootItemCondition alternative, Item from, ToolAction toolAction) {
        LootItemCondition[] lootConditions = ObfuscationReflectionHelper.getPrivateValue(CompositeLootItemCondition.class, alternative, "f_28560" + "9_");
        boolean found = false;

        if (lootConditions == null) {
            throw new IllegalStateException(CompositeLootItemCondition.class.getName() + " is missing field f_28560" + "9_");
        }

        for (int i = 0; i < lootConditions.length; i++) {
            if (lootConditions[i] instanceof MatchTool && checkMatchTool((MatchTool) lootConditions[i], from)) {
                lootConditions[i] = CanToolPerformAction.canToolPerformAction(toolAction).build();
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
