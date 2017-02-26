package net.minecraftforge.client.event;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * A set of events which are fired at various points during tooltip rendering.
 * <p>
 * Can be used to change the rendering parameters, draw something extra, etc.
 * <p>
 * Do not use this event directly, use one of the subclasses:
 * <ul>
 * <li>{@link RenderTooltipEvent.Pre}</li>
 * <li>{@link RenderTooltipEvent.PostBackground}</li>
 * <li>{@link RenderTooltipEvent.PostText}</li>
 * </ul>
 */
public abstract class RenderTooltipEvent extends Event
{
    @Nonnull
    protected final ItemStack stack;
    protected final List<String> lines;
    protected int x;
    protected int y;
    protected FontRenderer fr;

    public RenderTooltipEvent(@Nonnull ItemStack stack, @Nonnull List<String> lines, int x, int y, @Nonnull FontRenderer fr)
    {
        this.stack = stack;
        this.lines = Collections.unmodifiableList(lines); // Leave editing to ItemTooltipEvent
        this.x = x;
        this.y = y;
        this.fr = fr;
    }

    /**
     * @return The stack which the tooltip is being rendered for. As tooltips can be drawn without itemstacks, this stack may be empty.
     */
    @Nonnull
    public ItemStack getStack()
    {
        return stack;
    }
    
    /**
     * The lines to be drawn. May change between {@link RenderTooltipEvent.Pre} and {@link RenderTooltipEvent.Post}.
     * 
     * @return An <i>unmodifiable</i> list of strings. Use {@link ItemTooltipEvent} to modify tooltip text.
     */
    @Nonnull
    public List<String> getLines()
    {
        return lines;
    }

    /**
     * @return The X position of the tooltip box. By default, the mouse X position.
     */
    public int getX()
    {
        return x;
    }

    /**
     * @return The Y position of the tooltip box. By default, the mouse Y position.
     */
    public int getY()
    {
        return y;
    }
    
    /**
     * @return The {@link FontRenderer} instance the current render is using.
     */
    @Nonnull
    public FontRenderer getFontRenderer()
    {
        return fr;
    }

    /**
     * This event is fired before any tooltip calculations are done. It provides setters for all aspects of the tooltip, so the final render can be modified.
     * <p>
     * This event is {@link Cancelable}.
     */
    @Cancelable
    public static class Pre extends RenderTooltipEvent
    {
        private int screenWidth;
        private int screenHeight;
        private int maxWidth;

        public Pre(@Nonnull ItemStack stack, @Nonnull List<String> lines, int x, int y, int screenWidth, int screenHeight, int maxWidth, @Nonnull FontRenderer fr)
        {
            super(stack, lines, x, y, fr);
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.maxWidth = maxWidth;
        }

        public int getScreenWidth()
        {
            return screenWidth;
        }

        public void setScreenWidth(int screenWidth)
        {
            this.screenWidth = screenWidth;
        }

        public int getScreenHeight()
        {
            return screenHeight;
        }

        public void setScreenHeight(int screenHeight)
        {
            this.screenHeight = screenHeight;
        }

        /**
         * @return The max width the tooltip can be. Defaults to -1 (unlimited).
         */
        public int getMaxWidth()
        {
            return maxWidth;
        }

        /**
         * Sets the max width of the tooltip. Use -1 for unlimited.
         */
        public void setMaxWidth(int maxWidth)
        {
            this.maxWidth = maxWidth;
        }
        
        /**
         * Sets the {@link FontRenderer} to be used to render text.
         */
        public void setFontRenderer(@Nonnull FontRenderer fr)
        {
            this.fr = fr;
        }

        /**
         * Set the X origin of the tooltip.
         */
        public void setX(int x)
        {
            this.x = x;
        }

        /**
         * Set the Y origin of the tooltip.
         */
        public void setY(int y)
        {
            this.y = y;
        }
    }

    /**
     * Events inheriting from this class are fired at different stages during the tooltip rendering.
     * <p>
     * Do not use this event directly, use one of its subclasses:
     * <ul>
     * <li>{@link RenderTooltipEvent.PostBackground}</li>
     * <li>{@link RenderTooltipEvent.PostText}</li>
     * </ul>
     */
    protected static abstract class Post extends RenderTooltipEvent
    {
        private final int width;
        private final int height;
        
        public Post(@Nonnull ItemStack stack, @Nonnull List<String> textLines, int x, int y, @Nonnull FontRenderer fr, int width, int height)
        {
            super(stack, textLines, x, y, fr);
            this.width = width;
            this.height = height;
        }

        /**
         * @return The width of the tooltip box. This is the width of the <i>inner</i> box, not including the border.
         */
        public int getWidth()
        {
            return width;
        }

        /**
         * @return The height of the tooltip box. This is the height of the <i>inner</i> box, not including the border.
         */
        public int getHeight()
        {
            return height;
        }
    }
    
    /**
     * This event is fired directly after the tooltip background is drawn, but before any text is drawn.
     */
    public static class PostBackground extends Post 
    {
        public PostBackground(@Nonnull ItemStack stack, @Nonnull List<String> textLines, int x, int y, @Nonnull FontRenderer fr, int width, int height)
            { super(stack, textLines, x, y, fr, width, height); }
    }

    /**
     * This event is fired directly after the tooltip text is drawn, but before the GL state is reset.
     */
    public static class PostText extends Post
    {
        public PostText(@Nonnull ItemStack stack, @Nonnull List<String> textLines, int x, int y, @Nonnull FontRenderer fr, int width, int height)
            { super(stack, textLines, x, y, fr, width, height); }
    }
}
