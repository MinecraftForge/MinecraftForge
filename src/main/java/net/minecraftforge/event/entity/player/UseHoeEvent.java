/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

import javax.annotation.Nonnull;

/**
 * This event is fired when a player attempts to use a Hoe on a block, it
 * can be canceled to completely prevent any further processing.
 *
 * You can also set the result to ALLOW to mark the event as processed
 * and damage the hoe.
 *
 * setResult(ALLOW) is the same as the old setHandled();
 */
@Cancelable
@HasResult
// TODO 1.19: Remove
@Deprecated(forRemoval = true, since = "1.18.2")
public class UseHoeEvent extends PlayerEvent
{
    private final UseOnContext context;;

    public UseHoeEvent(UseOnContext context)
    {
        super(context.getPlayer());
        this.context = context;
    }

    @Nonnull
    public UseOnContext getContext()
    {
        return context;
    }
}
