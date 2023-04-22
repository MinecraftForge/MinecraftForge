/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.model.pose.IPose;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Allows users to register custom {@linkplain net.minecraftforge.client.model.pose.IPose poses}.
 *
 * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain Event.HasResult have a result}.
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public class RegisterPoseEvent extends Event implements IModBusEvent
{

    private final RegisterPoseConsumer registerPoseConsumer;

    @ApiStatus.Internal
    public RegisterPoseEvent(RegisterPoseConsumer registerPoseConsumer)
    {
        this.registerPoseConsumer = registerPoseConsumer;
    }

    /**
     * Register a Pose
     */
    public void register(IPose pose, ResourceLocation name, List<ResourceLocation> after, List<ResourceLocation> before)
    {
        registerPoseConsumer.registerPose(pose, name, after, before);
    }

    @FunctionalInterface
    @ApiStatus.Internal
    public interface RegisterPoseConsumer
    {
        void registerPose(IPose pose, ResourceLocation name, List<ResourceLocation> after, List<ResourceLocation> before);
    }
}
