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

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * MinecartUpdateEvent is fired when a minecart is updated.<br>
 * This event is fired whenever a minecart is updated in
 * {@link EntityMinecart#onUpdate()}.<br>
 * <br>
 * {@link #pos} contains the coordinate of the track the entity is on {if applicable}.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class MinecartUpdateEvent extends MinecartEvent
{
    private final BlockPos pos;

    public MinecartUpdateEvent(EntityMinecart minecart, BlockPos pos)
    {
        super(minecart);
        this.pos = pos;
    }

    public BlockPos getPos()
    {
        return pos;
    }
}
