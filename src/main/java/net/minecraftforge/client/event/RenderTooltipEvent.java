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

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

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
    protected final List<? extends FormattedText> lines;
    protected final PoseStack poseStack;
    protected int x;
    protected int y;
    protected Font font;

    public RenderTooltipEvent(@Nonnull ItemStack stack, @Nonnull List<? extends FormattedText> lines, PoseStack poseStack, int x, int y, @Nonnull Font font)
    {
        this.stack = stack;
        this.lines = Collections.unmodifiableList(lines); // Leave editing to ItemTooltipEvent
        this.poseStack = poseStack;
        this.x = x;
        this.y = y;
        this.font = font;
    }

    /**
     * @return The stack which the tooltip is being rendered for. As tooltips can be drawn without itemstacks, this stack may be empty.
     */
    @Nonnull
    public ItemStack getItemStack()
    {
        return stack;
    }
    
    /**
     * The lines to be drawn. May change between {@link RenderTooltipEvent.Pre} and {@link RenderTooltipEvent.Post}.
     * 
     * @return An <i>unmodifiable</i> list of strings. Use {@link ItemTooltipEvent} to modify tooltip text.
     */
    @Nonnull
    public List<? extends FormattedText> getLines()
    {
        return lines;
    }

    /**
     * @return The MatrixStack of the current rendering context
     */
    public PoseStack getPoseStack()
    {
        return poseStack;
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
    public Font getFont()
    {
        return font;
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

        public Pre(@Nonnull ItemStack stack, @Nonnull List<? extends FormattedText> lines, PoseStack poseStack, int x, int y, int screenWidth, int screenHeight, int maxWidth, @Nonnull Font font)
        {
            super(stack, lines, poseStack, x, y, font);
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
        public void setFontRenderer(@Nonnull Font fr)
        {
            this.font = fr;
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
        
        public Post(@Nonnull ItemStack stack, @Nonnull List<? extends FormattedText> textLines, PoseStack poseStack,int x, int y, @Nonnull Font font, int width, int height)
        {
            super(stack, textLines, poseStack, x, y, font);
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
        public PostBackground(@Nonnull ItemStack stack, @Nonnull List<? extends FormattedText> textLines, PoseStack poseStack, int x, int y, @Nonnull Font font, int width, int height)
        {
            super(stack, textLines, poseStack, x, y, font, width, height);
        }
    }

    /**
     * This event is fired directly after the tooltip text is drawn, but before the GL state is reset.
     */
    public static class PostText extends Post
    {
        public PostText(@Nonnull ItemStack stack, @Nonnull List<? extends FormattedText> textLines, PoseStack poseStack, int x, int y, @Nonnull Font font, int width, int height)
        {
            super(stack, textLines, poseStack, x, y, font, width, height);
        }
    }
    
    /**
     * This event is fired when the colours for the tooltip background are determined. 
     */
    public static class Color extends RenderTooltipEvent
    {
        private final int originalBackground;
        private final int originalBorderStart;
        private final int originalBorderEnd;
        private int background;
        private int borderStart;
        private int borderEnd;

        public Color(@Nonnull ItemStack stack, @Nonnull List<? extends FormattedText> textLines, PoseStack poseStack, int x, int y, @Nonnull Font font, int background, int borderStart,
                int borderEnd)
        {
            super(stack, textLines, poseStack, x, y, font);
            this.originalBackground = background;
            this.originalBorderStart = borderStart;
            this.originalBorderEnd = borderEnd;
            this.background = background;
            this.borderStart = borderStart;
            this.borderEnd = borderEnd;
        }

        public int getBackground()
        {
            return background;
        }

        public void setBackground(int background)
        {
            this.background = background;
        }

        public int getBorderStart()
        {
            return borderStart;
        }

        public void setBorderStart(int borderStart)
        {
            this.borderStart = borderStart;
        }

        public int getBorderEnd()
        {
            return borderEnd;
        }

        public void setBorderEnd(int borderEnd)
        {
            this.borderEnd = borderEnd;
        }

        public int getOriginalBackground()
        {
            return originalBackground;
        }

        public int getOriginalBorderStart()
        {
            return originalBorderStart;
        }

        public int getOriginalBorderEnd()
        {
            return originalBorderEnd;
        }
    }
}
