/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;

/**
 * PlayerSleepInBedEvent is fired when a player sleeps in a bed.
 * <br>
 * This event is fired whenever a player sleeps in a bed in
 * {@link Player#startSleeping(BlockPos)}.<br>
 * <br>
 * {@link #result} contains whether the player is able to sleep. <br>
 * <br>
 * This event does not have a result. {@link HasResult}
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class PlayerSleepInBedEvent extends PlayerEvent
{
    private BedSleepingProblem result = null;
    private final Optional<BlockPos> pos;

    public PlayerSleepInBedEvent(Player player, Optional<BlockPos> pos)
    {
        super(player);
        this.pos = pos;
    }

    public BedSleepingProblem getResultStatus()
    {
        return result;
    }

    public void setResult(BedSleepingProblem result)
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
