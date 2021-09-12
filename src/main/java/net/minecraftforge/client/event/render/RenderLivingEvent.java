/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.event.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when a {@link LivingEntity} is rendered.
 * See the two subclasses to listen for before and after rendering.
 *
 * @param <T> the living entity that is being rendered
 * @param <M> the model for the living entity
 *
 * @see RenderLivingEvent.Pre
 * @see RenderLivingEvent.Post
 * @see RenderPlayerEvent
 * @see LivingEntityRenderer
 */
public abstract class RenderLivingEvent<T extends LivingEntity, M extends EntityModel<T>> extends Event
{
    private final LivingEntity entity;
    private final LivingEntityRenderer<T, M> renderer;
    private final float renderTick;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final int light;

    public RenderLivingEvent(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float renderTick, PoseStack poseStack,
                             MultiBufferSource bufferSource, int light)
    {
        this.entity = entity;
        this.renderer = renderer;
        this.renderTick = renderTick;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.light = light;
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
        return renderTick;
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
    public MultiBufferSource getBufferSource()
    {
        return bufferSource;
    }

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    public int getLight()
    {
        return light;
    }

    /**
     * Fired <b>before</b> an entity is rendered.
     * This can be used to render additional effects or suppress rendering.
     *
     * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the entity will not be rendered and the corresponding
     * {@link RenderLivingEvent.Post} will not be fired. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @param <T> the living entity that is being rendered
     * @param <M> the model for the living entity
     */
    @Cancelable
    public static class Pre<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M>
    {
        public Pre(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light)
        {
            super(entity, renderer, partialTick, poseStack, bufferSource, light);
        }
    }

    /**
     * Fired <b>after</b> an entity is rendered, if the corresponding {@link RenderLivingEvent.Post} is not cancelled.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @param <T> the living entity that was rendered
     * @param <M> the model for the living entity
     */
    public static class Post<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M>
    {
        public Post(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light)
        {
            super(entity, renderer, partialTick, poseStack, bufferSource, light);
        }
    }
}
