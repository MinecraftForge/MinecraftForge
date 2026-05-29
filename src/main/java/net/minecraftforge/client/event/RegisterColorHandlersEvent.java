/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * Fired for registering block and item color handlers at the appropriate time.
 * See the two subclasses for registering block or item color handlers.
 *
 * <p>These events are fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see RegisterColorHandlersEvent.Block
 * @see RegisterColorHandlersEvent.ColorResolvers
 */
public sealed interface RegisterColorHandlersEvent {
    /**
     * Fired for registering block color handlers.
     *
     * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    record Block(BlockColors getBlockColors) implements RecordEvent, RegisterColorHandlersEvent {
        public static final EventBus<Block> BUS = EventBus.create(Block.class);

        @ApiStatus.Internal
        public Block {}

        /**
         * {@return the block colors registry}
         *
         * @see BlockColors#register(BlockColor, net.minecraft.world.level.block.Block...)
         */
        public BlockColors getBlockColors() {
            return getBlockColors;
        }

        /**
         * Registers {@link BlockTintSource} instances for a set of blocks.
         *
         * @param sources The tint sources
         * @param blocks  The blocks
         */
        @SuppressWarnings("deprecation")
        public void register(List<BlockTintSource> sources, net.minecraft.world.level.block.Block... blocks) {
            getBlockColors.register(sources, blocks);
        }
    }

    /**
     * Allows registration of custom {@link ColorResolver} implementations to be used with
     * {@link net.minecraft.world.level.BlockAndTintGetter#getBlockTint(BlockPos, ColorResolver)}.
     */
    @NullMarked
    final class ColorResolvers extends MutableEvent implements SelfDestructing, RegisterColorHandlersEvent {
        public static final EventBus<ColorResolvers> BUS = EventBus.create(ColorResolvers.class);

        private final ArrayList<ColorResolver> builder;

        @ApiStatus.Internal
        public ColorResolvers(ArrayList<ColorResolver> builder) {
            this.builder = builder;
        }

        public void register(ColorResolver resolver) {
            this.builder.add(resolver);
        }
    }
}
