/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * EntityTravelToDimensionEvent is fired before an Entity travels to a dimension.<br>
 * <br>
 * {@link #dimension} contains the id of the dimension the entity is traveling to.<br>
 * <br>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * If this event is canceled, the Entity does not travel to the dimension.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@Cancelable
public class EntityTravelToDimensionEvent extends EntityEvent
{
    private final RegistryKey<World> dimension;

    public EntityTravelToDimensionEvent(Entity entity, RegistryKey<World> dimension)
    {
        super(entity);
        this.dimension = dimension;
    }

    public RegistryKey<World> getDimension()
    {
        return dimension;
    }
}
