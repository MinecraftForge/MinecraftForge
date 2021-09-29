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

import java.util.Map;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * EntityAttributeCreationEvent.<br>
 * Use this event to register attributes for your own EntityTypes.
 * This event is fired after registration and before common setup.
 * <br>
 * Fired on the Mod bus {@link IModBusEvent}.<br>
 **/
public class EntityAttributeCreationEvent extends Event implements IModBusEvent
{
    private final Map<EntityType<? extends LivingEntity>, AttributeSupplier> map;

    public EntityAttributeCreationEvent(Map<EntityType<? extends LivingEntity>, AttributeSupplier> map)
    {
        this.map = map;
    }

    public void put(EntityType<? extends LivingEntity> entity, AttributeSupplier map)
    {
        if (DefaultAttributes.hasSupplier(entity))
            throw new IllegalStateException("Duplicate DefaultAttributes entry: " + entity);
        this.map.put(entity, map);
    }
}
