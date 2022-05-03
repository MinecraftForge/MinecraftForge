/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import java.util.Map;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

/**
 * EntityAttributeCreationEvent.<br>
 * Use this event to register attributes for your own EntityTypes.
 * This event is fired after registration and before common setup.
 * <br>
 * Fired on the Mod bus {@link IModBusEvent}.<br>
 **/
public class EntityAttributeCreationEvent extends Event implements IModBusEvent
{
    private final Map<EntityType<? extends LivingEntity>, AttributeModifierMap> map;

    public EntityAttributeCreationEvent(Map<EntityType<? extends LivingEntity>, AttributeModifierMap> map)
    {
        this.map = map;
    }

    public void put(EntityType<? extends LivingEntity> entity, AttributeModifierMap map)
    {
        if (GlobalEntityTypeAttributes.hasSupplier(entity))
            throw new IllegalStateException("Duplicate GlobalEntityTypeAttributes entry: " + entity);
        this.map.put(entity, map);
    }
}
