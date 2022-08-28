/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.ItemLike;
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
 * @see RegisterColorHandlersEvent.Block
 * @see RegisterColorHandlersEvent.Item
 */
public abstract class RegisterColorHandlersEvent extends Event implements IModBusEvent
{
    @ApiStatus.Internal
    protected RegisterColorHandlersEvent()
    {
    }

    /**
     * Fired for registering block color handlers.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class Block extends RegisterColorHandlersEvent
    {
        private final BlockColors blockColors;

        @ApiStatus.Internal
        public Block(BlockColors blockColors)
        {
            this.blockColors = blockColors;
        }

        /**
         * {@return the block colors registry}
         *
         * @see BlockColors#register(BlockColor, net.minecraft.world.level.block.Block...)
         */
        public BlockColors getBlockColors()
        {
            return blockColors;
        }

        /**
         * Registers a {@link BlockColor} instance for a set of blocks.
         *
         * @param blockColor The color provider
         * @param blocks     The blocks
         */
        public void register(BlockColor blockColor, net.minecraft.world.level.block.Block... blocks)
        {
            blockColors.register(blockColor, blocks);
        }
    }

    /**
     * Fired for registering item color handlers.
     *
     * <p>The block colors should only be used for referencing or delegating item colors to their respective block
     * colors. Use {@link RegisterColorHandlersEvent.Block} for registering your block color handlers.</p>
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class Item extends RegisterColorHandlersEvent
    {
        private final ItemColors itemColors;
        private final BlockColors blockColors;

        @ApiStatus.Internal
        public Item(ItemColors itemColors, BlockColors blockColors)
        {
            this.itemColors = itemColors;
            this.blockColors = blockColors;
        }

        /**
         * {@return the item colors registry}
         *
         * @see ItemColors#register(ItemColor, ItemLike...)
         */
        public ItemColors getItemColors()
        {
            return itemColors;
        }

        /**
         * {@return the block colors registry}
         * This should only be used for referencing or delegating item colors to their respective block colors.
         */
        public BlockColors getBlockColors()
        {
            return blockColors;
        }

        /**
         * Registers a {@link ItemColor} instance for a set of blocks.
         *
         * @param itemColor The color provider
         * @param items     The items
         */
        public void register(ItemColor itemColor, ItemLike... items)
        {
            itemColors.register(itemColor, items);
        }
    }

    /**
     * Allows registration of custom {@link ColorResolver} implementations to be used with
     * {@link net.minecraft.world.level.BlockAndTintGetter#getBlockTint(BlockPos, ColorResolver)}.
     */
    public static class ColorResolvers extends RegisterColorHandlersEvent
    {
        private final ImmutableList.Builder<ColorResolver> builder;

        @ApiStatus.Internal
        public ColorResolvers(ImmutableList.Builder<ColorResolver> builder)
        {
            this.builder = builder;
        }

        public void register(ColorResolver resolver)
        {
            this.builder.add(resolver);
        }

    }

}
