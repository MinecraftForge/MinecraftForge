/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
