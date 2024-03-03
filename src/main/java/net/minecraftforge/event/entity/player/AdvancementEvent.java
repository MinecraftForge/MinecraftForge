/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.advancements.Advancement;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

/**
 * This event is fired when a player gets an advancement.
 * <br>
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class AdvancementEvent extends PlayerEvent
{
    private final Advancement advancement;

    public AdvancementEvent(Player player, Advancement advancement)
    {
        super(player);
        this.advancement = advancement;
    }

    public Advancement getAdvancement()
    {
        return advancement;
    }
}
