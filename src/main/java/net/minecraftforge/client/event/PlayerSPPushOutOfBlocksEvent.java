/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is called before the pushOutOfBlocks calls in EntityPlayerSP.
 *
 * Cancelling the event will prevent pushOutOfBlocks from being called.
 */
@Cancelable
public class PlayerSPPushOutOfBlocksEvent extends PlayerEvent
{
    private double minY;

    public PlayerSPPushOutOfBlocksEvent(PlayerEntity player)
    {
        super(player);
        this.minY = player.getPosY() + 0.5D;
    }

    public void setMinY(double value) {
        this.minY = value;
    }

    public double getMinY() {
        return this.minY;
    }
}
