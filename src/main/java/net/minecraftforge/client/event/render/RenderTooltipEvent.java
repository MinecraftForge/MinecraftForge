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

package net.minecraftforge.client.event.render;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmlclient.gui.GuiUtils;

/**
 * Fired when a tooltip is rendering.
 * See the various subclasses for listening to specific events.
 *
 * @see RenderTooltipEvent.Pre
 * @see RenderTooltipEvent.Post
 * @see RenderTooltipEvent.Color
 * @see GuiUtils#drawHoveringText(ItemStack, PoseStack, List, int, int, int, int, int, int, int, int, Font)
 */
public abstract class RenderTooltipEvent extends Event
{
    protected final ItemStack stack;
    protected final List<? extends FormattedText> lines;
    protected final PoseStack poseStack;
    protected int x;
    protected int y;
    protected Font font;

    public RenderTooltipEvent(ItemStack stack, List<? extends FormattedText> lines, PoseStack poseStack, int x, int y, Font font)
    {
        this.stack = stack;
        this.lines = Collections.unmodifiableList(lines); // Leave editing to ItemTooltipEvent
        this.poseStack = poseStack;
        this.x = x;
        this.y = y;
        this.font = font;
    }

    /**
     * {@return the stack which the tooltip is being rendered for, may be {@link ItemStack#EMPTY}}
     */
    public ItemStack getItemStack()
    {
        return stack;
    }

    /**
     * {@return the unmodifiable list of text on the tooltip}
     * May change between {@link RenderTooltipEvent.Pre} and {@link RenderTooltipEvent.Post}.
     *
     * <p><em>Use {@link ItemTooltipEvent} to modify tooltip text. </em></p>
     */
    public List<? extends FormattedText> getLines()
    {
        return lines;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    /**
     * {@return the X position of the tooltip box}
     */
    public int getX()
    {
        return x;
    }

    /**
     * {@return the Y position of the tooltip box}
     */
    public int getY()
    {
        return y;
    }

    /**
     * {@return The font used to render the text}
     */
    public Font getFont()
    {
        return font;
    }

    /**
     * Fired <b>before</b> the tooltip is rendered.
     * This can be used to modify the positioning of the tooltip.
     *
     * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the tooltip will not be rendered and the corresponding
     * {@link RenderTooltipEvent.Color}, {@link RenderTooltipEvent.PostBackground}, and
     * {@link RenderTooltipEvent.PostText} will not be fired. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    @Cancelable
    public static class Pre extends RenderTooltipEvent
    {
        private int screenWidth;
        private int screenHeight;
        private int maxWidth;

        public Pre(ItemStack stack, List<? extends FormattedText> lines, PoseStack poseStack, int x, int y, int screenWidth, int screenHeight, int maxWidth, Font font)
        {
            super(stack, lines, poseStack, x, y, font);
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.maxWidth = maxWidth;
        }

        /**
         * {@return the width of the screen}.
         * The lines of text within the tooltip are wrapped to be within the screen width, and the tooltip box itself
         * is moved to be within the screen width.
         */
        public int getScreenWidth()
        {
            return screenWidth;
        }

        /**
         * Sets the screen width used for wrapping calculation.
         *
         * @param screenWidth the screen width
         */
        public void setScreenWidth(int screenWidth)
        {
            this.screenWidth = screenWidth;
        }

        /**
         * {@return the height of the screen}
         * The tooltip box is moved to be within the screen height.
         */
        public int getScreenHeight()
        {
            return screenHeight;
        }

        /**
         * Sets the screen height used for wrapping calculation.
         *
         * @param screenHeight the screen height
         */
        public void setScreenHeight(int screenHeight)
        {
            this.screenHeight = screenHeight;
        }

        /**
         * {@return the maximum width of the tooltip when being rendered}
         *
         * <p>A value of {@code -1} means an unlimited maximum width. However, an unlimited maxiumum width will still
         * be wrapped to be within the screen bounds.</p>
         */
        public int getMaxWidth()
        {
            return maxWidth;
        }


        /**
         * Sets the maximum width of the tooltip. Use {@code -1} for unlimited maximum width.
         *
         * @param maxWidth the new maximum width
         */
        public void setMaxWidth(int maxWidth)
        {
            this.maxWidth = maxWidth;
        }

        /**
         * Sets the font to be used to render text.
         *
         * @param fr the new font
         */
        public void setFontRenderer(Font fr)
        {
            this.font = fr;
        }

        /**
         * Sets the X origin of the tooltip.
         *
         * @param x the new X origin
         */
        public void setX(int x)
        {
            this.x = x;
        }

        /**
         * Sets the Y origin of the tooltip.
         *
         * @param y the new Y origin
         */
        public void setY(int y)
        {
            this.y = y;
        }
    }

