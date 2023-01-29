/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.pickup;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Set;

/**
 * FunctionalInterface used to query the Level to determine if the specified ItemEntity is valid to be picked up or not.
 *
 * <p>Any query which returns false will disallow the item from being picked up.
 * <p>All queries must return true, in order for the item to be picked up by the collector.
 */
@FunctionalInterface
public interface ItemPickupPredicate
{
    /**
     * Query if the specified ItemEntity is valid to be picked up or not.
     *
     * @param item The ItemEntity being picked up
     * @param stack The ItemStack being picked up
     * @param level Level containing the ItemEntity
     * @param pos Position of the ItemEntity
     * @param collector Object trying to pick up the ItemEntity or null
     * @return if the specified ItemEntity is valid to be picked up
     */
    boolean query(@NotNull ItemEntity item, @NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos, @NotNull Object collector, @NotNull Set<ItemPickupReason> pickupReasons);
}
