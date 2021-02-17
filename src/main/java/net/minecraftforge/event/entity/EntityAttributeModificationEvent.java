/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * EntityAttributeModificationEvent.<br>
 * Use this event to add attributes to existing entity types.
 * This event is fired after registration and before common setup, and after {@link EntityAttributeCreationEvent}
 * <br>
 * Fired on the Mod bus {@link IModBusEvent}.<br>
 **/
public class EntityAttributeModificationEvent extends Event implements IModBusEvent
{
    private final Map<EntityType<? extends LivingEntity>, AttributeModifierMap.MutableAttribute> entityAttributes;
    private final List<EntityType<? extends LivingEntity>> entityTypes;

    @SuppressWarnings("unchecked")
    public EntityAttributeModificationEvent(Map<EntityType<? extends LivingEntity>, AttributeModifierMap.MutableAttribute> mapIn)
    {
        this.entityAttributes = mapIn;
        this.entityTypes = ImmutableList.copyOf(
            ForgeRegistries.ENTITIES.getValues().stream()
                .filter(GlobalEntityTypeAttributes::doesEntityHaveAttributes)
                .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
                .collect(Collectors.toList())
        );
    }

    public void add(EntityType<? extends LivingEntity> entityType, Attribute attribute, double value)
    {
        AttributeModifierMap.MutableAttribute attributes = entityAttributes.computeIfAbsent(entityType,
                (type) -> new AttributeModifierMap.MutableAttribute());
        attributes.createMutableAttribute(attribute, value);
    }

    public void add(EntityType<? extends LivingEntity> entityType, Attribute attribute)
    {
        add(entityType, attribute, attribute.getDefaultValue());
    }

    public boolean has(EntityType<? extends LivingEntity> entityType, Attribute attribute)
    {
        AttributeModifierMap globalMap = GlobalEntityTypeAttributes.getAttributesForEntity(entityType);
        return globalMap.hasAttribute(attribute) || (entityAttributes.get(entityType) != null && entityAttributes.get(entityType).hasAttribute(attribute));
    }

    public List<EntityType<? extends LivingEntity>> getTypes()
    {
        return entityTypes;
    }

}
