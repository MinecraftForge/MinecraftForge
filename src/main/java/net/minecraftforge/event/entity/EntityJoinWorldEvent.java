/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.Collection;

/**
 * EntityJoinWorldEvent is fired when an Entity joins the world. <br>
 * This event is fired whenever an Entity is added to the world in 
 * {@link World#loadEntities(Collection)}, {@link net.minecraft.world.ServerWorld#loadEntities(Collection)} {@link World#joinEntityInSurroundings(Entity)}, and {@link World#spawnEntity(Entity)}. <br>
 * <strong>Note:</strong> This event may be called before the underlying {@link net.minecraft.world.chunk.Chunk} is promoted to {@link net.minecraft.world.chunk.ChunkStatus#FULL}. You will cause chunk loading deadlocks if you don't delay your world interactions.<br>
 * <br>
 * {@link #world} contains the world in which the entity is to join.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity is not added to the world.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@net.minecraftforge.eventbus.api.Cancelable
public class EntityJoinWorldEvent extends EntityEvent
{

    private final World world;

    public EntityJoinWorldEvent(Entity entity, World world)
    {
        super(entity);
        this.world = world;
    }

    public World getWorld()
    {
        return world;
    }
}
