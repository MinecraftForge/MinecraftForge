/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.event;

import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

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
