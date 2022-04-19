/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.client.player.Input;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * This event is fired after player movement inputs are updated.<br>
 * Handlers can freely manipulate {@link Input} to cancel movement.<br>
 */
public class MovementInputUpdateEvent extends PlayerEvent
{
    private final Input input;

    public MovementInputUpdateEvent(Player player, Input input)
    {
        super(player);
        this.input = input;
    }

    public Input getInput()
    {
        return input;
    }

}
