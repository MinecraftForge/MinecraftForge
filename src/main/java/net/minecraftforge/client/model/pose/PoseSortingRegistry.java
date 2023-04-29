/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.pose;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.Internal
public class PoseSortingRegistry
{
    private static final BiMap<ResourceLocation, IPose> poses = HashBiMap.create();
    private static final Multimap<ResourceLocation, ResourceLocation> edges = HashMultimap.create();


    private static final List<IPose> sortedPoses = new ArrayList<>();

    private static IPose registerPose(IPose pose, ResourceLocation name, List<ResourceLocation> after, List<ResourceLocation> before)
    {
        if (poses.containsKey(name))
            throw new IllegalStateException("Duplicate pose name " + name);

        processPose(pose, name, after, before);
        return pose;
    }

    public static boolean shouldRenderOtherPoses(LivingEntity livingEntity) {
        for (IPose pose : sortedPoses)
        {
            if (pose.isActive(livingEntity) && !pose.shouldOtherPosesActivate())
                return false;
        }
        return true;
    }

    public static <T extends LivingEntity> void updatePose(T livingEntity, PoseStack stack, EntityModel<T> model, float partialTicks) {
        for (IPose pose : sortedPoses)
        {
            if (pose.isActive(livingEntity))
            {
                pose.updatePose(livingEntity, stack, model, partialTicks);
                if (!pose.shouldOtherPosesActivate())
                    return;
            }
        }
    }

    private static void processPose(IPose pose, ResourceLocation name, List<ResourceLocation> afters, List<ResourceLocation> befores)
    {
        poses.put(name, pose);
        for(ResourceLocation after : afters)
        {
            edges.put(after, name);
        }
        for(ResourceLocation before : befores)
        {
            edges.put(name, before);
        }
    }


    @SuppressWarnings("UnstableApiUsage")
    private static void recalculatePoses()
    {
        final MutableGraph<IPose> graph = GraphBuilder.directed().nodeOrder(ElementOrder.<IPose>insertion()).build();

        for(IPose pose : poses.values())
        {
            graph.addNode(pose);
        }
        edges.forEach((key, value) -> {
            if (poses.containsKey(key) && poses.containsKey(value))
                graph.putEdge(poses.get(key), poses.get(value));
        });
        List<IPose> poseList = TopologicalSort.topologicalSort(graph, null);

        setPoseOrder(poseList);
    }

    public static void init()
    {
        EntityRenderersEvent.RegisterPoseEvent event = new EntityRenderersEvent.RegisterPoseEvent(PoseSortingRegistry::registerPose);
        ModLoader.get().postEventWithWrapInModOrder(event, (mc, e) -> ModLoadingContext.get().setActiveContainer(mc), (mc, e) -> ModLoadingContext.get().setActiveContainer(null));
        recalculatePoses();
    }

    private static void setPoseOrder(List<IPose> poseList)
    {
        sortedPoses.clear();
        sortedPoses.addAll(poseList);
    }
}
