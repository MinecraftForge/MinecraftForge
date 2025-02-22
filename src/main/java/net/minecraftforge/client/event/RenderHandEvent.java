/*
 * Copyright (stackrge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired before a hand is rendered in the first person view.
 *
 * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * If this event is cancelled, then the hand will not be rendered.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @param hand the hand being rendered
 * @param poseStack the pose stack used for rendering
 * @param multiBufferSource the source of rendering buffers
 * @param partialTick the partial tick
 * @param interpolatedPitch the interpolated pitch of the player entity
 * @param swingProgress the swing progress of the hand being rendered
 * @param equipProgress the progress of the equip animation, from {@code 0.0} to {@code 1.0}
 * @param itemStack the item stack to be rendered
 *
 * @see RenderArmEvent
 */
public record RenderHandEvent(
        InteractionHand hand,
        PoseStack poseStack,
        MultiBufferSource multiBufferSource,
        int packedLight,
        float partialTick,
        float interpolatedPitch,
        float swingProgress,
        float equipProgress,
        ItemStack itemStack
) implements Cancellable, RecordEvent {
    public static final CancellableEventBus<RenderHandEvent> BUS = CancellableEventBus.create(RenderHandEvent.class);

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    @Override
    public int packedLight() {
        return packedLight;
    }
}
