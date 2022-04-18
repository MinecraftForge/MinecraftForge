/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * VanillaGameEvent is fired on the server whenever one of Vanilla's {@link GameEvent GameEvents} fire. <br>
 * <br>
 * This allows for listening to Vanilla's events in a more structured and global way that is not tied to needing a block entity listener. <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}. <br>
 * <br>
 * Cancel this event to prevent Vanilla from posting the {@link GameEvent} to all nearby {@link net.minecraft.world.level.gameevent.GameEventListener GameEventListeners}.
 **/
@Cancelable
public class VanillaGameEvent extends Event
{
    private final Level level;
    @Nullable
    private final Entity cause;
    private final GameEvent vanillaEvent;
    private final BlockPos position;

    public VanillaGameEvent(Level level, @Nullable Entity cause, GameEvent vanillaEvent, BlockPos position)
    {
        this.level = level;
        this.cause = cause;
        this.vanillaEvent = vanillaEvent;
        this.position = position;
    }

    /**
     * @return The level the Vanilla {@link GameEvent} occurred.
     */
    public Level getLevel()
    {
        return level;
    }

    /**
     * @return The entity that was the source or "cause" of the {@link GameEvent}.
     */
    @Nullable
    public Entity getCause()
    {
        return cause;
    }

    /**
     * @return The Vanilla event.
     */
    public GameEvent getVanillaEvent()
    {
        return vanillaEvent;
    }

    /**
     * @return The position the event took place at. This may be a block or the block position of the entity targeted.
     */
    public BlockPos getEventPosition()
    {
        return position;
    }
}
