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
 * This event is fired whenever an {@link Entity} leaves a {@link Level}.
 * This event is fired whenever an entity is removed from the level in {@link LevelCallback#onTrackingEnd(Object)}.
 * <p>
 * This event is not {@linkplain Cancelable cancellable} and does not {@linkplain net.minecraftforge.eventbus.api.Event.HasResult have a result}.
 * <p>
 * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
 * on both logical sides.
 **/
public class EntityLeaveLevelEvent extends EntityEvent
{
    private final Level level;

    public EntityLeaveLevelEvent(Entity entity, Level level)
    {
        super(entity);
        this.level = level;
    }

    /**
     * {@return the level the entity is set to leave}
     */
    public Level getLevel()
    {
        return level;
    }
}
