/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;

public abstract class RenderLivingEvent<T extends LivingEntity, M extends EntityModel<T>> extends Event
{

    private final LivingEntity entity;
    private final LivingEntityRenderer<T, M> renderer;
    private final float partialRenderTick;
    private final PoseStack matrixStack;
    private final MultiBufferSource buffers;
    private final int light;

    public RenderLivingEvent(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialRenderTick, PoseStack matrixStack,
                             MultiBufferSource buffers, int light)
    {
        this.entity = entity;
        this.renderer = renderer;
        this.partialRenderTick = partialRenderTick;
        this.matrixStack = matrixStack;
        this.buffers = buffers;
        this.light = light;
    }

    public LivingEntity getEntity() { return entity; }
    public LivingEntityRenderer<T, M> getRenderer() { return renderer; }
    public float getPartialRenderTick() { return partialRenderTick; }
    public PoseStack getMatrixStack() { return matrixStack; }
    public MultiBufferSource getBuffers() { return buffers; }
    public int getLight() { return light; }

    @Cancelable
    public static class Pre<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M>
    {
        public Pre(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialRenderTick, PoseStack matrixStack, MultiBufferSource buffers, int light) {
            super(entity, renderer, partialRenderTick, matrixStack, buffers, light);
        }
    }

    public static class Post<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M>
    {
        public Post(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialRenderTick, PoseStack matrixStack, MultiBufferSource buffers, int light) {
            super(entity, renderer, partialRenderTick, matrixStack, buffers, light);
        }
    }
}
