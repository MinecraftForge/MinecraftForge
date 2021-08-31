package net.minecraftforge.client.event;

import net.minecraft.client.gui.Font;
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

    public RenderGuiItemDecorationsEvent(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String itemCountLabel) {
        this.font = font;
        this.stack = stack;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.itemCountLabel = itemCountLabel;
    }

    /**
     * Called before the overlay is rendered. Cancel to not render the overlay.
     */
    @Cancelable
    public static class Pre extends RenderGuiItemDecorationsEvent
    {
        public Pre(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String itemCountLabel) {
            super(font, stack, xOffset, yOffset, itemCountLabel);
        }
    }

    /**
     * Called after the overlay is rendered.
     */
    public static class Post extends RenderGuiItemDecorationsEvent
    {
        public Post(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String itemCountLabel) {
            super(font, stack, xOffset, yOffset, itemCountLabel);
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
