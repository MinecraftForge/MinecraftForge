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

package net.minecraftforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * EntityJoinWorldEvent is fired when an Entity joins the world. <br>
 * This event is fired whenever an Entity is added to the world in 
 * {@code Level#loadEntities(Collection)},
 * {@code net.minecraft.world.ServerWorld#loadEntities(Collection)}
 * {@code World#joinEntityInSurroundings(Entity)}, and
 * {@code World#spawnEntity(Entity)}. <br>
 * <strong>Note:</strong> This event may be called before the underlying {@link LevelChunk} is promoted to {@link ChunkStatus#FULL}.
 * You will cause chunk loading deadlocks if you don't delay your world interactions.<br>
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

    private final Level world;
    private final boolean loadedFromDisk;

    public EntityJoinWorldEvent(Entity entity, Level world)
    {
        this(entity, world, false);
    }

    public EntityJoinWorldEvent(Entity entity, Level world, boolean loadedFromDisk)
    {
        super(entity);
        this.world = world;
        this.loadedFromDisk = loadedFromDisk;
    }

    public Level getWorld()
    {
        return world;
    }

    /**
     * @return {@code true} if the entity was loaded from disk. On client entities, the info isn't available and this will always return {@code false}.
     */
    public boolean loadedFromDisk()
    {
        return loadedFromDisk;
    }
}
