/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired before a block texture will be overlaid on the player's view.
 *
 * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * If this event is cancelled, then the overlay will not be rendered.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @param player the player which the overlay will apply to
 * @param poseStack the pose stack used for rendering
 * @param overlayType the type of the overlay
 * @param blockState the block state which the overlay is gotten from
 * @param blockPos the position of the block which the overlay is gotten from
 */
public record RenderBlockScreenEffectEvent(
        Player player,
        PoseStack poseStack,
        OverlayType overlayType,
        BlockState blockState,
        BlockPos blockPos
) implements Cancellable, RecordEvent {
    public static final CancellableEventBus<RenderBlockScreenEffectEvent> BUS = CancellableEventBus.create(RenderBlockScreenEffectEvent.class);

    /**
     * The type of the block overlay to be rendered.
     *
     * @see RenderBlockScreenEffectEvent
     */
    public enum OverlayType {
        /**
         * The type of the overlay when the player is burning / on fire.
         */
        FIRE,
        /**
         * The type of overlay when the player is suffocating inside a solid block.
         */
        BLOCK,
        /**
         * The type of overlay when the player is underwater.
         */
        WATER
    }
}
