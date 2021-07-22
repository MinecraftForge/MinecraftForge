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

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;

/**
 * This event is fired when a player's spawn point is set or reset.<br>
 * The event can be canceled, which will prevent the spawn point from being changed.
 */
@Cancelable
public class PlayerSetSpawnEvent extends PlayerEvent
{
    private final ResourceKey<Level> spawnWorld;
    private final boolean forced;
    @Nullable
    private final BlockPos newSpawn;
    
    public PlayerSetSpawnEvent(Player player, ResourceKey<Level> spawnWorld, @Nullable BlockPos newSpawn, boolean forced)
    {
        super(player);
        this.spawnWorld = spawnWorld;
        this.newSpawn = newSpawn;
        this.forced = forced;
    }

    public boolean isForced()
    {
        return forced;
    }

    /**
     * The new spawn position, or null if the spawn position is being reset.
     */
    @Nullable
    public BlockPos getNewSpawn()
    {
        return newSpawn;
    }

    public ResourceKey<Level> getSpawnWorld()
    {
        return spawnWorld;
    }
}
