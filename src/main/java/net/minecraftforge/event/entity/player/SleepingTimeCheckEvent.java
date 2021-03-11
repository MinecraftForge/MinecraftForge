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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event.HasResult;

import java.util.Optional;

/**
 * This event is fired when the game checks if players can sleep at this time.<br>
 * Failing this check will cause sleeping players to wake up and prevent awake players from sleeping.<br>
 *
 * This event has a result. {@link HasResult}<br>
 *
 * setResult(ALLOW) informs game that player can sleep at this time.<br>
 * setResult(DEFAULT) causes game to check !{@link World#isDaytime()} instead.
 */
@HasResult
public class SleepingTimeCheckEvent extends PlayerEvent
{
    private final Optional<BlockPos> sleepingLocation;

    public SleepingTimeCheckEvent(PlayerEntity player, Optional<BlockPos> sleepingLocation)
    {
        super(player);
        this.sleepingLocation = sleepingLocation;
    }

    /**
     * Note that the sleeping location may be an approximated one.
     * @return The player's sleeping location.
     */
    public Optional<BlockPos> getSleepingLocation()
    {
        return sleepingLocation;
    }
}
