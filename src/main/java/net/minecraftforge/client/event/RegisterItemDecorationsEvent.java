/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.IItemDecorator;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Allows users to register custom {@linkplain IItemDecorator IItemDecorator} to Items.
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public final class RegisterItemDecorationsEvent implements SelfDestructing, IModBusEvent {
    public static EventBus<RegisterItemDecorationsEvent> getBus(BusGroup modBusGroup) {
        return IModBusEvent.getBus(modBusGroup, RegisterItemDecorationsEvent.class);
    }

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
