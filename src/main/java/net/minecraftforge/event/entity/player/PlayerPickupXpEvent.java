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

package net.minecraftforge.event.entity.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This event is called when a player collides with a EntityXPOrb on the ground.
 * The event can be canceled, and no further processing will be done.  
 */
@Cancelable
public class PlayerPickupXpEvent extends PlayerEvent
{
    private final EntityXPOrb orb;

    public PlayerPickupXpEvent(EntityPlayer player, EntityXPOrb orb)
    {
        super(player);
        this.orb = orb;
    }

    public EntityXPOrb getOrb()
    {
        return orb;
    }
}
