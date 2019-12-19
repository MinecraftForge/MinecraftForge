/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.event.furnace;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraftforge.eventbus.api.Event;

public class FurnaceSmeltEvent extends Event {

	private final AbstractFurnaceTileEntity furnace;
	private final IRecipe<?> recipe;

	public FurnaceSmeltEvent(AbstractFurnaceTileEntity furnace, IRecipe<?> recipe) {
		this.furnace = furnace;
		this.recipe = recipe;
	}

	public AbstractFurnaceTileEntity getFurnace() {
		return furnace;
	}

	public IRecipe<?> getRecipe() {
		return recipe;
	}

	/**
	 * FurnaceSmeltEvent.Pre is fired before vanilla smelt logic<br>
	 * <br>
	 * The event is fired during the {@link AbstractFurnaceTileEntity#func_214007_c(IRecipe)} method invocation.<br>
	 * <br>
	 * {@link #furnace} is the abstract furnace which is attempting to smelt<br>
	 * <br>
	 * {@link #recipe} is the recipe that is being processed<br>
	 * <br>
	 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
	 * If the event is not canceled, the vanilla smelting will take place uninterrupted.
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 * <br>
	 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
	 */
	@net.minecraftforge.eventbus.api.Cancelable
	public static class Pre extends FurnaceSmeltEvent {

		public Pre(AbstractFurnaceTileEntity furnace, IRecipe<?> recipe) {
			super(furnace, recipe);
		}
	}

	/**
	 * FurnaceSmeltEvent.Post is fired after vanilla smelt logic<br>
	 * <br>
	 * The event is fired during the {@link AbstractFurnaceTileEntity#func_214007_c(IRecipe)} method invocation.<br>
	 * <br>
	 * {@link #furnace} is the abstract furnace which is attempting to smelt<br>
	 * <br>
	 * {@link #recipe} is the recipe that is being processed<br>
	 * <br>
	 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
	 * <br>
	 * This event does not have a result. {@link HasResult}<br>
	 * <br>
	 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
	 */
	public static class Post extends FurnaceSmeltEvent {

		public Post(AbstractFurnaceTileEntity furnace, IRecipe<?> recipe) {
			super(furnace, recipe);
		}
	}
}
