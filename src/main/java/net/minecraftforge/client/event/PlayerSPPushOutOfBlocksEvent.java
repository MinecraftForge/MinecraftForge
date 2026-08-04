/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nonnull;

/**
 * This event is called before the pushOutOfBlocks calls in EntityPlayerSP.
 *
 * Cancelling the event will prevent pushOutOfBlocks from being called.
 */
@net.minecraftforge.eventbus.api.Cancelable
public class PlayerSPPushOutOfBlocksEvent extends PlayerEvent
{
    private AxisAlignedBB entityBoundingBox;

    public PlayerSPPushOutOfBlocksEvent(PlayerEntity player, AxisAlignedBB entityBoundingBox)
    {
        super(player);
        this.entityBoundingBox = entityBoundingBox;
    }

    public AxisAlignedBB getEntityBoundingBox() { return entityBoundingBox; }
    public void setEntityBoundingBox(@Nonnull AxisAlignedBB entityBoundingBox) { this.entityBoundingBox = entityBoundingBox; }
}
