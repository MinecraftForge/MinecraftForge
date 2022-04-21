/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * 
 * GrindstoneUpdateEvent is fired when the inputs to a grindstone are changed. <br> 
 * It is called from {@link GrindstoneMenu#createResult()}. <br>
 * If the event is canceled, vanilla behavior will not run, and the output will be set to {@link ItemStack#EMPTY}. <br>
 * If the event is not canceled, but the output is not empty, it will set the output and not run vanilla behavior. <br>
 * if the output is empty, and the event is not canceled, vanilla behavior will execute. <br>
 * if the amount of experience is larger than or equal 0, the vanilla behavior for calculating experience will not run. <br>
 */
@Cancelable
public class GrindstoneUpdateEvent extends Event
{
	
    private final ItemStack top;
    private final ItemStack bottom;
    private ItemStack output;
    private int xp;
    
    public GrindstoneUpdateEvent(ItemStack top, ItemStack bottom) 
    {
        this.top = top;
        this.bottom = bottom;
        this.output = ItemStack.EMPTY;
        this.xp = 0;
    }
    
    /**
     * @return The item in the top input grindstone slot. <br>
     */
    public ItemStack getTop()
    {
        return top;
    }
    
    /**
     * @return The item in the bottom input grindstone slot. <br>
     */
    public ItemStack getBottom() 
    {
        return bottom;
    }
    
    /**
     * This is the output as determined by the event, not by the vanilla behavior between these two items. <br>
     * If you are the first receiver of this event, it is guaranteed to be empty. <br>
     * It will only be non-empty if changed by an event handler. <br>
     * If this event is cancelled, this output stack is discarded. <br>
     * @return The item to set in the output grindstone slot.
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
    
    /**
     * This is the experience amount determined by the event, not the vanilla behavior. <br>
     * If you are the first receiver of this event, it is guaranteed to be 0. <br>
     * if the value is equal to -1, the vanilla behavior for calculating experience will run. <br>
     * @return The experience amount given to the player.
     */
    public int getXp()
    {
        return xp;
    }
    
    /**
     * Sets the experience amount.
     * @param xp The experience amount given to the player.
     */
    public void setXp(int xp)
    {
        this.xp = xp;
    }
}
