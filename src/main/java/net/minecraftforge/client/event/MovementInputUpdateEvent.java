/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.client.player.Input;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.LogicalSide;

/**
 * <p>Fired after the player's movement inputs are updated.</p>
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 */
public class MovementInputUpdateEvent extends PlayerEvent
{
    private final Input input;

    /**
     * @hidden
     * @see ForgeHooksClient#onMovementInputUpdate(Player, Input)
     */
    public MovementInputUpdateEvent(Player player, Input input)
    {
        super(player);
        this.input = input;
    }

    /**
     * {@return the player's movement inputs}
     */
    public Input getInput()
    {
        return input;
    }

}
