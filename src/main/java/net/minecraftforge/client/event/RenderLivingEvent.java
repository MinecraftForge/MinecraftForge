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

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

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

    public LivingEntity getEntity()
    {
        return entity;
    }

    public LivingEntityRenderer<T, M> getRenderer()
    {
        return renderer;
    }

    public float getRenderTick()
    {
        return renderTick;
    }

    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    public MultiBufferSource getBufferSource()
    {
        return bufferSource;
    }

    public int getLight()
    {
        return light;
    }

    @Cancelable
    public static class Pre<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M>
    {
        public Pre(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light)
        {
            super(entity, renderer, partialTick, poseStack, bufferSource, light);
        }
    }

    public static class Post<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M>
    {
        public Post(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light)
        {
            super(entity, renderer, partialTick, poseStack, bufferSource, light);
        }
    }
}
