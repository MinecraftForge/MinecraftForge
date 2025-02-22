/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.fml.LogicalSide;

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
 *
 * @param poseStack the pose stack used for rendering
 * @param multiBufferSource the source of rendering buffers
 * @param arm the arm being rendered
 */
public record RenderArmEvent(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, HumanoidArm arm)
        implements Cancellable, RecordEvent {
    public static final CancellableEventBus<RenderArmEvent> BUS = CancellableEventBus.create(RenderArmEvent.class);

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
