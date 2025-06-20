/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired before the player's arm is rendered in first person. This is a more targeted version of {@link RenderHandEvent},
 * and can be used to replace the rendering of the player's arm, such as for rendering armor on the arm or outright
 * replacing the arm with armor.
 *
 * <p>This event is {@linkplain Cancellable cancellable}. If this event is cancelled, then the arm will not be rendered.</p>
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @param getPoseStack The pose stack used for rendering
 * @param getMultiBufferSource The source of rendering buffers
 * @param getArm The arm being rendered
 */
public record RenderArmEvent(
        PoseStack getPoseStack,
        MultiBufferSource getMultiBufferSource,
        int getPackedLight,
        HumanoidArm getArm
) implements Cancellable, RecordEvent {
    public static final CancellableEventBus<RenderArmEvent> BUS = CancellableEventBus.create(RenderArmEvent.class);

    @ApiStatus.Internal
    public RenderArmEvent {}

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    public int getPackedLight() {
        return getPackedLight;
    }
}
