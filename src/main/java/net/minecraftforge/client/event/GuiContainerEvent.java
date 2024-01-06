/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.eventbus.api.Event;

/**
 * Event class for handling GuiContainer specific events.
 */
public class GuiContainerEvent extends Event
{

    private final AbstractContainerScreen guiContainer;

    public GuiContainerEvent(AbstractContainerScreen guiContainer)
    {
        this.guiContainer = guiContainer;
    }

    public AbstractContainerScreen getGuiContainer()
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
        private final PoseStack mStack;
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
        public DrawForeground(AbstractContainerScreen guiContainer, PoseStack mStack, int mouseX, int mouseY)
        {
            super(guiContainer);
            this.mStack = mStack;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public PoseStack getMatrixStack()
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
        private final PoseStack mStack;
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
        public DrawBackground(AbstractContainerScreen guiContainer, PoseStack mStack, int mouseX, int mouseY)
        {
            super(guiContainer);
            this.mStack = mStack;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public PoseStack getMatrixStack()
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
