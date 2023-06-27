/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.Internal
public class PoseSortingRegistry
{

    private static final Map<IPose, PosePriority> unsortedReplacementPoses = new HashMap<>();
    private static final Map<IPose, PosePriority> unsortedModifyingPoses = new HashMap<>();
    private static final List<IPose> sortedModifyingPoses = new ArrayList<>();
    private static final List<IPose> sortedReplacementPoses = new ArrayList<>();

    public static void registerPose(IPose pose, PosePriority priority, boolean isReplacementPose)
    {
        Map<IPose, PosePriority> poses = isReplacementPose ? unsortedReplacementPoses : unsortedModifyingPoses;
        if (poses.containsKey(pose))
            throw new IllegalStateException("Duplicate pose");
        poses.put(pose, priority);
    }

    public static boolean shouldRenderOtherPoses(LivingEntity livingEntity)
    {
        for (IPose pose : sortedReplacementPoses)
        {
            if (pose.isActive(livingEntity))
                return false;
        }
        return true;
    }

    public static <T extends LivingEntity> void updatePose(T livingEntity, PoseStack stack, EntityModel<T> model, float partialTicks, boolean isReplacementPoseActive)
    {
        if (!isReplacementPoseActive)
        {
            for (IPose pose : sortedReplacementPoses)
            {
                if (pose.isActive(livingEntity))
                {
                    pose.updatePose(livingEntity, stack, model, partialTicks);
                    return;
                }
            }
        }

        for (IPose pose : sortedModifyingPoses)
        {
            if (pose.isActive(livingEntity))
            {
                pose.updatePose(livingEntity, stack, model, partialTicks);
            }
        }
    }

    public static void init()
    {
        setPoseOrder(sortedModifyingPoses, unsortedModifyingPoses.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).toList());
        setPoseOrder(sortedReplacementPoses, unsortedReplacementPoses.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).toList());
    }

    private static void setPoseOrder(List<IPose> originalList, List<IPose> sortedPoses)
    {
        originalList.clear();
        originalList.addAll(sortedPoses);
    }
}
