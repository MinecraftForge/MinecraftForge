/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired before a block texture will be overlaid on the player's view.
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @param getPlayer The player which the overlay will apply to
 * @param getPoseStack The pose stack used for rendering
 * @param getOverlayType The type of the overlay
 * @param getBlockState The block which the overlay is gotten from
 * @param getBlockPos The position of the block which the overlay is gotten from
 */
public record RenderBlockScreenEffectEvent(
        Player getPlayer,
        PoseStack getPoseStack,
        OverlayType getOverlayType,
        BlockState getBlockState,
        BlockPos getBlockPos
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

    @ApiStatus.Internal
    public RenderBlockScreenEffectEvent {}
}
