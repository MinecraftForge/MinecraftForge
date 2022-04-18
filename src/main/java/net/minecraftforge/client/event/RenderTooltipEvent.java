/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
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
 * <li>{@link RenderTooltipEvent.GatherComponents}</li>
 * <li>{@link RenderTooltipEvent.Color}</li>
 * </ul>
 */
public abstract class RenderTooltipEvent extends net.minecraftforge.eventbus.api.Event
{
    @Nonnull
    protected final ItemStack itemStack;
    protected final PoseStack poseStack;
    protected int x;
    protected int y;
    protected Font font;
    protected final List<ClientTooltipComponent> components;


    public RenderTooltipEvent(@Nonnull ItemStack itemStack, PoseStack poseStack, int x, int y, @Nonnull Font font, @Nonnull List<ClientTooltipComponent> components)
    {
        this.itemStack = itemStack;
        this.poseStack = poseStack;
        this.components = Collections.unmodifiableList(components);
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
        return itemStack;
    }


    /**
     * @return The PoseStack rendering context.
     */
    public PoseStack getPoseStack() { return poseStack; }

    /**
     * The components to be drawn.
     * To modify this list use {@link GatherComponents}.
     * @return an unmodifiable list of tooltip components to be drawn.
     */
    @Nonnull
    public List<ClientTooltipComponent> getComponents()
    {
        return components;
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
     * @return The {@link Font} instance the current render is using.
     */
    @Nonnull
    public Font getFont()
    {
        return font;
    }

    /**
     * Fires when a tooltip gathers the {@link TooltipComponent}s to render. This event fires before any text wrapping
     * or text processing.
     * This event allows modifying the components to be rendered as well as specifying a maximum width for the tooltip.
     * The maximum width will cause any text components to be wrapped.
     */
    @Cancelable
    public static class GatherComponents extends Event
    {
        private final ItemStack itemStack;
        private final int screenWidth;
        private final int screenHeight;
        private final List<Either<FormattedText, TooltipComponent>> tooltipElements;
        private int maxWidth;

        public GatherComponents(ItemStack itemStack, int screenWidth, int screenHeight, List<Either<FormattedText, TooltipComponent>> tooltipElements, int maxWidth)
        {
            this.itemStack = itemStack;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.tooltipElements = tooltipElements;
            this.maxWidth = maxWidth;
        }

        /**
         * @return the ItemStack whose tooltip is being rendered or an empty stack if this tooltip is not for a stack
         */
        public ItemStack getItemStack()
        {
            return itemStack;
        }

        /**
         * @return the width of the screen
         */
        public int getScreenWidth()
        {
            return screenWidth;
        }

        /**
         * @return the height of the screen
         */
        public int getScreenHeight()
        {
            return screenHeight;
        }

        /**
         * The elements to be rendered. These can be either formatted text or custom tooltip components.
         * This list is modifiable.
         */
        public List<Either<FormattedText, TooltipComponent>> getTooltipElements()
        {
            return tooltipElements;
        }

        /**
         * @return the current maximum width for the text components of the tooltip (or -1 for no maximum width)
         */
        public int getMaxWidth()
        {
            return maxWidth;
        }

        /**
         * Set the maximum width for the text components of the tooltip (or -1 for no maximum width)
         */
        public void setMaxWidth(int maxWidth)
        {
            this.maxWidth = maxWidth;
        }
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

        public Pre(@Nonnull ItemStack stack, PoseStack poseStack, int x, int y, int screenWidth, int screenHeight, @Nonnull Font font, @Nonnull List<ClientTooltipComponent> components)
        {
            super(stack, poseStack, x, y, font, components);
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
        }

        public int getScreenWidth()
        {
            return screenWidth;
        }

        public int getScreenHeight()
        {
            return screenHeight;
        }

        /**
         * Sets the {@link Font} to be used to render text.
         */
        public void setFont(@Nonnull Font fr)
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
     * This event is fired when the colours for the tooltip background are determined. 
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

        public Color(@Nonnull ItemStack stack, PoseStack poseStack, int x, int y, @Nonnull Font fr, int background, int borderStart, int borderEnd, @Nonnull List<ClientTooltipComponent> components)
        {
            super(stack, poseStack, x, y, fr, components);
            this.originalBackground = background;
            this.originalBorderStart = borderStart;
            this.originalBorderEnd = borderEnd;
            this.backgroundStart = background;
            this.backgroundEnd = background;
            this.borderStart = borderStart;
            this.borderEnd = borderEnd;
        }

        public int getBackgroundStart()
        {
            return backgroundStart;
        }

        public int getBackgroundEnd()
        {
            return backgroundEnd;
        }

        public void setBackground(int background)
        {
            this.backgroundStart = background;
            this.backgroundEnd = background;
        }

        public void setBackgroundStart(int backgroundStart)
        {
            this.backgroundStart = backgroundStart;
        }

        public void setBackgroundEnd(int backgroundEnd)
        {
            this.backgroundEnd = backgroundEnd;
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

        public int getOriginalBackgroundStart()
        {
            return originalBackground;
        }

        public int getOriginalBackgroundEnd()
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
