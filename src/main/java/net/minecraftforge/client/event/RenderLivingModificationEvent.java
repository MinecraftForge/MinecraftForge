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
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fired when a {@link LivingEntity} is rendered.
 *
 * <p>Despite this event's use of generic type parameters, this is not a {@link net.minecraftforge.eventbus.api.GenericEvent},
 * and should not be treated as such (such as using generic-specific listeners, which may cause a {@link ClassCastException}).</p>
 *
 * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * If this event is cancelled, then the entity will not be rendered and the corresponding pre and post callbacks will
 * not be called.
 *
 * Use {@link RenderLivingModificationEvent#addConsumers} to add additional rendering logic before or after the main
 * rendering has occurred.
 *
 * @param <T> the living entity that is being rendered
 * @param <M> the model for the living entity
 * @see RenderPlayerEvent
 * @see LivingEntityRenderer
 */
@Cancelable
public class RenderLivingModificationEvent<T extends LivingEntity, M extends EntityModel<T>> extends Event
{
    private final LivingEntityRenderer<T, M> renderer;
    private final T entity;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;

    private final float yaw;

    public static class RenderConsumers {
        @Nullable
        private Consumer<RenderLivingModificationEvent<?, ?>> pre;
        @Nullable
        private Consumer<RenderLivingModificationEvent<?, ?>> post;

        private RenderConsumers(@Nullable Consumer<RenderLivingModificationEvent<?, ?>> pre, @Nullable Consumer<RenderLivingModificationEvent<?, ?>> post)
        {
            this.pre = pre;
            this.post = post;
        }

        /**
         * Use this if you only need to add logic to before the entity rendering occurs
         * @param consumer the callback that will be called before rendering occurs
         * @return a render consumer object for use with {@link RenderLivingModificationEvent}
         */
        public static RenderConsumers Pre(Consumer<RenderLivingModificationEvent<?, ?>> consumer) {
            return new RenderConsumers(consumer, null);
        }

        /**
         * Use this if you only need to add logic to after the entity rendering occurs
         * @param consumer the callback that will be called after rendering occurs
         * @return a render consumer object for use with {@link RenderLivingModificationEvent}
         */
        public static RenderConsumers Post(Consumer<RenderLivingModificationEvent<?, ?>> consumer) {
            return new RenderConsumers(null, consumer);
        }

        /**
         * Use this you need to add logic to both before and after the entity rendering occurs. If you are manipulating
         * the pose stack across the rendering, make sure to call pop in your post.
         * @param pre the callback that will be called before rendering occurs
         * @param post the callback that will be called after rendering occurs
         * @return a render consumer object for use with {@link RenderLivingModificationEvent}
         */
        public static RenderConsumers Both(Consumer<RenderLivingModificationEvent<?, ?>> pre, Consumer<RenderLivingModificationEvent<?, ?>> post) {
            return new RenderConsumers(pre, post);
        }
    }

    private final List<RenderConsumers> consumers = new ArrayList<>();

    @ApiStatus.Internal
    public RenderLivingModificationEvent(T entity, LivingEntityRenderer<T, M> renderer, float yaw, float partialTick, PoseStack poseStack,
                                         MultiBufferSource multiBufferSource, int packedLight)
    {
        this.entity = entity;
        this.renderer = renderer;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.yaw = yaw;
    }

    /**
     * Add callback logic to run before or after the main rendering occurs. Replaces the old {@link RenderLivingEvent.Pre}
     * and {@link RenderLivingEvent.Post} events. Guarantees that all post callbacks will be called in the opposite order
     * of all pre callbacks.
     *
     * See {@link RenderConsumers#Pre}, {@link RenderConsumers#Post}, {@link RenderConsumers#Both}
     * @param consumer The Renderconsumers object holding your pre/post callbacks.
     */
    public void addConsumers(RenderConsumers consumer)
    {
        consumers.add(consumer);
    }

    void prePhase() {
        consumers.forEach(x -> {
            if (x.pre != null) {
                x.pre.accept(this);
            }
        });
    }

    void postPhase() {
        for (int i = consumers.size() - 1; i >= 0; i--) {
            var con = consumers.get(i);
            if (con.post != null) {
                con.post.accept(this);
            }
        }
    }

    /**
     * @return the living entity being rendered
     */
    public T getEntity()
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
     * @return the yaw of the entity being rendered
     */
    public float getYaw() {
        return yaw;
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

    @ApiStatus.Internal
    public static <T extends LivingEntity, M extends EntityModel<T>> void dispatchPre(RenderLivingModificationEvent<T, M> ev){
        ev.prePhase();
    }

    @ApiStatus.Internal
    public static <T extends LivingEntity, M extends EntityModel<T>> void dispatchPost(RenderLivingModificationEvent<T, M> ev){
        ev.postPhase();
    }
}
