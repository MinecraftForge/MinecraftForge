/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftsingularity.common.MinecraftForge;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when a {@link LivingEntity} is rendered.
 * See the two subclasses to listen for before and after rendering.
 *
 * <p>Despite this event's use of generic type parameters, this is not a {@link net.minecraftsingularity.eventbus.api.GenericEvent},
 * and should not be treated as such (such as using generic-specific listeners, which may cause a {@link ClassCastException}).</p>
 *
 * @param <T> the living entity that is being rendered
 * @param <M> the model for the living entity
 * @see RenderLivingEvent.Pre
 * @see RenderLivingEvent.Post
 * @see RenderAvatarEvent
 * @see LivingEntityRenderer
 */
public sealed interface RenderLivingEvent<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    /**
     * @return the living entity being rendered
     */
    S getState();

    /**
     * @return the renderer for the living entity
     */
    LivingEntityRenderer<T, S, M> getRenderer();

    /**
     * {@return the pose stack used for rendering}
     */
    PoseStack getPoseStack();

    /**
     * {@return The render buffer that is collecting the model data. So that things can be batched}
     */
    SubmitNodeCollector getNodeCollector();

    /**
     * {@return Various state infomration about the camera}
     */
    CameraRenderState getCameraState();

    /**
     * Fired <b>before</b> an entity is rendered.
     * This can be used to render additional effects or suppress rendering.
     *
     * <p>This event is {@linkplain Cancellable cancelable}.
     * If this event is cancelled, then the entity will not be rendered and the corresponding
     * {@link RenderLivingEvent.Post} will not be fired.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @param <T> the living entity that is being rendered
     * @param <M> the model for the living entity
     */
    record Pre<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>(
            S getState,
            LivingEntityRenderer<T, S, M> getRenderer,
            PoseStack getPoseStack,
            SubmitNodeCollector getNodeCollector,
            CameraRenderState getCameraState
    ) implements Cancellable, RenderLivingEvent<T, S, M>, RecordEvent {
        public static final CancellableEventBus<Pre> BUS = CancellableEventBus.create(Pre.class);

        @ApiStatus.Internal
        public Pre {}
    }

    /**
     * Fired <b>after</b> an entity is rendered, if the corresponding {@link RenderLivingEvent.Post} is not cancelled.
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main singularity event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @param <T> the living entity that was rendered
     * @param <M> the model for the living entity
     */
    record Post<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>(
            S getState,
            LivingEntityRenderer<T, S, M> getRenderer,
            PoseStack getPoseStack,
            SubmitNodeCollector getNodeCollector,
            CameraRenderState getCameraState
    ) implements RecordEvent, RenderLivingEvent<T, S, M> {
        public static final EventBus<Post> BUS = EventBus.create(Post.class);

        @ApiStatus.Internal
        public Post {}
    }
}
