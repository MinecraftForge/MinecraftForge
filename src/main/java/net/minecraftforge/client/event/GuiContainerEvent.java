/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraftforge.eventbus.api.Event;

/**
 * Event class for handling GuiContainer specific events.
 */
public class GuiContainerEvent extends Event
{

    private final ContainerScreen guiContainer;

    public GuiContainerEvent(ContainerScreen guiContainer)
    {
        this.guiContainer = guiContainer;
    }

    public ContainerScreen getGuiContainer()
    {
        return guiContainer;
    }

    /**
     * This event is fired directly after the GuiContainer has draw any foreground elements,
     * But before the "dragged" stack, and before any tooltips.
     * This is useful for any slot / item specific overlays.
     * Things that need to be on top of All GUI elements but bellow tooltips and dragged stacks.
     */
    public static class DrawForeground extends GuiContainerEvent
    {
        private final MatrixStack mStack;
        private final int mouseX;
        private final int mouseY;

        /**
         * Called directly after the GuiContainer has drawn any foreground elements.
         *
         * @param guiContainer The container.
         * @param mStack       The MatrixStack.
         * @param mouseX       The current X position of the players mouse.
         * @param mouseY       The current Y position of the players mouse.
         */
        public DrawForeground(ContainerScreen guiContainer, MatrixStack mStack, int mouseX, int mouseY)
        {
            super(guiContainer);
            this.mStack = mStack;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public MatrixStack getMatrixStack()
        {
            return mStack;
        }

        public int getMouseX()
        {
            return mouseX;
        }

        public int getMouseY()
        {
            return mouseY;
        }
    }
    
    /**
     * This event is fired directly after the GuiContainer has draw any background elements,
     * This is useful for drawing new background elements.
     */
    public static class DrawBackground extends GuiContainerEvent
    {
        private final MatrixStack mStack;
        private final int mouseX;
        private final int mouseY;

        /**
         * Called directly after the GuiContainer has drawn any background elements.
         *
         * @param guiContainer The container.
         * @param mStack       The MatrixStack.
         * @param mouseX       The current X position of the players mouse.
         * @param mouseY       The current Y position of the players mouse.
         */
        public DrawBackground(ContainerScreen guiContainer, MatrixStack mStack, int mouseX, int mouseY)
        {
            super(guiContainer);
            this.mStack = mStack;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public MatrixStack getMatrixStack()
        {
            return mStack;
        }

        public int getMouseX()
        {
            return mouseX;
        }

        public int getMouseY()
        {
            return mouseY;
        }
    }
}
