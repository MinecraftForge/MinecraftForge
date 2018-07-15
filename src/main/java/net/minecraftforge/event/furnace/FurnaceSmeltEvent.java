/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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


import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * FurnaceSmeltEvent is fired when a furnace smelts an item. <br>
 * <br>
 * The furnace will use {@link #getOutputStack} as the result <br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 **/
public class FurnaceSmeltEvent extends Event {

    private final TileEntityFurnace furnace;
    private final ItemStack inputStack;
    private ItemStack outputStack;

    public FurnaceSmeltEvent(TileEntityFurnace furnace, ItemStack inputStack, ItemStack outputStack) {
        this.furnace = furnace;
        this.inputStack = inputStack;
        this.outputStack = outputStack;
    }

    public TileEntityFurnace getFurnace() {
        return furnace;
    }

    public ItemStack getInputStack() {
        return inputStack;
    }

    public ItemStack getOutputStack() {
        return outputStack;
    }

    public void setOutputStack(ItemStack stack) {
        this.outputStack = stack;
    }
}