/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * This LootItemCondition "forge:can_tool_perform_action" can be used to check if a tool can perform a given ToolAction.
 */
public record CanToolPerformAction(ToolAction action) implements LootItemCondition {
    public static final MapCodec<CanToolPerformAction> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
        ToolAction.CODEC.fieldOf("action").forGetter(CanToolPerformAction::action)
    ).apply(b, CanToolPerformAction::new));
    public static final LootItemConditionType TYPE = new LootItemConditionType(CODEC);

    @Override
    public @NotNull LootItemConditionType getType() {
        return TYPE;
    }

    @Override
    public @NotNull Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    @Override
    public boolean test(LootContext ctx) {
        ItemStack itemstack = ctx.getParamOrNull(LootContextParams.TOOL);
        return itemstack != null && itemstack.canPerformAction(this.action);
    }

    public static LootItemCondition.Builder canToolPerformAction(ToolAction action) {
        return () -> new CanToolPerformAction(action);
    }
}