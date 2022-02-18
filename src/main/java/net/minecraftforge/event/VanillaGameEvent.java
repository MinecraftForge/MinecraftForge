/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.event;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * VanillaGameEvent is fired on the server whenever one of Vanilla's {@link GameEvent GameEvents} fire. <br>
 * <br>
 * This allows for listening to Vanilla's events in a more structured and global way that is not tied to needing a block entity listener. <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
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
