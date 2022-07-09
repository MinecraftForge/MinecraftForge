/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;

/**
 * Fired when an overlay is about to be rendered to the screen to allow the user to modify it.
 *
 * @see BossEventProgress
 * @see DebugText
 * @see Chat
 */
public abstract class CustomizeGuiOverlayEvent extends Event
{
    private final Window window;
    private final PoseStack poseStack;
    private final float partialTick;

    @ApiStatus.Internal
    protected CustomizeGuiOverlayEvent(Window window, PoseStack poseStack, float partialTick)
    {
        this.window = window;
        this.poseStack = poseStack;
        this.partialTick = partialTick;
    }

    public Window getWindow()
    {
        return window;
    }

    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    public float getPartialTick()
    {
        return partialTick;
    }

    /**
     * Fired <b>before</b> a boss health bar is rendered to the screen.
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * Cancelling this event will prevent the given bar from rendering.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @Cancelable
    public static class BossEventProgress extends CustomizeGuiOverlayEvent
    {
        private final LerpingBossEvent bossEvent;
        private final int x;
        private final int y;
        private int increment;

        @ApiStatus.Internal
        public BossEventProgress(Window window, PoseStack poseStack, float partialTick, LerpingBossEvent bossEvent, int x, int y, int increment)
        {
            super(window, poseStack, partialTick);
            this.bossEvent = bossEvent;
            this.x = x;
            this.y = y;
            this.increment = increment;
        }

        /**
         * @return the boss health bar currently being rendered
         */
        public LerpingBossEvent getBossEvent()
        {
            return bossEvent;
        }

        /**
         * {@return the X position of the boss health bar}
         */
        public int getX()
        {
            return x;
        }

        /**
         * {@return the Y position of the boss health bar}
         */
        public int getY()
        {
            return y;
        }

        /**
         * {@return the Y position increment before rendering the next boss health bar}
         */
        public int getIncrement()
        {
            return increment;
        }

        /**
         * Sets the Y position increment before rendering the next boss health bar.
         *
         * @param increment the new Y position increment
         */
        public void setIncrement(int increment)
        {
            this.increment = increment;
        }
    }

    /**
     * Fired <b>before</b> textual information is rendered to the debug screen.
     * This can be used to add or remove text information.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class DebugText extends CustomizeGuiOverlayEvent
    {
        private final ArrayList<String> left;
        private final ArrayList<String> right;

        @ApiStatus.Internal
        public DebugText(Window window, PoseStack poseStack, float partialTick, ArrayList<String> left, ArrayList<String> right)
        {
            super(window, poseStack, partialTick);
            this.left = left;
            this.right = right;
        }

        /**
         * @return the modifiable list of text to render on the left side
         */
        public ArrayList<String> getLeft()
        {
            return left;
        }

        /**
         * @return the modifiable list of text to render on the right side
         */
        public ArrayList<String> getRight()
        {
            return right;
        }
    }

    /**
     * Fired <b>before</b> the chat messages overlay is rendered to the screen.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.<p/>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class Chat extends CustomizeGuiOverlayEvent
    {
        private int posX;
        private int posY;

        @ApiStatus.Internal
        public Chat(Window window, PoseStack poseStack, float partialTick, int posX, int posY)
        {
            super(window, poseStack, partialTick);
            this.setPosX(posX);
            this.setPosY(posY);
        }

        /**
         * @return the X position of the chat messages overlay
         */
        public int getPosX()
        {
            return posX;
        }

        /**
         * Sets the new X position for rendering the chat messages overlay
         *
         * @param posX the new X position
         */
        public void setPosX(int posX)
        {
            this.posX = posX;
        }

        /**
         * @return the Y position of the chat messages overlay
         */
        public int getPosY()
        {
            return posY;
        }

        /**
         * Sets the new Y position for rendering the chat messages overlay
         *
         * @param posY the new y position
         */
        public void setPosY(int posY)
        {
            this.posY = posY;
        }
    }
}
