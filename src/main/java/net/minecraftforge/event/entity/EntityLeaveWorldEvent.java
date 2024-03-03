/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * EntityLeaveWorldEvent is fired when an Entity leaves the world. <br>
 * This event is fired whenever an Entity is removed from the world in
 * {@link ClientLevel#removeEntity(int, Entity.RemovalReason)}, {@link ServerLevel#removeEntityComplete(Entity, boolean)}. <br>
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

    private final Level world;

    public EntityLeaveWorldEvent(Entity entity, Level world)
    {
        super(entity);
        this.world = world;
    }

    public Level getWorld()
    {
        return world;
    }
}
