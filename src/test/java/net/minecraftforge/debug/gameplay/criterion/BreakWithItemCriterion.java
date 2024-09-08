/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BreakWithItemCriterion extends SimpleCriterionTrigger<BreakWithItemCriterion.Instance> {
    public Criterion<Instance> instance(BlockPredicate block, ItemPredicate item, boolean offHand) {
        return this.createCriterion(new Instance(block, item, offHand));
    }

    private static final Codec<Instance> CODEC = RecordCodecBuilder.create(it ->
        it.group(
            BlockPredicate.CODEC.fieldOf("breakingBlock").forGetter(i -> i.breakingBlock),
            ItemPredicate.CODEC.fieldOf("holdingItem").forGetter(i -> i.holdingItem),
            Codec.BOOL.optionalFieldOf("allowOffhand", false).forGetter(i -> i.allowOffHand)
        ).apply(it, Instance::new)
    );

    @Override
    public Codec<Instance> codec() {
        return CODEC;
    }

    public void trigger(ServerPlayer player, BlockPos block) {
        this.trigger(player, it ->
            it.breakingBlock.matches((ServerLevel) player.level(), block) && (
                it.holdingItem.test(player.getItemInHand(InteractionHand.MAIN_HAND)) ||
                    it.allowOffHand && it.holdingItem.test(player.getItemInHand(InteractionHand.OFF_HAND))
            ));
    }

    public static record Instance(BlockPredicate breakingBlock, ItemPredicate holdingItem, boolean allowOffHand) implements SimpleCriterionTrigger.SimpleInstance {
        @Override
        public Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }
    }
}
