/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import java.util.Map;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;

/**
 * EntityAttributeCreationEvent.<br>
 * Use this event to register attributes for your own EntityTypes.
 * This event is fired after registration and before common setup.
 **/
public final class EntityAttributeCreationEvent extends MutableEvent {
    public static final EventBus<EntityAttributeCreationEvent> BUS = EventBus.create(EntityAttributeCreationEvent.class);

    @Deprecated(forRemoval = true, since = "1.21.9")
    public static EventBus<EntityAttributeCreationEvent> getBus(BusGroup modBusGroup) {
        return BUS;
    }

    private final Map<EntityType<? extends LivingEntity>, AttributeSupplier> map;

    public EntityAttributeCreationEvent(Map<EntityType<? extends LivingEntity>, AttributeSupplier> map) {
        this.map = map;
    }

    public void put(EntityType<? extends LivingEntity> entity, AttributeSupplier map) {
        if (DefaultAttributes.hasSupplier(entity))
            throw new IllegalStateException("Duplicate DefaultAttributes entry: " + entity);
        this.map.put(entity, map);
    }
}
