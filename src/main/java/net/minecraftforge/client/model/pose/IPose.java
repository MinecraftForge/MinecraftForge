/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;


/**
 * An IPose that is used to modify the rendering of {@linkplain LivingEntity LivingEntities}.
 * Register it using {@linkplain net.minecraftforge.client.event.EntityRenderersEvent.RegisterPoseEvent#register(IPose, ResourceLocation, List, List) RegisterPoseEvent#register}.
 */
public interface IPose
{
    /**
     *
     * @return if this pose should be applied to this entity
     */
    boolean isActive(LivingEntity entity);

    /**
     *
     * this is called right before rendering, modify the PoseStack or the EntityModel here
     */

    <T extends LivingEntity> void updatePose(T entity, PoseStack stack, EntityModel<T> model, float partialTicks);
}
