/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Fired when an overlay is about to be rendered to the screen to allow the user to modify it.
 *
 * @see BossEventProgress
 * @see DebugText
 * @see Chat
 */
public sealed interface CustomizeGuiOverlayEvent {
    Window getWindow();

    GuiGraphicsExtractor getGuiGraphics();

    float getPartialTick();

    /**
     * Fired <b>before</b> a boss health bar is rendered to the screen.
     *
     * <p>This event is {@linkplain Cancellable cancellable}.
     * Cancelling this event will prevent the given bar from rendering.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    final class BossEventProgress extends MutableEvent implements Cancellable, CustomizeGuiOverlayEvent {
        public static final CancellableEventBus<BossEventProgress> BUS = CancellableEventBus.create(BossEventProgress.class);

        private final Window window;
        private final GuiGraphicsExtractor guiGraphics;
        private final float partialTick;
        private final LerpingBossEvent bossEvent;
        private final int x;
        private final int y;
        private int increment;

        @ApiStatus.Internal
        public BossEventProgress(Window window, GuiGraphicsExtractor guiGraphics, float partialTick, LerpingBossEvent bossEvent, int x, int y, int increment) {
            this.window = window;
            this.guiGraphics = guiGraphics;
            this.partialTick = partialTick;
            this.bossEvent = bossEvent;
            this.x = x;
            this.y = y;
            this.increment = increment;
        }

        @Override
        public Window getWindow() {
            return window;
        }

        @Override
        public GuiGraphicsExtractor getGuiGraphics() {
            return guiGraphics;
        }

        @Override
        public float getPartialTick() {
            return partialTick;
        }

        /**
         * @return the boss health bar currently being rendered
         */
        public LerpingBossEvent getBossEvent() {
            return bossEvent;
        }

        /**
         * {@return the X position of the boss health bar}
         */
        public int getX() {
            return x;
        }

        /**
         * {@return the Y position of the boss health bar}
         */
        public int getY() {
            return y;
        }

        /**
         * {@return the Y position increment before rendering the next boss health bar}
         */
        public int getIncrement() {
            return increment;
        }

        /**
         * Sets the Y position increment before rendering the next boss health bar.
         *
         * @param increment the new Y position increment
         */
        public void setIncrement(int increment) {
            this.increment = increment;
        }
    }

    /**
     * Fired <b>before</b> textual information is rendered to the debug screen.
     * This can be used to add or remove text information.
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @param getText the modifiable list of text to render on the side
     * @param getSide the side of the text getting rendered
     */
    record DebugText(
            Window getWindow,
            GuiGraphicsExtractor getGuiGraphics,
            float getPartialTick,
            List<String> getText,
            Side getSide
    ) implements RecordEvent, CustomizeGuiOverlayEvent {
        public static final EventBus<DebugText> BUS = EventBus.create(DebugText.class);

        @ApiStatus.Internal
        public DebugText {}

        public enum Side {
            Left,
            Right
        }
    }

    /**
     * Fired <b>before</b> the chat messages overlay is rendered to the screen.
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    final class Chat extends MutableEvent implements CustomizeGuiOverlayEvent {
        public static final EventBus<Chat> BUS = EventBus.create(Chat.class);

        private final Window window;
        private final GuiGraphicsExtractor guiGraphics;
        private final float partialTick;
        private int posX;
        private int posY;

        @ApiStatus.Internal
        public Chat(Window window, GuiGraphicsExtractor guiGraphics, float partialTick, int posX, int posY) {
            this.window = window;
            this.guiGraphics = guiGraphics;
            this.partialTick = partialTick;
            this.setPosX(posX);
            this.setPosY(posY);
        }

        @Override
        public Window getWindow() {
            return window;
        }

        @Override
        public GuiGraphicsExtractor getGuiGraphics() {
            return guiGraphics;
        }

        @Override
        public float getPartialTick() {
            return partialTick;
        }

        /**
         * @return the X position of the chat messages overlay
         */
        public int getPosX() {
            return posX;
        }

        /**
         * Sets the new X position for rendering the chat messages overlay
         *
         * @param posX the new X position
         */
        public void setPosX(int posX) {
            this.posX = posX;
        }

        /**
         * @return the Y position of the chat messages overlay
         */
        public int getPosY() {
            return posY;
        }

        /**
         * Sets the new Y position for rendering the chat messages overlay
         *
         * @param posY the new y position
         */
        public void setPosY(int posY) {
            this.posY = posY;
        }
    }
}
