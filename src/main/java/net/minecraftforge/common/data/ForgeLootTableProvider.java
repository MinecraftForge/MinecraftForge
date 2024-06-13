/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.loot.CanToolPerformAction;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

/**
 * Currently used only for replacing shears item to shears_dig tool action
 */
public final class ForgeLootTableProvider extends LootTableProvider {
    private static final String POOLS = "f_7915" + "6_"; // LootTable.Builder.pools
    private static final String ENTRIES = "f_7902" + "3_"; // LootPool.entries
    private static final String CONDITIONS = "f_7902" + "4_"; // LootPool.conditions
    private static final String CHILDREN = "f_7942" + "8_"; // CompositeEntryBase.children
    private static final String ENTRY_CONDITION = "f_7963" + "6_"; // LootPoolEntryContainer.conditions
    private static final String TERMS = "f_28560" + "9_"; // CompositeLootItemCondition.terms

    public ForgeLootTableProvider(PackOutput pack, CompletableFuture<HolderLookup.Provider> lookup) {
        super(pack, Set.of(), VanillaLootTableProvider.create(pack, lookup).getTables(), lookup);
    }

    @Override
    protected void validate(Registry<LootTable> map, ValidationContext validationcontext, ProblemReporter report) {
        // Do not validate against all registered loot tables
    }

    @Override
    public List<LootTableProvider.SubProviderEntry> getTables() {
        return super.getTables().stream().map(entry -> {
            // Provides new sub provider with filtering only changed loot tables and replacing condition item to condition tag
            return new LootTableProvider.SubProviderEntry(provider -> replaceAndFilterChangesOnly(entry.provider().apply(provider)), entry.paramSet());
        }).collect(Collectors.toList());
    }

    private LootTableSubProvider replaceAndFilterChangesOnly(LootTableSubProvider subProvider) {
        return (newConsumer) -> subProvider.generate((resourceLocation, builder) -> {
            if (findAndReplaceInLootTableBuilder(builder, Items.SHEARS, ToolActions.SHEARS_DIG)) {
                newConsumer.accept(resourceLocation, builder);
            }
        });
    }

    private boolean findAndReplaceInLootTableBuilder(LootTable.Builder builder, Item from, ToolAction toolAction) {
        ImmutableList.Builder<LootPool> lootPools = ObfuscationReflectionHelper.getPrivateValue(LootTable.Builder.class, builder, POOLS);
        boolean found = false;

        if (lootPools == null) {
            throw new IllegalStateException(LootTable.Builder.class.getName() + " is missing field " + POOLS);
        }

        for (LootPool lootPool : lootPools.build()) {
            if (findAndReplaceInLootPool(lootPool, from, toolAction)) {
                found = true;
            }
        }

        return found;
    }

