/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired for hooking into {@link AbstractContainerScreen} events.
 * See the subclasses to listen for specific events.
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see Render.Foreground
 * @see Render.Background
 */
public abstract class ContainerScreenEvent extends Event
{
    private final AbstractContainerScreen<?> containerScreen;

    @ApiStatus.Internal
    protected ContainerScreenEvent(AbstractContainerScreen<?> containerScreen)
    {
        this.containerScreen = containerScreen;
    }

    /**
     * {@return the container screen}
     */
    public AbstractContainerScreen<?> getContainerScreen()
    {
        return containerScreen;
    }

    /**
     * Fired every time an {@link AbstractContainerScreen} renders.
     * See the two subclasses to listen for foreground or background rendering.
     *
     * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @see Foreground
     * @see Background
     */
    public static abstract class Render extends ContainerScreenEvent
    {
        private final PoseStack poseStack;
        private final int mouseX;
        private final int mouseY;

        @ApiStatus.Internal
        protected Render(AbstractContainerScreen<?> guiContainer, PoseStack poseStack, int mouseX, int mouseY)
        {
            super(guiContainer);
            this.poseStack = poseStack;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        /**
         * {@return the pose stack used for rendering}
         */
        public PoseStack getPoseStack()
        {
            return poseStack;
        }

        /**
         * {@return the X coordinate of the mouse pointer}
         */
        public int getMouseX()
        {
            return mouseX;
        }

        /**
         * {@return the Y coordinate of the mouse pointer}
         */
        public int getMouseY()
        {
            return mouseY;
        }

        /**
         * Fired after the container screen's foreground layer and elements are drawn, but
         * before rendering the tooltips and the item stack being dragged by the player.
         *
         * <p>This can be used for rendering elements that must be above other screen elements, but
         * below tooltips and the dragged stack, such as slot or item stack specific overlays.</p>
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        public static class Foreground extends Render
        {
            @ApiStatus.Internal
            public Foreground(AbstractContainerScreen<?> guiContainer, PoseStack poseStack, int mouseX, int mouseY)
            {
                super(guiContainer, poseStack, mouseX, mouseY);
            }
        }

        /**
         * Fired after the container screen's background layer and elements are drawn.
         * This can be used for rendering new background elements.
         *
         * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        public static class Background extends Render
        {
            @ApiStatus.Internal
            public Background(AbstractContainerScreen<?> guiContainer, PoseStack poseStack, int mouseX, int mouseY)
            {
                super(guiContainer, poseStack, mouseX, mouseY);
            }
        }
    }
}
