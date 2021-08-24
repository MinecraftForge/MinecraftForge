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

import net.minecraft.server.level.ServerPlayer;

/**
 * This event will fire on the server when the player's permission level changes, but will also fire at other times when the game syncs the permission level to the client.
 */
public class SendPlayerPermissionLevelEvent extends PlayerEvent
{
    private final int level;

    public SendPlayerPermissionLevelEvent(ServerPlayer player, int level)
    {
        super(player);
        this.level = level;
    }

    /**
     * @return The permission level.
     */
    public int getLevel()
    {
        return level;
    }
}
