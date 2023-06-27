/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;

/**
 * An IPose that is used to modify the rendering of {@linkplain LivingEntity LivingEntities}.
 * Register it using {@linkplain net.minecraftforge.client.event.EntityRenderersEvent.AddLayers#registerPose(IPose, PosePriority, boolean) RegisterPoseEvent#registerPose}.
 * During registering you have the option to sort it using a priority. You can also set it to be a replacement pose, which causes all vanilla posing and most of the posestack manipulation to stop. If a replacement pose is active, no other posing is applied by vanilla or other modded poses. Non replacing poses are applied in order, later poses can override changes made to the model of previous poses.
 */
public interface IPose
{
    /**
     * @return if this pose should be applied to this entity
     */
    boolean isActive(LivingEntity entity);

    /**
     * this is called right before rendering, modify the PoseStack and the EntityModel here. Is only called if {@linkplain IPose#isActive(LivingEntity) isActive} returns true
     */

    <T extends LivingEntity> void updatePose(T entity, PoseStack stack, EntityModel<T> model, float partialTicks);
}
