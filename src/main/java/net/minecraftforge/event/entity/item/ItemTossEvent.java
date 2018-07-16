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

package net.minecraftforge.event.entity.item;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Event that is fired whenever a player tosses (Q) an item or drag-n-drops a
 * stack of items outside the inventory GUI screens. Canceling the event will
 * stop the items from entering the world, but will not prevent them being
 * removed from the inventory - and thus removed from the system.
 */
@Cancelable
public class ItemTossEvent extends ItemEvent
{

    private final EntityPlayer player;

    /**
     * Creates a new event for EntityItems tossed by a player.
     * 
     * @param entityItem The EntityItem being tossed.
     * @param player The player tossing the item.
     */
    public ItemTossEvent(EntityItem entityItem, EntityPlayer player)
    {
        super(entityItem);
        this.player = player;
    }

    /**
     * The player tossing the item.
     */
    public EntityPlayer getPlayer()
    {
        return player;
    }
}