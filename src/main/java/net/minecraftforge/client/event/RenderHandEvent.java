/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * whenever a hand is rendered in first person.
 * Canceling the event causes the hand to not render.
 */
@Cancelable
public class RenderHandEvent extends Event
{
    private final InteractionHand hand;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;
    private final float partialTicks;
    private final float interpolatedPitch;
    private final float swingProgress;
    private final float equipProgress;
    @Nonnull
    private final ItemStack stack;

    public RenderHandEvent(InteractionHand hand, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight,
                           float partialTicks, float interpolatedPitch,
                           float swingProgress, float equipProgress, @Nonnull ItemStack stack)
    {
        this.hand = hand;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.partialTicks = partialTicks;
        this.interpolatedPitch = interpolatedPitch;
        this.swingProgress = swingProgress;
        this.equipProgress = equipProgress;
        this.stack = stack;
    }

    public InteractionHand getHand()
    {
        return hand;
    }

    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    public MultiBufferSource getMultiBufferSource() {
        return multiBufferSource;
    }

    public int getPackedLight() {
        return packedLight;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

    /**
     * @return The interpolated pitch of the player entity
     */
    public float getInterpolatedPitch()
    {
        return interpolatedPitch;
    }

    /**
     * @return The swing progress of the hand being rendered
     */
    public float getSwingProgress()
    {
        return swingProgress;
    }

    /**
     * @return The progress of the equip animation. 1.0 is fully equipped.
     */
    public float getEquipProgress()
    {
        return equipProgress;
    }

    /**
     * @return The ItemStack to be rendered
     */
    @Nonnull
    public ItemStack getItemStack()
    {
        return stack;
    }
}
