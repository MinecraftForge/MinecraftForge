/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired when an Entity joins the level. <br>
 * This event is fired whenever an Entity is added to the level in {@link Level#addFreshEntity(Entity)}
 * or {@code PersistentEntitySectionManager#addNewEntity(EntityAccess, boolean)}.
 * <br>
 * <strong>Note:</strong> This event may be called before the underlying {@link LevelChunk} is promoted to {@link ChunkStatus#FULL}.
 * You will cause chunk loading deadlocks if you don't delay your world interactions.<br>
 * <br>
 * {@link #getLevel()} contains the level in which the entity is to join.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity is not added to the world.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@net.minecraftforge.eventbus.api.Cancelable
public class EntityJoinLevelEvent extends EntityEvent
{

    private final Level level;
    private final boolean loadedFromDisk;

    public EntityJoinLevelEvent(Entity entity, Level level)
    {
        this(entity, level, false);
    }

    public EntityJoinLevelEvent(Entity entity, Level level, boolean loadedFromDisk)
    {
        super(entity);
        this.level = level;
        this.loadedFromDisk = loadedFromDisk;
    }

    public Level getLevel()
    {
        return level;
    }

    /**
     * @return {@code true} if the entity was loaded from disk. On client entities, the info isn't available and this will always return {@code false}.
     */
    public boolean loadedFromDisk()
    {
        return loadedFromDisk;
    }
}
