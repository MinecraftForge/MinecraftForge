/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired when a player is being rendered.
 * See the two subclasses for listening for before and after rendering.
 *
 * @see RenderPlayerEvent.Pre
 * @see RenderPlayerEvent.Post
 * @see PlayerRenderer
 */
public sealed interface RenderPlayerEvent {
    PlayerRenderState state();

    /**
     * {@return the player entity renderer}
     */
    PlayerRenderer renderer();

    /**
     * {@return the pose stack used for rendering}
     */
    PoseStack poseStack();

    /**
     * {@return the source of rendering buffers}
     */
    MultiBufferSource multiBufferSource();

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    int packedLight();

    /**
     * Fired <b>before</b> the player is rendered.
     * This can be used for rendering additional effects or suppressing rendering.
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the player will not be rendered and the corresponding
     * {@link RenderPlayerEvent.Post} will not be fired.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    record Pre(
            PlayerRenderState state,
            PlayerRenderer renderer,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int packedLight
    ) implements Cancellable, RecordEvent, RenderPlayerEvent {
        public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);
    }

    /**
     * Fired <b>after</b> the player is rendered, if the corresponding {@link RenderPlayerEvent.Pre} is not cancelled.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    record Post(
            PlayerRenderState state,
            PlayerRenderer renderer,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int packedLight
    ) implements RecordEvent, RenderPlayerEvent {
        public static final EventBus<Post> BUS = EventBus.create(Post.class);
    }
}
