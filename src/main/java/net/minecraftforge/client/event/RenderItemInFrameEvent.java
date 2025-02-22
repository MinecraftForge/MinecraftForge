/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired before an item stack is rendered in an item frame.
 * This can be used to prevent normal rendering or add custom rendering.
 *
 * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * If the event is cancelled, then the item stack will not be rendered</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @param itemFrameState the item frame entity
 * @param renderer the renderer for the item frame entity
 * @param poseStack the pose stack used for rendering
 * @param multiBufferSource the source of rendering buffers
 *
 * @see ItemFrameRenderer
 */
public record RenderItemInFrameEvent(
        ItemFrameRenderState itemFrameState,
        ItemFrameRenderer<?> renderer,
        PoseStack poseStack,
        MultiBufferSource multiBufferSource,
        int packedLight
) implements Cancellable, RecordEvent {
    public static final CancellableEventBus<RenderItemInFrameEvent> BUS = CancellableEventBus.create(RenderItemInFrameEvent.class);

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    @Override
    public int packedLight() {
        return packedLight;
    }
}
