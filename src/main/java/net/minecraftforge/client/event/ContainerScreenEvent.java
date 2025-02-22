/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.fml.LogicalSide;

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
public sealed interface ContainerScreenEvent {
    /**
     * {@return the container screen}
     */
    AbstractContainerScreen<?> containerScreen();

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
    sealed interface Render extends ContainerScreenEvent {
        /**
         * {@return the gui graphics used for rendering}
         */
        GuiGraphics guiGraphics();

        /**
         * {@return the X coordinate of the mouse pointer}
         */
        int mouseX();

        /**
         * {@return the Y coordinate of the mouse pointer}
         */
        int mouseY();

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
        record Foreground(AbstractContainerScreen<?> containerScreen, GuiGraphics guiGraphics, int mouseX, int mouseY)
                implements Render, RecordEvent {
            public static final EventBus<Foreground> BUS = EventBus.create(Foreground.class);
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
        record Background(AbstractContainerScreen<?> containerScreen, GuiGraphics guiGraphics, int mouseX, int mouseY)
                implements Render, RecordEvent {
            public static final EventBus<Background> BUS = EventBus.create(Background.class);
        }
    }
}
