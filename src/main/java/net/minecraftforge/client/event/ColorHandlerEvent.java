/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Fired for registering block and item color handlers at the appropriate time.
 * See the two subclasses for registering block or item color handlers.
 *
 * <p>These events are fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see ColorHandlerEvent.Block
 * @see ColorHandlerEvent.Item
 */
public abstract class ColorHandlerEvent extends Event implements IModBusEvent
{
    /**
     * @hidden This should only be invoked by subclasses.
     */
    public ColorHandlerEvent()
    {
    }

    /**
     * Fired for registering block color handlers.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class Block extends ColorHandlerEvent
    {
        private final BlockColors blockColors;

        /**
         * @hidden This should only be invoked by the client hook.
         * @see ForgeHooksClient#onBlockColorsInit(BlockColors)
         */
        public Block(BlockColors blockColors)
        {
            this.blockColors = blockColors;
        }

        /**
         * {@return the block colors registry}
         * @see BlockColors#register(BlockColor, net.minecraft.world.level.block.Block...)
         */
        public BlockColors getBlockColors()
        {
            return blockColors;
        }
    }

    /**
     * Fired for registering item color handlers.
     *
     * <p>The block colors should only be used for referencing or delegating item colors to their respective block
     * colors. Use {@link ColorHandlerEvent.Block} for registering your block color handlers. </p>
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class Item extends ColorHandlerEvent
    {
        private final ItemColors itemColors;
        private final BlockColors blockColors;

        /**
         * @hidden
         * @see ForgeHooksClient#onItemColorsInit(ItemColors, BlockColors)
         */
        public Item(ItemColors itemColors, BlockColors blockColors)
        {
            this.itemColors = itemColors;
            this.blockColors = blockColors;
        }

        /**
         * {@return the item colors registry}
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
    }
}
