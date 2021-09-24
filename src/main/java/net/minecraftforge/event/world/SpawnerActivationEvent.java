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

package net.minecraftforge.event.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * This event is fired on every tick of a Spawner entity.<br>
 * <br>
 * This event has a {@link HasResult result}:
 * <li>{@link Result#ALLOW} Allows Spawner activation.</li>
 * <li>{@link Result#DEFAULT} Use vanilla Spawner activation rules.</li>
 * <li>{@link Result#DENY} Deny Spawner activation.</li><br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@HasResult
public class SpawnerActivationEvent extends Event
{
    private final Level level;
    private final BaseSpawner spawner;
    private final BlockPos pos;

    public SpawnerActivationEvent(Level level, BaseSpawner spawner, BlockPos pos)
    {
        this.level = level;
        this.spawner = spawner;
        this.pos = pos;
    }

    public Level getLevel()
    {
        return level;
    }

    public BaseSpawner getSpawner()
    {
        return spawner;
    }

    public BlockPos getPos()
    {
        return pos;
    }
}
