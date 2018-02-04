/*
 * Minecraft Forge
 * Copyright (c) 2018.
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
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * EntityPushedByWaterEvent is fired when an Entity is about to be moved by water current.<br>
 * <br>
 * This event has a result. {@link HasResult}<br>
 * If the result of this method is not Result.DEFAULT, it will be used in place of
 * entity.isPushedByWater().
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@HasResult
public class EntityPushedByWaterEvent extends EntityEvent
{
    public EntityPushedByWaterEvent(Entity entity)
    {
        super(entity);
    }
}
