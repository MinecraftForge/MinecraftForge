/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.brewing;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;

/**
 * This event is called when a player picks up a potion from a brewing stand.
 */
public class PlayerBrewedPotionEvent extends PlayerEvent
{
    private final ItemStack stack;

    public PlayerBrewedPotionEvent(Player player, @Nonnull ItemStack stack)
    {
        super(player);
        this.stack = stack;
    }

    /**
     * The ItemStack of the potion.
     */
    @Nonnull
    public ItemStack getStack()
    {
        return stack;
    }
}
