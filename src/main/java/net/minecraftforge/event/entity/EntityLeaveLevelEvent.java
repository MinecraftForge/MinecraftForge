/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired when an Entity leaves the level. <br>
 * This event is fired whenever an Entity is removed from the level in {@link LevelCallback#onTrackingEnd(Object)}. <br>
 * <br>
 * {@link #getLevel()} contains the level from which the entity is removed. <br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}
 */
public class EntityLeaveLevelEvent extends EntityEvent
{

    private final Level level;

    public EntityLeaveLevelEvent(Entity entity, Level level)
    {
        super(entity);
        this.level = level;
    }

    public Level getLevel()
    {
        return level;
    }
}
