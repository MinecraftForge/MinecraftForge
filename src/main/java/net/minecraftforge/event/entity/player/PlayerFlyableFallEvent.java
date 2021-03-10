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

package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;

/**
 * Occurs when a player falls, but is able to fly.  Doesn't need to be cancelable, this is mainly for notification purposes.
 * @author Mithion
 *
 */
public class PlayerFlyableFallEvent extends PlayerEvent
{
    private float distance;
    private float multiplier;

    public PlayerFlyableFallEvent(PlayerEntity player, float distance, float multiplier)
    {
        super(player);
        this.distance = distance;
        this.multiplier = multiplier;
    }

    public float getDistance() { return distance;}
    public void setDistance(float distance) { this.distance = distance; }
    public float getMultiplier() { return multiplier; }
    public void setMultiplier(float multiplier) { this.multiplier = multiplier; }
}
