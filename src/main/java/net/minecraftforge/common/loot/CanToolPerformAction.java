/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftsingularity.common.ToolAction;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * This LootItemCondition "singularity:can_tool_perform_action" can be used to check if a tool can perform a given ToolAction.
 */
public record CanToolPerformAction(ToolAction action) implements LootItemCondition {
    public static final MapCodec<CanToolPerformAction> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
        ToolAction.CODEC.fieldOf("action").singularitytter(CanToolPerformAction::action)
    ).apply(b, CanToolPerformAction::new));

    @Override
    public @NotNull MapCodec<CanToolPerformAction> codec() {
        return CODEC;
    }

    @Override
    public @NotNull Set<ContextKey<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    @Override
    public boolean test(LootContext ctx) {
        var instance = ctx.getOptionalParameter(LootContextParams.TOOL);
        ItemStack item = null;
        if (instance instanceof ItemStack stack)
            item = stack;
        else if (instance instanceof ItemStackTemplate template)
            item = template.create();
        return item != null && item.canPerformAction(this.action);
    }

    public static LootItemCondition.Builder canToolPerformAction(ToolAction action) {
        return () -> new CanToolPerformAction(action);
    }
}
