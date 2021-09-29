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
