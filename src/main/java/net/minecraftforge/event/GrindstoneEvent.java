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
import org.jetbrains.annotations.ApiStatus;

public abstract class GrindstoneEvent extends Event
{
    private final ItemStack top;
    private final ItemStack bottom;
    private int xp;

    @ApiStatus.Internal
    protected GrindstoneEvent(ItemStack top, ItemStack bottom, int xp)
    {
        this.top = top;
        this.bottom = bottom;
        this.xp = xp;
    }

    /**
     * {@return the item in the top input slot}
     */
    public ItemStack getTopItem()
    {
        return top;
    }

    /**
     * {@return the item in the bottom input slot}
     */
    public ItemStack getBottomItem()
    {
        return bottom;
    }

    /**
     * {@return the experience amount given to the player} <br>
     * This is the experience amount determined by the event. It will be {@code -1} unless {@link #setXp(int)} is called. <br>
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
     * This event is {@link Cancelable}, and is fired when the inputs of a grindstone are changed. <br>
     *
     * The following rules apply:
     * <ul>
     *     <li>If the event is canceled, vanilla behavior will not run, and the output will be {@linkplain ItemStack#EMPTY empty}.</li>
     *     <li>If the event is not canceled</li>
     *     <ul>
     *          <li>and the output is empty, the output will be determined by vanilla.</li>
     *          <li>and the output is not empty, the output will be set, without running vanilla behavior.</li>
     *     </ul>
     *     <li>Vanilla XP calculation logic will be used unless all of the following criterias are met:</li>
     *     <ul>
     *         <li>the amount of experience is greater than or equal to {@code 0};</li>
     *         <li>the event is not {@linkplain #isCanceled() canceled};</li>
     *         <li>the {@linkplain #getOutput() output} is not empty.</li>
     *     </ul>
     * </ul>
     */
    @Cancelable
    public static class OnPlaceItem extends GrindstoneEvent
    {
        private ItemStack output;

        @ApiStatus.Internal
        public OnPlaceItem(ItemStack top, ItemStack bottom, int xp)
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
     * This event is {@link Cancelable}, and is fired when the output in a grindstone is taken. <br>
     * If the event is canceled, vanilla behavior will not run, and no inputs will be consumed. <br>
     * If the amount of experience is larger than or equal 0, the vanilla behavior for calculating experience will not run.
     * @see GrindstoneMenu#GrindstoneMenu(int, Inventory)
     */
    @Cancelable
    public static class OnTakeItem extends GrindstoneEvent
    {
        private ItemStack newTop = ItemStack.EMPTY;
        private ItemStack newBottom = ItemStack.EMPTY;

        @ApiStatus.Internal
        public OnTakeItem(ItemStack top, ItemStack bottom, int xp)
        {
            super(top, bottom, xp);
        }

        /**
         * {@return the item in that will be in the top input slot after the event}
         */
        public ItemStack getNewTopItem()
        {
            return newTop;
        }

        /**
         * {@return the item in that will be in the bottmon input slot after the event}
         */
        public ItemStack getNewBottomItem()
        {
            return newBottom;
        }

        /**
         * Sets the {@linkplain ItemStack} in the top slot.
         * @param newTop the new stack
         */
        public void setNewTopItem(ItemStack newTop)
        {
            this.newTop = newTop;
        }

        /**
         * Sets the {@linkplain ItemStack} in the bottom slot.
         * @param newBottom the new stack
         */
        public void setNewBottomItem(ItemStack newBottom)
        {
            this.newBottom = newBottom;
        }

        /**
         * {@return the experience amount given to the player}
         */
        public int getXp()
        {
            return super.getXp();
        }
    }
}
