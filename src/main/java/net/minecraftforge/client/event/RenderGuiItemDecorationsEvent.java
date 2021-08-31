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

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * Called when an itemStacks overlay, such as a durability bar or a stack counter is rendered.
 * Usable to render or remove additional Decorations such as durability bars, counters etc.
 */
public class RenderGuiItemDecorationsEvent extends Event {
    private final Font font;
    private final ItemStack stack;
    private final int xOffset;
    private final int yOffset;
    private final String itemCountLabel;
    private final TextureManager textureManager;

    public RenderGuiItemDecorationsEvent(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String itemCountLabel, TextureManager textureManager) {
        this.font = font;
        this.stack = stack;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.itemCountLabel = itemCountLabel;
        this.textureManager = textureManager;
    }

    /**
     * Called before the overlay is rendered. Cancel to not render the overlay.
     */
    @Cancelable
    public static class Pre extends RenderGuiItemDecorationsEvent
    {
        public Pre(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String itemCountLabel, TextureManager textureManager) {
            super(font, stack, xOffset, yOffset, itemCountLabel, textureManager);
        }
    }

    /**
     * Called after the overlay is rendered.
     */
    public static class Post extends RenderGuiItemDecorationsEvent
    {
        public Post(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String itemCountLabel, TextureManager textureManager) {
            super(font, stack, xOffset, yOffset, itemCountLabel, textureManager);
        }
    }

    /**
     * Returns the Font with which the item count is rendered
     */
    public Font getFont() {
        return font;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public String getItemCountLabel() {
        return itemCountLabel;
    }
}
