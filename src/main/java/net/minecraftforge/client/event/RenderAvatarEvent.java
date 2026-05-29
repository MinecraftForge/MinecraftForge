/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when a Avatar is being rendered, typically a player or a mannequin.
 * See the two subclasses for listening for before and after rendering.
 *
 * @see RenderAvatarEvent.Pre
 * @see RenderAvatarEvent.Post
 * @see AvatarRenderer
 */
public sealed interface RenderAvatarEvent {
    AvatarRenderState getState();

    /**
     * {@return the player entity renderer}
     */
    AvatarRenderer<?> getRenderer();

    @SuppressWarnings("unchecked")
    default <AvatarlikeEntity extends Avatar & ClientAvatarEntity> AvatarRenderer<AvatarlikeEntity> getRendererTyped() {
        return (AvatarRenderer<AvatarlikeEntity>) getRenderer();
    }

    /**
     * {@return the pose stack used for rendering}
     */
    PoseStack getPoseStack();

    /**
     * {@return the collector you should render to}
     */
    SubmitNodeCollector getNodeCollector();

    /**
     * {@return State related to the current camera}
     *
     */
    CameraRenderState getCameraState();

    /**
     * Fired <b>before</b> the player is rendered.
     * This can be used for rendering additional effects or suppressing rendering.
     *
     * <p>This event is {@linkplain Cancellable cancellable}.
     * If this event is cancelled, then the player will not be rendered and the corresponding
     * {@link RenderAvatarEvent.Post} will not be fired.</p>
     *
     * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    record Pre(
            AvatarRenderState getState,
            AvatarRenderer<?> getRenderer,
            PoseStack getPoseStack,
            SubmitNodeCollector getNodeCollector,
            CameraRenderState getCameraState
    ) implements Cancellable, RecordEvent, RenderAvatarEvent {
        public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);

        @ApiStatus.Internal
        public Pre {}
    }

    /**
     * Fired <b>after</b> the player is rendered, if the corresponding {@link RenderAvatarEvent.Pre} is not cancelled.
     *
     * <p>This event is only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    record Post(
            AvatarRenderState getState,
            AvatarRenderer<?> getRenderer,
            PoseStack getPoseStack,
            SubmitNodeCollector getNodeCollector,
            CameraRenderState getCameraState
    ) implements RecordEvent, RenderAvatarEvent {
        public static final EventBus<Post> BUS = EventBus.create(Post.class);

        @ApiStatus.Internal
        public Post {}
    }
}
