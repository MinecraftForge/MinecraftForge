/*
 * Forge Mod Loader
 * Copyright (c) 2012-2014 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors (this class):
 *     bspkrs - implementation
 */

package cpw.mods.fml.client.config;

import net.minecraft.client.gui.GuiButton;

/**
 * This class implements an easy way to check if the mouse has hovered within a certain region of the screen for a given 
 * period of time. The region can be defined manually or by supplying a GuiButton object.
 * 
 * @author bspkrs
 */
public class HoverChecker
{
    private int       top, bottom, left, right, threshold;
    private GuiButton button;
    private long      hoverStart;
    
    public HoverChecker(int top, int bottom, int left, int right, int threshold)
    {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.threshold = threshold;
        this.hoverStart = -1;
    }
    
    public HoverChecker(GuiButton button, int threshold)
    {
        this.button = button;
        this.threshold = threshold;
    }
    
    /**
     * Call this method if the intended region has changed such as if the region must follow a scrolling list.
     * It is not necessary to call this method if a GuiButton defines the hover region.
     */
    public void updateBounds(int top, int bottom, int left, int right)
    {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }
    
    /**
     * Checks if the mouse is in the hover region. If the specified time period has elapsed the method returns true.
     * The hover timer is reset if the mouse is not within the region.
     */
    public boolean checkHover(int mouseX, int mouseY)
    {
        return checkHover(mouseX, mouseY, true);
    }

    /**
     * Checks if the mouse is in the hover region. If the specified time period has elapsed the method returns true.
     * The hover timer is reset if the mouse is not within the region.
     */
    public boolean checkHover(int mouseX, int mouseY, boolean canHover)
    {
        if (this.button != null)
        {
            this.top = button.yPosition;
            this.bottom = button.yPosition + button.height;
            this.left = button.xPosition;
            this.right = button.xPosition + button.width;
            canHover = canHover && button.visible;
        }
        
        if (canHover && hoverStart == -1 && mouseY >= top && mouseY <= bottom && mouseX >= left && mouseX <= right)
            hoverStart = System.currentTimeMillis();
        else if (!canHover || mouseY < top || mouseY > bottom || mouseX < left || mouseX > right)
            resetHoverTimer();
        
        return canHover && hoverStart != -1 && System.currentTimeMillis() - hoverStart >= threshold;
    }
    
    /**
     * Manually resets the hover timer.
     */
    public void resetHoverTimer()
    {
        hoverStart = -1;
    }
}