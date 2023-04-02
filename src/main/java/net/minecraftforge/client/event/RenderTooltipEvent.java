/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Fired during tooltip rendering.
 * See the various subclasses for listening to specific events.
 *
 * @see RenderTooltipEvent.GatherComponents
 * @see RenderTooltipEvent.Pre
 * @see RenderTooltipEvent.Color
 */
public abstract class RenderTooltipEvent extends Event
{
    @NotNull
    protected final ItemStack itemStack;
    protected final GuiGraphics graphics;
    protected int x;
    protected int y;
    protected Font font;
    protected final List<ClientTooltipComponent> components;

    @ApiStatus.Internal
    protected RenderTooltipEvent(@NotNull ItemStack itemStack, GuiGraphics graphics, int x, int y, @NotNull Font font, @NotNull List<ClientTooltipComponent> components)
    {
        this.itemStack = itemStack;
        this.graphics = graphics;
        this.components = Collections.unmodifiableList(components);
        this.x = x;
        this.y = y;
        this.font = font;
    }

    /**
     * {@return the item stack which the tooltip is being rendered for, or an {@linkplain ItemStack#isEmpty() empty
     * item stack} if there is no associated item stack}
     */
    @NotNull
    public ItemStack getItemStack()
    {
        return itemStack;
    }

    /**
     * {@return the graphics helper for the gui}
     */
    public GuiGraphics getGraphics()
    {
        return this.graphics;
    }

    /**
     * {@return the unmodifiable list of tooltip components}
     *
     * <p>Use {@link ItemTooltipEvent} or {@link GatherComponents} to modify tooltip contents or components.</p>
     */
    @NotNull
    public List<ClientTooltipComponent> getComponents()
    {
        return components;
    }

    /**
     * {@return the X position of the tooltip box} By default, this is the mouse X position.
     */
    public int getX()
    {
        return x;
    }

    /**
     * {@return the Y position of the tooltip box} By default, this is the mouse Y position.
     */
    public int getY()
    {
        return y;
    }

    /**
     * {@return The font used to render the text}
     */
    @NotNull
    public Font getFont()
    {
        return font;
    }

