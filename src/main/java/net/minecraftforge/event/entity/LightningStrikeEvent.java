/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Event that fires when a lightning strikes.
 * Cancel to prevent vanilla behavior.
 */
@Cancelable
public class LightningStrikeEvent extends Event
{
    private final LightningBolt bolt;
    private final BlockPos pos;

    public LightningStrikeEvent(LightningBolt bolt, BlockPos pos)
    {
        this.bolt = bolt;
        this.pos = pos;
    }

    /**
     * @return The strike position of the lightning bolt.
     */
    public BlockPos getStrikePos()
    {
        return pos;
    }

    /**
     * @return The lightning bolt instance that is striking.
     */
    public LightningBolt getLightningBolt()
    {
        return bolt;
    }
}
