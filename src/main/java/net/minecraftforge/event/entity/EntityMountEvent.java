/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * This event gets fired whenever a entity mounts/dismounts another entity.<br>
 * <b>entityBeingMounted can be null</b>, be sure to check for that.
 * <br>
 * <br>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * If this event is canceled, the entity does not mount/dismount the other entity.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 *<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 * 
 */

@net.minecraftforge.eventbus.api.Cancelable
public class EntityMountEvent extends EntityEvent
{
    
    private final Entity entityMounting;
    private final Entity entityBeingMounted;
    private final World worldObj;
    
    private final boolean isMounting;

    public EntityMountEvent(Entity entityMounting, Entity entityBeingMounted, World entityWorld, boolean isMounting)
    {
        super(entityMounting);
        this.entityMounting = entityMounting;
        this.entityBeingMounted = entityBeingMounted;
        this.worldObj = entityWorld;
        this.isMounting = isMounting;
    }
    
    public boolean isMounting()
    {
        return isMounting;
    }
    
    public boolean isDismounting()
    {
        return !isMounting;
    }

    public Entity getEntityMounting()
    {
        return entityMounting;
    }

    public Entity getEntityBeingMounted()
    {
        return entityBeingMounted;
    }

    public World getWorldObj()
    {
        return worldObj;
    }
}
