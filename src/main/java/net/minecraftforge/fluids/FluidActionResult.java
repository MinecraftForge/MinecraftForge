/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Holds the result of a fluid action from {@link FluidUtil}.
 *
 * Failed actions will always have {@link #isSuccess()} == false and an empty ItemStack result. See {@link #FAILURE}.
 *
 * Successful actions will always have {@link #isSuccess()} == true.
 * Successful actions may have an empty ItemStack result in some cases,
 * for example the action succeeded and the resulting item was consumed.
 */
public class FluidActionResult
{
	public static final FluidActionResult FAILURE = new FluidActionResult(false, ItemStack.EMPTY);

	public final boolean success;
	@NotNull
	public final ItemStack result;

	public FluidActionResult(@NotNull ItemStack result)
	{
		this(true, result);
	}

	private FluidActionResult(boolean success, @NotNull ItemStack result)
	{
		this.success = success;
		this.result = result;
	}

	public boolean isSuccess()
	{
		return success;
	}

	@NotNull
	public ItemStack getResult()
	{
		return result;
	}
}
