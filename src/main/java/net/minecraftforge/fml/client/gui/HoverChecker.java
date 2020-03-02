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

package net.minecraftforge.fml.client.gui;

import net.minecraft.client.gui.widget.Widget;

/**
 * This class implements an easy way to check if the mouse has hovered within a certain region of the screen for a given
 * period of time. The region can be defined manually or by supplying a GuiButton object.
 *
 * @author bspkrs
 */
public class HoverChecker
{
    private int       top, bottom, left, right, threshold;
    private Widget    widget;
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

    public HoverChecker(Widget widget, int threshold)
    {
        this.widget = widget;
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
        if (this.widget != null)
        {
            this.top = widget.y;
            this.bottom = widget.y + widget.getHeight();
            this.left = widget.x;
            this.right = widget.x + widget.getWidth();
            canHover = canHover && widget.visible;
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
