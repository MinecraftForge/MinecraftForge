/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired for registering block and item color handlers at the appropriate time.
 * See the two subclasses for registering block or item color handlers.
 *
 * <p>These events are fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see Block
 * @see Item
 */
public abstract class RegisterColorHandlersEvent extends Event implements IModBusEvent {
    @ApiStatus.Internal
    protected RegisterColorHandlersEvent() {}

    /**
     * Fired for registering block color handlers.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class Block extends RegisterColorHandlersEvent {
        private final BlockColors blockColors;

        @ApiStatus.Internal
        public Block(BlockColors blockColors) {
            this.blockColors = blockColors;
        }

        /**
         * {@return the block colors registry}
         *
         * @see BlockColors#register(BlockColor, net.minecraft.world.level.block.Block...)
         */
        public BlockColors getBlockColors() {
            return blockColors;
        }

        /**
         * Registers a {@link BlockColor} instance for a set of blocks.
         *
         * @param blockColor The color provider
         * @param blocks     The blocks
         */
        @SuppressWarnings("deprecation")
        public void register(BlockColor blockColor, net.minecraft.world.level.block.Block... blocks) {
            blockColors.register(blockColor, blocks);
        }
    }

    /**
     * Fired for registering custom {@linkplain ItemTintSource item tint sources}.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class Item extends RegisterColorHandlersEvent
    {

        @ApiStatus.Internal
        public Item() {}

        /**
         * Register an {@linkplain ItemTintSource item tint source} {@linkplain MapCodec map codec} with a specific id.
         */
        public void register(ResourceLocation id, MapCodec<? extends ItemTintSource> codec) {
            ItemTintSources.ID_MAPPER.put(id, codec);
        }
    }

    /**
     * Allows registration of custom {@link ColorResolver} implementations to be used with
     * {@link net.minecraft.world.level.BlockAndTintGetter#getBlockTint(BlockPos, ColorResolver)}.
     */
    public static class ColorResolvers extends RegisterColorHandlersEvent {
        private final ImmutableList.Builder<ColorResolver> builder;

        @ApiStatus.Internal
        public ColorResolvers(ImmutableList.Builder<ColorResolver> builder) {
            this.builder = builder;
        }

        public void register(ColorResolver resolver) {
            this.builder.add(resolver);
        }
    }
}
