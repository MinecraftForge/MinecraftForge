/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * EntityMobGriefingEvent is fired when mob griefing is about to occur and allows an event listener to specify whether it should or not.<br>
 * This event is fired when ever the {@code mobGriefing} game rule is checked.<br>
 * <br>
 * This event has a {@link HasResult result}:
 * <li>{@link Result#ALLOW} means this instance of mob griefing is allowed.</li>
 * <li>{@link Result#DEFAULT} means the {@code mobGriefing} game rule is used to determine the behaviour.</li>
 * <li>{@link Result#DENY} means this instance of mob griefing is not allowed.</li><br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@HasResult
public class EntityMobGriefingEvent extends EntityEvent
{
    public EntityMobGriefingEvent(Entity entity)
    {
        super(entity);
    }
}
