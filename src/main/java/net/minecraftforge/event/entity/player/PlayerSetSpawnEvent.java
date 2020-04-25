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

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

@net.minecraftforge.eventbus.api.Cancelable
public class PlayerSetSpawnEvent extends PlayerEvent
{
    private final boolean forced;
    private final BlockPos newSpawn;
    
    public PlayerSetSpawnEvent(PlayerEntity player, BlockPos newSpawn, boolean forced) {
        super(player);
        this.newSpawn = newSpawn;
        this.forced = forced;
    }

    /**
     * This event is called before a player's spawn point is changed.
     * The event can be canceled, and no further processing will be done.
     */
    public boolean isForced()
    {
        return forced;
    }

    public BlockPos getNewSpawn()
    {
        return newSpawn;
    }
}
