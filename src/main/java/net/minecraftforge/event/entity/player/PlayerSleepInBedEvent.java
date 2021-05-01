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
import net.minecraft.entity.player.PlayerEntity.SleepResult;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;

/**
 * PlayerSleepInBedEvent is fired when a player sleeps in a bed.
 * <br>
 * This event is fired whenever a player sleeps in a bed in
 * {@link EntityPlayer#trySleep(BlockPos)}.<br>
 * <br>
 * {@link #result} contains whether the player is able to sleep. <br>
 * <br>
 * This event does not have a result. {@link HasResult}
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class PlayerSleepInBedEvent extends PlayerEvent
{
    private SleepResult result = null;
    private final Optional<BlockPos> pos;

    public PlayerSleepInBedEvent(PlayerEntity player, Optional<BlockPos> pos)
    {
        super(player);
        this.pos = pos;
    }

    public SleepResult getResultStatus()
    {
        return result;
    }

    public void setResult(SleepResult result)
    {
        this.result = result;
    }

    public BlockPos getPos()
    {
        return pos.orElse(null);
    }

    public Optional<BlockPos> getOptionalPos() {
        return pos;
    }

}
