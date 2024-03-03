/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * Use these events to register block/item
 * color handlers at the appropriate time.
 */
public abstract class ColorHandlerEvent extends Event implements IModBusEvent
{
    public static class Block extends ColorHandlerEvent
    {
        private final BlockColors blockColors;

        public Block(BlockColors blockColors)
        {
            this.blockColors = blockColors;
        }

        public BlockColors getBlockColors()
        {
            return blockColors;
        }
    }

    public static class Item extends ColorHandlerEvent
    {
        private final ItemColors itemColors;
        private final BlockColors blockColors;

        public Item(ItemColors itemColors, BlockColors blockColors)
        {
            this.itemColors = itemColors;
            this.blockColors = blockColors;
        }

        public ItemColors getItemColors()
        {
            return itemColors;
        }

        public BlockColors getBlockColors()
        {
            return blockColors;
        }
    }
}
