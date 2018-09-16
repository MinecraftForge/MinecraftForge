/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.event.entity.minecart;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * MinecartCollisionEvent is fired when a minecart collides with an Entity.
 * This event is fired whenever a minecraft collides in
 * {@link EntityMinecart#applyEntityCollision(Entity)}.
 * 
 * {@link #collider} contains the Entity the Minecart collided with.
 * 
 * This event is not {@link Cancelable}.
 * 
 * This event does not have a result. {@link HasResult}
 * 
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class MinecartCollisionEvent extends MinecartEvent
{
    private final Entity collider;

    public MinecartCollisionEvent(EntityMinecart minecart, Entity collider)
    {
        super(minecart);
        this.collider = collider;
    }

    public Entity getCollider()
    {
        return collider;
    }
}
