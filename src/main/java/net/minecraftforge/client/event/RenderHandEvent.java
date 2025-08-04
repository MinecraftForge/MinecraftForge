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
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired before a hand is rendered in the first person view.
 *
 * <p>This event is {@linkplain Cancellable cancellable}. If this event is cancelled, then the hand will not be rendered.</p>
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @param getHand The hand being rendered
 * @param getPoseStack The pose stack used for rendering
 * @param getMultiBufferSource The source of rendering buffers
 * @param getPartialTick The partial tick
 * @param getInterpolatedPitch The interpolated pitch of the player entity
 * @param getSwingProgress The swing progress of the hand being rendered
 * @param getEquipProgress The progress of the equip animation, from {@code 0.0} to {@code 1.0}
 * @param getItemStack The item stack to be rendered
 *
 * @see RenderArmEvent
 */
public record RenderHandEvent(
        InteractionHand getHand,
        PoseStack getPoseStack,
        MultiBufferSource getMultiBufferSource,
        int getPackedLight,
        float getPartialTick,
        float getInterpolatedPitch,
        float getSwingProgress,
        float getEquipProgress,
        ItemStack getItemStack
) implements Cancellable, RecordEvent {
    public static final CancellableEventBus<RenderHandEvent> BUS = CancellableEventBus.create(RenderHandEvent.class);

    @ApiStatus.Internal
    public RenderHandEvent {}

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    public int getPackedLight() {
        return getPackedLight;
    }
}
