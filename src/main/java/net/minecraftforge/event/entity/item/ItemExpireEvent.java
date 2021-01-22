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

package net.minecraftforge.event.entity.item;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.entity.item.ItemEntity;

/**
 * Event that is fired when an EntityItem's age has reached its maximum
 * lifespan. Canceling this event will prevent the EntityItem from being
 * flagged as dead, thus staying it's removal from the world. If canceled
 * it will add more time to the entities life equal to extraLife.
 */
@Cancelable
public class ItemExpireEvent extends ItemEvent
{

    private int extraLife;

    /**
     * Creates a new event for an expiring EntityItem.
     * 
     * @param entityItem The EntityItem being deleted.
     * @param extraLife The amount of time to be added to this entities lifespan if the event is canceled.
     */
    public ItemExpireEvent(ItemEntity entityItem, int extraLife)
    {
        super(entityItem);
        this.setExtraLife(extraLife);
    }

    public int getExtraLife()
    {
        return extraLife;
    }

    public void setExtraLife(int extraLife)
    {
        this.extraLife = extraLife;
    }
}
