/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * An ArmPose that can be defined by the user.
 * Register one by creating a custom {@link net.minecraft.client.model.HumanoidModel.ArmPose}
 * and returning it in {@link IClientItemExtensions#getArmPose(LivingEntity, InteractionHand, ItemStack)}.
 */
@FunctionalInterface
public interface IArmPoseTransformer
{

    /**
     * This method should be used to apply all wanted transformations to the player when the ArmPose is active.
     * You can use {@link LivingEntity#getTicksUsingItem()} and {@link LivingEntity#getUseItemRemainingTicks()} for moving animations.
     *
     * @param model   The humanoid model
     * @param entity  The humanoid entity
     * @param arm Arm to pose
     */
    void applyTransform(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm);

}