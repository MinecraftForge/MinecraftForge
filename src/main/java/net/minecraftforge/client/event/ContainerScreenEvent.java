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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired for hooking into {@link AbstractContainerScreen} rendering.
 * See the two subclasses to listen for foreground or background rendering.
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see ContainerScreenEvent.DrawForeground
 * @see ContainerScreenEvent.DrawBackground
 */
public class ContainerScreenEvent extends Event
{
    private final AbstractContainerScreen<?> containerScreen;

    public ContainerScreenEvent(AbstractContainerScreen<?> containerScreen)
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
     * Fired after the container screen's foreground layer and elements are drawn, but
     * before rendering the tooltips and the item stack being dragged by the player.
     *
     * <p>This can be used for rendering elements that must be above other screen elements, but
     * below tooltips and the dragged stack, such as slot or item stack specific overlays. </p>
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class DrawForeground extends ContainerScreenEvent
    {
        private final PoseStack poseStack;
        private final int mouseX;
        private final int mouseY;

        public DrawForeground(AbstractContainerScreen<?> containerScreen, PoseStack poseStack, int mouseX, int mouseY)
        {
            super(containerScreen);
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
         * {@return the x coordinate of the mouse pointer}
         */
        public int getMouseX()
        {
            return mouseX;
        }

        /**
         * {@return the y coordinate of the mouse pointer}
         */
        public int getMouseY()
        {
            return mouseY;
        }
    }

    /**
     * Fired after the container screen's background layer and elements are drawn.
     * This can be used for rendering new background elements.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class DrawBackground extends ContainerScreenEvent
    {
        private final PoseStack poseStack;
        private final int mouseX;
        private final int mouseY;

        public DrawBackground(AbstractContainerScreen<?> containerScreen, PoseStack poseStack, int mouseX, int mouseY)
        {
            super(containerScreen);
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
         * {@return the x coordinate of the mouse pointer}
         */
        public int getMouseX()
        {
            return mouseX;
        }

        /**
         * {@return the y coordinate of the mouse pointer}
         */
        public int getMouseY()
        {
            return mouseY;
        }
    }
}
