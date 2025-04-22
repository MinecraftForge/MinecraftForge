/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when a {@link LivingEntity} is rendered.
 * See the two subclasses to listen for before and after rendering.
 *
 * <p>Despite this event's use of generic type parameters, this is not a {@link net.minecraftforge.eventbus.api.GenericEvent},
 * and should not be treated as such (such as using generic-specific listeners, which may cause a {@link ClassCastException}).</p>
 *
 * @param <T> the living entity that is being rendered
 * @param <M> the model for the living entity
 * @see RenderLivingEvent.Pre
 * @see RenderLivingEvent.Post
 * @see RenderPlayerEvent
 * @see LivingEntityRenderer
 */
public abstract sealed class RenderLivingEvent<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends Event {
    private final S state;
    private final LivingEntityRenderer<T, S, M> renderer;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;

    @ApiStatus.Internal
    protected RenderLivingEvent(S state, LivingEntityRenderer<T, S, M> renderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        this.state = state;
        this.renderer = renderer;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
    }

    /**
     * @return the living entity being rendered
     */
    public S getState() {
        return state;
    }

    /**
     * @return the renderer for the living entity
     */
    public LivingEntityRenderer<T, S, M> getRenderer() {
        return renderer;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack() {
        return poseStack;
    }

    /**
     * {@return the source of rendering buffers}
     */
    public MultiBufferSource getMultiBufferSource() {
        return multiBufferSource;
    }

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    public int getPackedLight() {
        return packedLight;
    }

    /**
     * Fired <b>before</b> an entity is rendered.
     * This can be used to render additional effects or suppress rendering.
     *
     * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the entity will not be rendered and the corresponding
     * {@link RenderLivingEvent.Post} will not be fired.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @param <T> the living entity that is being rendered
     * @param <M> the model for the living entity
     */
    @Cancelable
    public static final class Pre<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLivingEvent<T, S, M> {
        @ApiStatus.Internal
        public Pre(S state, LivingEntityRenderer<T, S, M> renderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
            super(state, renderer, poseStack, multiBufferSource, packedLight);
        }
    }

    /**
     * Fired <b>after</b> an entity is rendered, if the corresponding {@link RenderLivingEvent.Post} is not cancelled.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @param <T> the living entity that was rendered
     * @param <M> the model for the living entity
     */
    public static final class Post<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLivingEvent<T, S, M> {
        @ApiStatus.Internal
        public Post(S state, LivingEntityRenderer<T, S, M> renderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
            super(state, renderer, poseStack, multiBufferSource, packedLight);
        }
    }
}
