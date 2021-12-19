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

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.eventbus.api.Event;

/**
 * Event class for handling GuiContainer specific events.
 */
public class ContainerScreenEvent extends Event
{

    private final AbstractContainerScreen<?> containerScreen;

    public ContainerScreenEvent(AbstractContainerScreen<?> containerScreen)
    {
        this.containerScreen = containerScreen;
    }

    public AbstractContainerScreen<?> getContainerScreen()
    {
        return containerScreen;
    }

    /**
     * This event is fired directly after the GuiContainer has draw any foreground elements,
     * But before the "dragged" stack, and before any tooltips.
     * This is useful for any slot / item specific overlays.
     * Things that need to be on top of All GUI elements but bellow tooltips and dragged stacks.
     */
    public static class DrawForeground extends ContainerScreenEvent
    {
        private final PoseStack poseStack;
        private final int mouseX;
        private final int mouseY;

        /**
         * Called directly after the GuiContainer has drawn any foreground elements.
         *
         * @param guiContainer The container.
         * @param poseStack    The pose stack.
         * @param mouseX       The current X position of the players mouse.
         * @param mouseY       The current Y position of the players mouse.
         */
        public DrawForeground(AbstractContainerScreen<?> guiContainer, PoseStack poseStack, int mouseX, int mouseY)
        {
            super(guiContainer);
            this.poseStack = poseStack;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public PoseStack getPoseStack()
        {
            return poseStack;
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
    public static class DrawBackground extends ContainerScreenEvent
    {
        private final PoseStack poseStack;
        private final int mouseX;
        private final int mouseY;

        /**
         * Called directly after the GuiContainer has drawn any background elements.
         *
         * @param guiContainer The container.
         * @param poseStack    The PoseStack.
         * @param mouseX       The current X position of the players mouse.
         * @param mouseY       The current Y position of the players mouse.
         */
        public DrawBackground(AbstractContainerScreen<?> guiContainer, PoseStack poseStack, int mouseX, int mouseY)
        {
            super(guiContainer);
            this.poseStack = poseStack;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public PoseStack getPoseStack()
        {
            return poseStack;
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
