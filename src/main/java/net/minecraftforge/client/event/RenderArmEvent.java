/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This is a more targeted version of {@link RenderHandEvent} event that is fired specifically when
 * a player's arm is being rendered in first person, and should be used instead if the desired
 * outcome is just to replace the rendering of the arm, such as to make armor render on it or
 * instead of it.
 *
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * Canceling the event causes the arm to not render.
 */
@Cancelable
public class RenderArmEvent extends Event
{
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;
    private final AbstractClientPlayer player;
    private final HumanoidArm arm;

    public RenderArmEvent(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, AbstractClientPlayer player, HumanoidArm arm)
    {
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.player = player;
        this.arm = arm;
    }

    /**
     * @return The arm being rendered.
     */
    public HumanoidArm getArm()
    {
        return arm;
    }

    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    public MultiBufferSource getMultiBufferSource()
    {
        return multiBufferSource;
    }

    public int getPackedLight()
    {
        return packedLight;
    }

    /**
     * @return the client player that is having their arm rendered. In general this will be the same as {@link net.minecraft.client.Minecraft#player}.
     */
    public AbstractClientPlayer getPlayer()
    {
        return player;
    }
}
