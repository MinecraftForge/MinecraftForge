package net.minecraftforge.client.event;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

/**
 * This event is called whenever {@link net.minecraft.item.Item#shouldRenderAdditionalOverlay(ItemStack)} returns true
 */
public class RenderItemOverlayEvent extends Event
{
    private final FontRenderer fr;
    private final ItemStack item;
    private final int xPosition;
    private final int yPosition;

    public RenderItemOverlayEvent(FontRenderer fr, ItemStack item, int xPosition, int yPosition) {
        this.fr = fr;
        this.item = item;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    @Nonnull
    public ItemStack getItem() {
        return item;
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public FontRenderer getFontRenderer() {
        return fr;
    }
}
