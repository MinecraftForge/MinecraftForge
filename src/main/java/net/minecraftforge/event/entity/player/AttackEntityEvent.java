/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * AttackEntityEvent is fired when a player attacks an Entity.<br>
 * This event is fired whenever a player attacks an Entity in
 * {@link Player#attack(Entity)}.<br>
 * <br>
 * {@link #target} contains the Entity that was damaged by the player. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the player does not attack the Entity.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class AttackEntityEvent extends PlayerEvent
{
    private final Entity target;
    public AttackEntityEvent(Player player, Entity target)
    {
        super(player);
        this.target = target;
    }

    public Entity getTarget()
    {
        return target;
    }
}
