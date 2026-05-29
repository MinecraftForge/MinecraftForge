/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired for hooking into {@link AbstractContainerScreen} events.
 * See the subclasses to listen for specific events.
 *
 * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see Render.Foreground
 * @see Render.Background
 */
public sealed interface ContainerScreenEvent {
    /**
     * {@return the container screen}
     */
    AbstractContainerScreen<?> getContainerScreen();

    /**
     * Fired every time an {@link AbstractContainerScreen} renders.
     * See the two subclasses to listen for foreground or background rendering.
     *
     * <p>These events are fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @see Foreground
     * @see Background
     */
    sealed interface Render extends ContainerScreenEvent {
        /**
         * {@return the gui graphics used for rendering}
         */
        GuiGraphicsExtractor getGuiGraphics();

        /**
         * {@return the X coordinate of the mouse pointer}
         */
        int getMouseX();

        /**
         * {@return the Y coordinate of the mouse pointer}
         */
        int getMouseY();

        /**
         * Fired after the container screen's foreground layer and elements are drawn, but
         * before rendering the tooltips and the item stack being dragged by the player.
         *
         * <p>This can be used for rendering elements that must be above other screen elements, but
         * below tooltips and the dragged stack, such as slot or item stack specific overlays.</p>
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Foreground(
                AbstractContainerScreen<?> getContainerScreen,
                GuiGraphicsExtractor getGuiGraphics,
                int getMouseX,
                int getMouseY
        ) implements RecordEvent, Render {
            public static final EventBus<Foreground> BUS = EventBus.create(Foreground.class);

            @ApiStatus.Internal
            public Foreground {}
        }

        /**
         * Fired after the container screen's background layer and elements are drawn.
         * This can be used for rendering new background elements.
         *
         * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
         * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
         */
        record Background(
                AbstractContainerScreen<?> getContainerScreen,
                GuiGraphicsExtractor getGuiGraphics,
                int getMouseX,
                int getMouseY
        ) implements RecordEvent, Render {
            public static final EventBus<Background> BUS = EventBus.create(Background.class);

            @ApiStatus.Internal
            public Background {}
        }
    }
}