    private boolean findAndReplaceInLootPool(LootPool lootPool, Item from, ToolAction toolAction) {
        List<LootPoolEntryContainer> lootEntries = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, lootPool, ENTRIES);
        List<LootItemCondition> lootConditions = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, lootPool, CONDITIONS);
        boolean found = false;

        if (lootEntries == null)
            throw new IllegalStateException(LootPool.class.getName() + " is missing field " + ENTRIES);

        for (LootPoolEntryContainer lootEntry : lootEntries) {
            if (findAndReplaceInLootEntry(lootEntry, from, toolAction))
                found = true;

            if (lootEntry instanceof CompositeEntryBase) {
                if (findAndReplaceInParentedLootEntry((CompositeEntryBase) lootEntry, from, toolAction))
                    found = true;
            }
        }

        if (lootConditions == null)
            throw new IllegalStateException(LootPool.class.getName() + " is missing field " + CONDITIONS);
        else {
            lootConditions = new ArrayList<>(lootConditions);
            ObfuscationReflectionHelper.setPrivateValue(LootPool.class,  lootPool, lootConditions, CONDITIONS);
        }

        for (int i = 0; i < lootConditions.size(); i++) {
            LootItemCondition lootCondition = lootConditions.get(i);
            if (lootCondition instanceof MatchTool matchTool && checkMatchTool(matchTool, from)) {
                lootConditions.set(i, CanToolPerformAction.canToolPerformAction(toolAction).build());
                found = true;
            } else if (lootCondition instanceof InvertedLootItemCondition inverted) {
                LootItemCondition invLootCondition = inverted.term();

                if (invLootCondition instanceof MatchTool matchTool && checkMatchTool(matchTool, from)) {
                    lootConditions.set(i, InvertedLootItemCondition.invert(CanToolPerformAction.canToolPerformAction(toolAction)).build());
                    found = true;
                } else if (invLootCondition instanceof CompositeLootItemCondition compositeLootItemCondition && findAndReplaceInComposite(compositeLootItemCondition, from, toolAction)) {
                    found = true;
                }
            }
        }



        return found;
    }

    private boolean findAndReplaceInParentedLootEntry(CompositeEntryBase entry, Item from, ToolAction toolAction) {
        List<LootPoolEntryContainer> lootEntries = ObfuscationReflectionHelper.getPrivateValue(CompositeEntryBase.class, entry, CHILDREN);
        boolean found = false;

        if (lootEntries == null) {
            throw new IllegalStateException(CompositeEntryBase.class.getName() + " is missing field " + CHILDREN);
        }

        for (LootPoolEntryContainer lootEntry : lootEntries) {
            if (findAndReplaceInLootEntry(lootEntry, from, toolAction)) {
                found = true;
            }
        }

        return found;
    }

    private boolean findAndReplaceInLootEntry(LootPoolEntryContainer entry, Item from, ToolAction toolAction) {
        List<LootItemCondition> lootConditions = ObfuscationReflectionHelper.getPrivateValue(LootPoolEntryContainer.class, entry, ENTRY_CONDITION);
        boolean found = false;

        if (lootConditions == null)
            throw new IllegalStateException(LootPoolEntryContainer.class.getName() + " is missing field f_7963" + "6_");
        else {
            lootConditions = new ArrayList<>(lootConditions);
            ObfuscationReflectionHelper.setPrivateValue(LootPoolEntryContainer.class, entry, lootConditions, ENTRY_CONDITION);
        }

        for (int i = 0; i < lootConditions.size(); i++) {
            var condition = lootConditions.get(i);
            if (condition instanceof CompositeLootItemCondition composite && findAndReplaceInComposite(composite, from, toolAction)) {
                found = true;
            } else if (condition instanceof MatchTool matchTool && checkMatchTool(matchTool, from)) {
                lootConditions.set(i, CanToolPerformAction.canToolPerformAction(toolAction).build());
                found = true;
            }
        }

        return found;
    }

    private boolean findAndReplaceInComposite(CompositeLootItemCondition alternative, Item from, ToolAction toolAction) {
        List<LootItemCondition> lootConditions = ObfuscationReflectionHelper.getPrivateValue(CompositeLootItemCondition.class, alternative, TERMS);
        boolean found = false;

        if (lootConditions == null)
            throw new IllegalStateException(CompositeLootItemCondition.class.getName() + " is missing field " + TERMS);
        else {
            lootConditions = new ArrayList<>(lootConditions);
            ObfuscationReflectionHelper.setPrivateValue(CompositeLootItemCondition.class, alternative, lootConditions, TERMS);
        }

        for (int i = 0; i < lootConditions.size(); i++) {
            if (lootConditions.get(i) instanceof MatchTool matchTool && checkMatchTool(matchTool, from)) {
                lootConditions.set(i, CanToolPerformAction.canToolPerformAction(toolAction).build());
                found = true;
            }
        }

        return found;
    }

    @SuppressWarnings("deprecation")
    private boolean checkMatchTool(MatchTool lootCondition, Item expected) {
        return lootCondition.predicate().flatMap(ItemPredicate::items).filter(s -> s.contains(expected.builtInRegistryHolder())).isPresent();
    }
}
