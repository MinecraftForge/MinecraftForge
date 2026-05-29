/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftsingularity.client.IItemDecorator;

import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Allows users to register custom {@linkplain IItemDecorator IItemDecorator} to Items.
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
@NullMarked
public final class RegisterItemDecorationsEvent extends MutableEvent implements SelfDestructing {
    public static final EventBus<RegisterItemDecorationsEvent> BUS = EventBus.create(RegisterItemDecorationsEvent.class);

    private final Map<Item, List<IItemDecorator>> decorators;

    @ApiStatus.Internal
    public RegisterItemDecorationsEvent(Map<Item, List<IItemDecorator>> decorators) {
        this.decorators = decorators;
    }

    /**
     * Register an ItemDecorator to an Item
     */
    public void register(ItemLike itemLike, IItemDecorator decorator) {
        List<IItemDecorator> itemDecoratorList = decorators.computeIfAbsent(itemLike.asItem(), item -> new ArrayList<>());
        itemDecoratorList.add(decorator);
    }
}
