/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Base class for all events that modify the behavior of ItemStack, and require Player as user.
 */
public abstract class PlayerItemStackEvent extends PlayerEvent
{

	private final ItemStack stack;

	public PlayerItemStackEvent(Player user, ItemStack stack)
	{
		super(user);
		this.stack = stack;
	}

	public ItemStack getStack()
	{
		return stack;
	}

}