    /**
     * Fired when a tooltip gathers the {@link TooltipComponent}s to be rendered, before any text wrapping or processing.
     * The list of components and the maximum width of the tooltip can be modified through this event.
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the list of components will be empty, causing the tooltip to not be rendered and
     * the corresponding {@link RenderTooltipEvent.Pre} and {@link RenderTooltipEvent.Color} to not be fired.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @Cancelable
    public static class GatherComponents extends Event
    {
        private final ItemStack itemStack;
        private final int screenWidth;
        private final int screenHeight;
        private final List<Either<FormattedText, TooltipComponent>> tooltipElements;
        private int maxWidth;

        @ApiStatus.Internal
        public GatherComponents(ItemStack itemStack, int screenWidth, int screenHeight, List<Either<FormattedText, TooltipComponent>> tooltipElements, int maxWidth)
        {
            this.itemStack = itemStack;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.tooltipElements = tooltipElements;
            this.maxWidth = maxWidth;
        }

        /**
         * {@return the item stack which the tooltip is being rendered for, or an {@linkplain ItemStack#isEmpty() empty
         * item stack} if there is no associated item stack}
         */
        public ItemStack getItemStack()
        {
            return itemStack;
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
         * {@return the height of the screen}
         * The tooltip box is moved to be within the screen height.
         */
        public int getScreenHeight()
        {
            return screenHeight;
        }

        /**
         * {@return the modifiable list of elements to be rendered on the tooltip} These elements can be either
         * formatted text or custom tooltip components.
         */
        public List<Either<FormattedText, TooltipComponent>> getTooltipElements()
        {
            return tooltipElements;
        }

        /**
         * {@return the maximum width of the tooltip when being rendered}
         *
         * <p>A value of {@code -1} means an unlimited maximum width. However, an unlimited maximum width will still
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
    }

    /**
     * Fired <b>before</b> the tooltip is rendered.
     * This can be used to modify the positioning and font of the tooltip.
     *
     * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the tooltip will not be rendered and the corresponding
     * {@link RenderTooltipEvent.Color} will not be fired.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    @Cancelable
    public static class Pre extends RenderTooltipEvent
    {
        private final int screenWidth;
        private final int screenHeight;
        private final ClientTooltipPositioner positioner;

        @ApiStatus.Internal
        public Pre(@NotNull ItemStack stack, GuiGraphics graphics, int x, int y, int screenWidth, int screenHeight, @NotNull Font font, @NotNull List<ClientTooltipComponent> components, @NotNull ClientTooltipPositioner positioner)
        {
            super(stack, graphics, x, y, font, components);
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.positioner = positioner;
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
         * {@return the height of the screen}
         * The tooltip box is moved to be within the screen height.
         */
        public int getScreenHeight()
        {
            return screenHeight;
        }

        public ClientTooltipPositioner getTooltipPositioner()
        {
            return positioner;
        }

        /**
         * Sets the font to be used to render text.
         *
         * @param fr the new font
         */
        public void setFont(@NotNull Font fr)
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
     * Fired when the colours for the tooltip background are determined.
     * This can be used to modify the background color and the border's gradient colors.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class Color extends RenderTooltipEvent
    {
        private final int originalBackground;
        private final int originalBorderStart;
        private final int originalBorderEnd;
        private int backgroundStart;
        private int backgroundEnd;
        private int borderStart;
        private int borderEnd;

        @ApiStatus.Internal
        public Color(@NotNull ItemStack stack, GuiGraphics graphics, int x, int y, @NotNull Font fr, int background, int borderStart, int borderEnd, @NotNull List<ClientTooltipComponent> components)
        {
            super(stack, graphics, x, y, fr, components);
            this.originalBackground = background;
            this.originalBorderStart = borderStart;
            this.originalBorderEnd = borderEnd;
            this.backgroundStart = background;
            this.backgroundEnd = background;
            this.borderStart = borderStart;
            this.borderEnd = borderEnd;
        }

        /**
         * {@return the gradient start color for the tooltip background (top edge)}
         */
        public int getBackgroundStart()
        {
            return backgroundStart;
        }

        /**
         * {@return the gradient end color for the tooltip background (bottom edge)}
         */
        public int getBackgroundEnd()
        {
            return backgroundEnd;
        }

        /**
         * Sets the new color for the tooltip background. This sets both the gradient start and end color for the
         * background to this color.
         *
         * @param background the new color for the tooltip background
         */
        public void setBackground(int background)
        {
            this.backgroundStart = background;
            this.backgroundEnd = background;
        }

        /**
         * Sets the new start color for the gradient of the tooltip background (top edge).
         *
         * @param backgroundStart the new start color for the tooltip background
         */
        public void setBackgroundStart(int backgroundStart)
        {
            this.backgroundStart = backgroundStart;
        }

        /**
         * Sets the new end color for the gradient of the tooltip background (bottom edge).
         *
         * @param backgroundEnd the new end color for the tooltip background
         */
        public void setBackgroundEnd(int backgroundEnd)
        {
            this.backgroundEnd = backgroundEnd;
        }

        /**
         * {@return the gradient start color for the tooltip border (top edge)}
         */
        public int getBorderStart()
        {
            return borderStart;
        }

        /**
         * Sets the new start color for the gradient of the tooltip border (top edge).
         *
         * @param borderStart the new start color for the tooltip border
         */
        public void setBorderStart(int borderStart)
        {
            this.borderStart = borderStart;
        }

        /**
         * {@return the gradient end color for the tooltip border (bottom edge)}
         */
        public int getBorderEnd()
        {
            return borderEnd;
        }

        /**
         * Sets the new end color for the gradient of the tooltip border (bottom edge).
         *
         * @param borderEnd the new end color for the tooltip border
         */
        public void setBorderEnd(int borderEnd)
        {
            this.borderEnd = borderEnd;
        }

        /**
         * {@return the original tooltip background's gradient start color (top edge)}
         */
        public int getOriginalBackgroundStart()
        {
            return originalBackground;
        }

        /**
         * {@return the original tooltip background's gradient end color (bottom edge)}
         */
        public int getOriginalBackgroundEnd()
        {
            return originalBackground;
        }

        /**
         * {@return the original tooltip border's gradient start color (top edge)}
         */
        public int getOriginalBorderStart()
        {
            return originalBorderStart;
        }

        /**
         * {@return the original tooltip border's gradient end color (bottom edge)}
         */
        public int getOriginalBorderEnd()
        {
            return originalBorderEnd;
        }
    }
}
