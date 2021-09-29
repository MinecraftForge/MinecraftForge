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
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * EntityTravelToDimensionEvent is fired before an Entity travels to a dimension.<br>
 * <br>
 * {@link #dimension} contains the id of the dimension the entity is traveling to.<br>
 * <br>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * If this event is canceled, the Entity does not travel to the dimension.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@Cancelable
public class EntityTravelToDimensionEvent extends EntityEvent
{
    private final ResourceKey<Level> dimension;

    public EntityTravelToDimensionEvent(Entity entity, ResourceKey<Level> dimension)
    {
        super(entity);
        this.dimension = dimension;
    }

    public ResourceKey<Level> getDimension()
    {
        return dimension;
    }
}
