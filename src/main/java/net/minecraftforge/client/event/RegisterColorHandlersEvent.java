/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

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

        @Deprecated(forRemoval = true, since = "1.21.9")
        public static EventBus<Block> getBus(BusGroup modBusGroup) {
            return BUS;
        }

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
         * Registers a {@link BlockColor} instance for a set of blocks.
         *
         * @param blockColor The color provider
         * @param blocks     The blocks
         */
        @SuppressWarnings("deprecation")
        public void register(BlockColor blockColor, net.minecraft.world.level.block.Block... blocks) {
            getBlockColors.register(blockColor, blocks);
        }
    }

    /**
     * Allows registration of custom {@link ColorResolver} implementations to be used with
     * {@link net.minecraft.world.level.BlockAndTintGetter#getBlockTint(BlockPos, ColorResolver)}.
     */
    @NullMarked
    final class ColorResolvers extends MutableEvent implements SelfDestructing, RegisterColorHandlersEvent {
        public static final EventBus<ColorResolvers> BUS = EventBus.create(ColorResolvers.class);

        @Deprecated(forRemoval = true, since = "1.21.9")
        public static EventBus<ColorResolvers> getBus(BusGroup modBusGroup) {
            return BUS;
        }

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
