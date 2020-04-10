/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fluids;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

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
	@Nonnull
	public final ItemStack result;

	public FluidActionResult(@Nonnull ItemStack result)
	{
		this(true, result);
	}

	private FluidActionResult(boolean success, @Nonnull ItemStack result)
	{
		this.success = success;
		this.result = result;
	}

	public boolean isSuccess()
	{
		return success;
	}

	@Nonnull
	public ItemStack getResult()
	{
		return result;
	}
}
