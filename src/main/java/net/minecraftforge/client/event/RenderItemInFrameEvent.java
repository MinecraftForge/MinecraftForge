/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired before an item stack is rendered in an item frame.
 * This can be used to prevent normal rendering or add custom rendering.
 *
 * <p>This event is {@linkplain Cancellable cancellable}. If the event is cancelled, then the item stack will not be rendered</p>
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see ItemFrameRenderer
 */
public record RenderItemInFrameEvent(
        ItemFrameRenderState getItemFrameState,
        ItemFrameRenderer<?> getRenderer,
        PoseStack getPoseStack,
        SubmitNodeCollector getNodeCollector,
        int getPackedLight
) implements Cancellable, RecordEvent {
    public static final CancellableEventBus<RenderItemInFrameEvent> BUS = CancellableEventBus.create(RenderItemInFrameEvent.class);

    @ApiStatus.Internal
    public RenderItemInFrameEvent {}

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    public int getPackedLight() {
        return getPackedLight;
    }
}
