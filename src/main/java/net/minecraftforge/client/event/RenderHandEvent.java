/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired before a hand is rendered in the first person view.
 *
 * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * If this event is cancelled, then the hand will not be rendered.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see RenderArmEvent
 */
@Cancelable
public class RenderHandEvent extends Event
{
    private final InteractionHand hand;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;
    private final float partialTick;
    private final float interpolatedPitch;
    private final float swingProgress;
    private final float equipProgress;
    private final ItemStack stack;

    @ApiStatus.Internal
    public RenderHandEvent(InteractionHand hand, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight,
                           float partialTick, float interpolatedPitch,
                           float swingProgress, float equipProgress, ItemStack stack)
    {
        this.hand = hand;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.partialTick = partialTick;
        this.interpolatedPitch = interpolatedPitch;
        this.swingProgress = swingProgress;
        this.equipProgress = equipProgress;
        this.stack = stack;
    }

    /**
     * {@return the hand being rendered}
     */
    public InteractionHand getHand()
    {
        return hand;
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
     * {@return the partial tick}
     */
    public float getPartialTick()
    {
        return partialTick;
    }

    /**
     * {@return the interpolated pitch of the player entity}
     */
    public float getInterpolatedPitch()
    {
        return interpolatedPitch;
    }

    /**
     * {@return the swing progress of the hand being rendered}
     */
    public float getSwingProgress()
    {
        return swingProgress;
    }

    /**
     * {@return the progress of the equip animation, from {@code 0.0} to {@code 1.0}}
     */
    public float getEquipProgress()
    {
        return equipProgress;
    }

    /**
     * {@return the item stack to be rendered}
     */
    public ItemStack getItemStack()
    {
        return stack;
    }
}
