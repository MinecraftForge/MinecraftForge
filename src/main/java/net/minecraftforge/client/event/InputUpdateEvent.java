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
public class InputUpdateEvent extends PlayerEvent
{
    private final Input movementInput;

    public InputUpdateEvent(Player player, Input movementInput)
    {
        super(player);
        this.movementInput = movementInput;
    }

    public Input getMovementInput()
    {
        return movementInput;
    }

}
