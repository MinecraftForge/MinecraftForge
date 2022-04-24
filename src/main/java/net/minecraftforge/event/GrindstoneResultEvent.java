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
 * GrindstoneResultEvent is fired when the before the result stack is taken from a grindstone. <br> 
 * It is called from {@link GrindstoneMenu#GrindstoneMenu(...).new Slot() {...}.onTake(Player, ItemStack)}. <br>
 * If the event is canceled, vanilla behavior will not run, and the output will be set to {@link ItemStack#EMPTY}. <br>
 * If the event is not canceled, but the output is not empty, it will set the output and not run vanilla behavior. <br>
 * if the output is empty, and the event is not canceled, vanilla behavior will execute. <br>
 * if the amount of experience is larger than or equal 0, the vanilla behavior for calculating experience will not run. <br>
 */
@Cancelable
public class GrindstoneResultEvent extends Event
{
	private final ItemStack top;
    private final ItemStack bottom;
    private ItemStack newTop;
    private ItemStack newBottom;
    
    public GrindstoneResultEvent(ItemStack top, ItemStack bottom) 
    {
        this.top = top;
        this.bottom = bottom;
        this.newTop = ItemStack.EMPTY;
        this.newBottom = ItemStack.EMPTY;
    }
    
    /**
     * @return The item in the top input grindstone slot. <br>
     */
    public ItemStack getTop()
    {
        return top;
    }
    
    /**
     * @return The item in the top input grindstone slot. <br>
     */
    public ItemStack getnewTop()
    {
        return newTop;
    }
    
    /**
     * Sets the itemstack in the top slot
     * @param top
     */
    public void setnewTop(ItemStack top)
    {
        this.newTop = top;
    }
    
    /**
     * @return The item in the bottom input grindstone slot. <br>
     */
    public ItemStack getBottom() 
    {
        return bottom;
    }
    
    /**
     * @return The item in the bottom input grindstone slot. <br>
     */
    public ItemStack getnewBottom() 
    {
        return newBottom;
    }
    
    /**
     * Sets the itemstack in the bottom slot
     * @param top
     */
    public void setnewBottom(ItemStack bottom)
    {
        this.newBottom = bottom;
    }
}
