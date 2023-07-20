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
 * @see RenderLivingEvent.AboutToRender
 * @see RenderLivingEvent.Pre
 * @see RenderLivingEvent.Post
 * @see RenderPlayerEvent
 * @see LivingEntityRenderer
 */
public abstract class RenderLivingEvent<T extends LivingEntity, M extends EntityModel<T>> extends Event
{
    private final LivingEntity entity;
    private final LivingEntityRenderer<T, M> renderer;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;

    @ApiStatus.Internal
    protected RenderLivingEvent(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack,
                                MultiBufferSource multiBufferSource, int packedLight)
    {
        this.entity = entity;
        this.renderer = renderer;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
    }

    /**
     * @return the living entity being rendered
     */
    public LivingEntity getEntity()
    {
        return entity;
    }

    /**
     * @return the renderer for the living entity
     */
    public LivingEntityRenderer<T, M> getRenderer()
    {
        return renderer;
    }

    /**
     * {@return the partial tick}
     */
    public float getPartialTick()
    {
        return partialTick;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    /**
     * {@return the source of rendering buffers}
     */
    public MultiBufferSource getMultiBufferSource()
    {
        return multiBufferSource;
    }

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    public int getPackedLight()
    {
        return packedLight;
    }

    /**
     * Fired <b>before</b> an entity is rendered.
     * This should be used for suppressing rendering.
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the player will not be rendered.
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @Cancelable
    public static class AboutToRender<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M>
    {
        @ApiStatus.Internal
        public AboutToRender(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight)
        {
            super(entity, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }


    /**
     * Fired <b>before</b> an entity is rendered. This can be used for rendering additional effects.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     *
     * @param <T> the living entity that is being rendered
     * @param <M> the model for the living entity
     */
    public static class Pre<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M>
    {
        @ApiStatus.Internal
        public Pre(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight)
        {
            super(entity, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }

        /**
         * The functionality to cancel the living render has been moved to {@link RenderLivingEvent.AboutToRender}
         * @param cancel The new canceled value
         */
        @Deprecated(since = "1.20.1", forRemoval = true)
        @Override
        public void setCanceled(boolean cancel) {
            super.setCanceled(cancel);
        }
    }

    /**
     * Fired <b>after</b> an entity is rendered. This event can be used to render additional effects. If you have
     * pushed to the pose stack in your pre event don't forget to pop here if you have not done so already.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class Post<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M>
    {
        @ApiStatus.Internal
        public Post(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight)
        {
            super(entity, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }
}
