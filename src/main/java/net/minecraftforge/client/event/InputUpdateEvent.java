/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.MovementInput;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * This event is fired after player movement inputs are updated.<br>
 * Handlers can freely manipulate {@link MovementInput} to cancel movement.<br>
 */
public class InputUpdateEvent extends PlayerEvent
{
    private final MovementInput movementInput;

    public InputUpdateEvent(PlayerEntity player, MovementInput movementInput)
    {
        super(player);
        this.movementInput = movementInput;
    }

    public MovementInput getMovementInput()
    {
        return movementInput;
    }

}
