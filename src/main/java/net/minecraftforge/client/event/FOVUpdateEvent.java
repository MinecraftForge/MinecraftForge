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

package net.minecraftforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraftforge.eventbus.api.Event;

public class FOVUpdateEvent extends Event
{
    private final Player entity;
    private final float fov;
    private float newfov;

    public FOVUpdateEvent(Player entity, float fov)
    {
        this.entity = entity;
        this.fov = fov;
        this.setNewfov(Mth.lerp(Minecraft.getInstance().options.fovEffectScale, 1.0F, fov));
    }

    public Player getEntity()
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
