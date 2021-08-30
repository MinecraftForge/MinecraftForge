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
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event will fire when the player is opped or deopped.
 * <p>
 * This event is cancelable which will stop the op or deop from happening.
 */
@Cancelable
public class PermissionsChangedEvent extends PlayerEvent
{
    private final int newLevel;
    private final int oldLevel;

    public PermissionsChangedEvent(ServerPlayer player, int newLevel, int oldLevel)
    {
        super(player);
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    /**
     * @return The new permission level.
     */
    public int getNewLevel()
    {
        return newLevel;
    }
    /**
     * @return The old permission level.
     */
    public int getOldLevel()
    {
        return oldLevel;
    }
}
