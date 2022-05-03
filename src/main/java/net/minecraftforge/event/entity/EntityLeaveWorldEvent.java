/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * EntityLeaveWorldEvent is fired when an Entity leaves the world. <br>
 * This event is fired whenever an Entity is removed from the world in
 * {@link ClientWorld#removeEntity(Entity)}, {@link ServerWorld#removeEntityComplete(Entity,Boolean)}. <br>
 * <br>
 * {@link #world} contains the world from which the entity is removed. <br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}
 */
public class EntityLeaveWorldEvent extends EntityEvent
{

    private final World world;

    public EntityLeaveWorldEvent(Entity entity, World world)
    {
        super(entity);
        this.world = world;
    }

    public World getWorld()
    {
        return world;
    }
}
