/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.item.ItemUseContext;
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
 * 
 * TODO: 1.17 Remove
 */
@Cancelable
@HasResult
@Deprecated
public class UseHoeEvent extends PlayerEvent
{
    private final ItemUseContext context;;

    public UseHoeEvent(ItemUseContext context)
    {
        super(context.getPlayer());
        this.context = context;
    }

    @Nonnull
    public ItemUseContext getContext()
    {
        return context;
    }
}
