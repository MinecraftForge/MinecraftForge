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

package net.minecraftforge.client.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 6:07 PM, 9/5/13
 */
public class FOVUpdateEvent extends Event
{
    private final PlayerEntity entity;
    private final float fov;
    private float newfov;

    public FOVUpdateEvent(PlayerEntity entity, float fov)
    {
        this.entity = entity;
        this.fov = fov;
        this.setNewfov(fov);
    }

    public PlayerEntity getEntity()
    {
        return entity;
    }

    public float getFov()
    {
        return fov;
    }

    public float getNewfov()
    {
        return newfov;
    }

    public void setNewfov(float newfov)
    {
        this.newfov = newfov;
    }
}
