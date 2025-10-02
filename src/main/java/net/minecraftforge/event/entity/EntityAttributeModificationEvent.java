/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

/**
 * EntityAttributeModificationEvent.<br>
 * Use this event to add attributes to existing entity types.
 * This event is fired after registration and before common setup, and after {@link EntityAttributeCreationEvent}
 **/
public final class EntityAttributeModificationEvent extends MutableEvent {
    public static final EventBus<EntityAttributeModificationEvent> BUS = EventBus.create(EntityAttributeModificationEvent.class);

    @Deprecated(forRemoval = true, since = "1.21.9")
    public static EventBus<EntityAttributeModificationEvent> getBus(BusGroup modBusGroup) {
        return BUS;
    }

    private final Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> entityAttributes;
    private final List<EntityType<? extends LivingEntity>> entityTypes;

    @SuppressWarnings("unchecked")
    public EntityAttributeModificationEvent(Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> mapIn) {
        this.entityAttributes = mapIn;
        this.entityTypes = List.copyOf(
            ForgeRegistries.ENTITY_TYPES.getValues().stream()
                .filter(DefaultAttributes::hasSupplier)
                .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
                .toList()
        );
    }

    public void add(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute, double value) {
        var attributes = entityAttributes.computeIfAbsent(entityType, (type) -> new AttributeSupplier.Builder());
        attributes.add(attribute, value);
    }

    public void add(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute) {
        add(entityType, attribute, attribute.get().getDefaultValue());
    }

    public boolean has(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute) {
        AttributeSupplier globalMap = DefaultAttributes.getSupplier(entityType);
        return globalMap.hasAttribute(attribute) || (entityAttributes.get(entityType) != null && entityAttributes.get(entityType).hasAttribute(attribute));
    }

    public List<EntityType<? extends LivingEntity>> getTypes() {
        return entityTypes;
    }
}
