/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired before the player's arm is rendered in first person. This is a more targeted version of {@link RenderHandEvent},
 * and can be used to replace the rendering of the player's arm, such as for rendering armor on the arm or outright
 * replacing the arm with armor.
 *
 * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * If this event is cancelled, then the arm will not be rendered.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
@Cancelable
public class RenderArmEvent extends Event
{
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;
    private final AbstractClientPlayer player;
    private final HumanoidArm arm;

    @ApiStatus.Internal
    public RenderArmEvent(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, AbstractClientPlayer player, HumanoidArm arm)
    {
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.player = player;
        this.arm = arm;
    }

    /**
     * {@return the arm being rendered}
     */
    public HumanoidArm getArm()
    {
        return arm;
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
    public MultiBufferSource getMultiBufferSource()
    {
        return multiBufferSource;
    }

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    public int getPackedLight()
    {
        return packedLight;
    }

    /**
     * {@return the client player that is having their arm rendered} In general, this will be the same as
     * {@link net.minecraft.client.Minecraft#player}.
     */
    public AbstractClientPlayer getPlayer()
    {
        return player;
    }
}
