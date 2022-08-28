/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public abstract class GrindstoneEvent extends Event
{
    private final ItemStack top;
    private final ItemStack bottom;
    private int xp;

    protected GrindstoneEvent(ItemStack top, ItemStack bottom, int xp)
    {
        this.top = top;
        this.bottom = bottom;
        this.xp = xp;
    }

    /**
     * @return The item in the top input grindstone slot. <br>
     */
    public ItemStack getTopItem()
    {
        return top;
    }

    /**
     * @return The item in the bottom input grindstone slot. <br>
     */
    public ItemStack getBottomItem()
    {
        return bottom;
    }

    /**
     * This is the experience amount determined by the event, not the vanilla behavior. <br>
     * If you are the first receiver of this event, it is guaranteed to be -1. <br>
     * The value must be set to a positive value to override vanilla behavior. <br>
     * if the value is equal to -1, the vanilla behavior for calculating experience will run. <br>
     * @return The experience amount given to the player. <br>
     */
    public int getXp()
    {
        return xp;
    }

    /**
     * Sets the experience amount. <br>
     * @param xp The experience amount given to the player. <br>
     */
    public void setXp(int xp)
    {
        this.xp = xp;
    }

    /**
     * This event is {@link Cancelable} <br>
     * {@link OnplaceItem} is fired when the inputs to a grindstone are changed. <br>
     * It is called from {@link GrindstoneMenu#createResult()}. <br>
     * If the event is canceled, vanilla behavior will not run, and the output will be set to {@link ItemStack#EMPTY}. <br>
     * If the event is not canceled, but the output is not empty, it will set the output and not run vanilla behavior. <br>
     * if the output is empty, and the event is not canceled, vanilla behavior will execute. <br>
     * if the amount of experience is larger than or equal 0, the vanilla behavior for calculating experience will not run. <br>
     */
    @Cancelable
    public static class OnplaceItem extends GrindstoneEvent
    {
        private ItemStack output;

        public OnplaceItem(ItemStack top, ItemStack bottom, int xp)
        {
            super(top, bottom, xp);
            this.output = ItemStack.EMPTY;
        }

        /**
         * This is the output as determined by the event, not by the vanilla behavior between these two items. <br>
         * If you are the first receiver of this event, it is guaranteed to be empty. <br>
         * It will only be non-empty if changed by an event handler. <br>
         * If this event is cancelled, this output stack is discarded. <br>
         * @return The item to set in the output grindstone slot. <br>
         */
        public ItemStack getOutput()
        {
            return output;
        }

        /**
         * Sets the output slot to a specific itemstack.
         * @param output The stack to change the output to.
         */
        public void setOutput(ItemStack output)
        {
            this.output = output;
        }
    }

    /**
     * This event is {@link Cancelable} <br>
     * {@link OnTakeItem} is fired when the output in a grindstone are is taken. <br>
     * It is called from {@link GrindstoneMenu#GrindstoneMenu(int, Inventory)}. <br>
     * If the event is canceled, vanilla behavior will not run, and no inputs will be consumed. <br>
     * if the amount of experience is larger than or equal 0, the vanilla behavior for calculating experience will not run. <br>
     */
    @Cancelable
    public static class OnTakeItem extends GrindstoneEvent
    {
        private ItemStack newTop = ItemStack.EMPTY;
        private ItemStack newBottom = ItemStack.EMPTY;

        public OnTakeItem(ItemStack top, ItemStack bottom, int xp)
        {
            super(top, bottom, xp);
        }

        /**
         * @return The item in that will be in the top input grindstone slot after the event. <br>
         */
        public ItemStack getNewTopItem()
        {
            return newTop;
        }

        /**
         * @return The item in that will be in the bottom input grindstone slot after the event. <br>
         */
        public ItemStack getNewBottomItem()
        {
            return newBottom;
        }

        /**
         * Sets the itemstack in the top slot. <br>
         * @param newTop
         */
        public void setNewTopItem(ItemStack newTop)
        {
            this.newTop = newTop;
        }

        /**
         * Sets the itemstack in the bottom slot. <br>
         * @param newBottom
         */
        public void setNewBottomItem(ItemStack newBottom)
        {
            this.newBottom = newBottom;
        }

        /**
         * This is the experience amount that will be returned by the event. <br>
         * @return The experience amount given to the player. <br>
         */
        public int getXp()
        {
            return super.getXp();
        }
    }
}