    /**
     * Fired <b>after</b> the tooltip is rendered, at different points.
     * See the two subclasses for listening to after background or after text rendering.
     *
     * @see RenderTooltipEvent.PostBackground
     * @see RenderTooltipEvent.PostText
     */
    protected static abstract class Post extends RenderTooltipEvent
    {
        private final int width;
        private final int height;
        
        public Post(ItemStack stack, List<? extends FormattedText> textLines, PoseStack poseStack,int x, int y, Font font, int width, int height)
        {
            super(stack, textLines, poseStack, x, y, font);
            this.width = width;
            this.height = height;
        }

        /**
         * {@return the width of the inner tooltip box (not including border)}
         */
        public int getWidth()
        {
            return width;
        }

        /**
         * {@return the height of the inner tooltip box (not including border)}
         */
        public int getHeight()
        {
            return height;
        }
    }

    /**
     * Fired <b>after</b> the tooltip background is rendered, and <em>before</em> the tooltip text is rendered.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class PostBackground extends Post 
    {
        public PostBackground(ItemStack stack, List<? extends FormattedText> textLines, PoseStack poseStack, int x, int y, Font font, int width, int height)
        {
            super(stack, textLines, poseStack, x, y, font, width, height);
        }
    }

    /**
     * Fired <b>after</b> the tooltip text is rendered.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class PostText extends Post
    {
        public PostText(ItemStack stack, List<? extends FormattedText> textLines, PoseStack poseStack, int x, int y, Font font, int width, int height)
        {
            super(stack, textLines, poseStack, x, y, font, width, height);
        }
    }

    /**
     * Fired <em>directly <b>before</b> the tooltip background</em> is rendered.
     * This can be used to modify the background color and the border's gradient colors.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class Color extends RenderTooltipEvent
    {
        private final int originalBackground;
        private final int originalBorderStart;
        private final int originalBorderEnd;
        private int background;
        private int borderStart;
        private int borderEnd;

        public Color(ItemStack stack, List<? extends FormattedText> textLines, PoseStack poseStack, int x, int y, Font font, int background, int borderStart,
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

        /**
         * {@return the current tooltip background color}
         */
        public int getBackground()
        {
            return background;
        }

        /**
         * Sets the new color for the tooltip background.
         *
         * @param background the new tooltip background color
         */
        public void setBackground(int background)
        {
            this.background = background;
        }

        /**
         * {@return the gradient start color for the tooltip border}
         */
        public int getBorderStart()
        {
            return borderStart;
        }

        /**
         * Sets the new start color for the gradient of the tooltip border.
         *
         * @param borderStart the new start color for the tooltip border
         */
        public void setBorderStart(int borderStart)
        {
            this.borderStart = borderStart;
        }

        /**
         * {@return the gradient end color for the tooltip border}
         */
        public int getBorderEnd()
        {
            return borderEnd;
        }

        /**
         * Sets the new end color for the gradient of the tooltip border.
         *
         * @param borderEnd the new end color for the tooltip border
         */
        public void setBorderEnd(int borderEnd)
        {
            this.borderEnd = borderEnd;
        }

        /**
         * {@return the original tooltip background color}
         */
        public int getOriginalBackground()
        {
            return originalBackground;
        }

        /**
         * {@return the original tooltip border's gradient start color}
         */
        public int getOriginalBorderStart()
        {
            return originalBorderStart;
        }

        /**
         * {@return the original tooltip border's gradient end color}
         */
        public int getOriginalBorderEnd()
        {
            return originalBorderEnd;
        }
    }
}
