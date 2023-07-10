/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * Extension interface for {@link GuiGraphics}.
 */
public interface IForgeGuiGraphics
{
    private GuiGraphics self()
    {
        return (GuiGraphics) this;
    }

    int DEFAULT_BACKGROUND_COLOR = 0xF0100010;
    int DEFAULT_BORDER_COLOR_START = 0x505000FF;
    int DEFAULT_BORDER_COLOR_END = (DEFAULT_BORDER_COLOR_START & 0xFEFEFE) >> 1 | DEFAULT_BORDER_COLOR_START & 0xFF000000;
    String UNDO_CHAR  = "\u21B6";
    String RESET_CHAR = "\u2604";
    String VALID      = "\u2714";
    String INVALID    = "\u2715";
    int[] TEXT_COLOR_CODES = new int[] { 0, 170, 43520, 43690, 11141120, 11141290, 16755200, 11184810, 5592405, 5592575, 5635925, 5636095, 16733525, 16733695, 16777045, 16777215,
            0, 42, 10752, 10794, 2752512, 2752554, 2763264, 2763306, 1381653, 1381695, 1392405, 1392447, 4134165, 4134207, 4144917, 4144959 };

    default int getColorFromFormattingCharacter(char c, boolean isLighter)
    {
        return TEXT_COLOR_CODES[isLighter ? "0123456789abcdef".indexOf(c) : "0123456789abcdef".indexOf(c) + 16];
    }

    /**
     * Draws a textured box of any size (smallest size is borderSize * 2 square)
     * based on a fixed size textured box with continuous borders and filler.
     *
     * @param texture the ResourceLocation object that contains the desired image
     * @param x x-axis offset
     * @param y y-axis offset
     * @param u bound resource location image x offset
     * @param v bound resource location image y offset
     * @param width the desired box width
     * @param height the desired box height
     * @param textureWidth the width of the box texture in the resource location image
     * @param textureHeight the height of the box texture in the resource location image
     * @param borderSize the size of the box's borders
     */
    default void blitWithBorder(ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int borderSize)
    {
        this.blitWithBorder(texture, x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize);
    }

    /**
     * Draws a textured box of any size (smallest size is borderSize * 2 square)
     * based on a fixed size textured box with continuous borders and filler.
     *
     * @param texture the ResourceLocation object that contains the desired image
     * @param x x-axis offset
     * @param y y-axis offset
     * @param u bound resource location image x offset
     * @param v bound resource location image y offset
     * @param width the desired box width
     * @param height the desired box height
     * @param textureWidth the width of the box texture in the resource location image
     * @param textureHeight the height of the box texture in the resource location image
     * @param topBorder the size of the box's top border
     * @param bottomBorder the size of the box's bottom border
     * @param leftBorder the size of the box's left border
     * @param rightBorder the size of the box's right border
     */
    default void blitWithBorder(ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder)
    {
        int fillerWidth = textureWidth - leftBorder - rightBorder;
        int fillerHeight = textureHeight - topBorder - bottomBorder;
        int canvasWidth = width - leftBorder - rightBorder;
        int canvasHeight = height - topBorder - bottomBorder;
        int xPasses = canvasWidth / fillerWidth;
        int remainderWidth = canvasWidth % fillerWidth;
        int yPasses = canvasHeight / fillerHeight;
        int remainderHeight = canvasHeight % fillerHeight;

        // Draw Border
        // Top Left
        self().blit(texture, x, y, u, v, leftBorder, topBorder);
        // Top Right
        self().blit(texture, x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder);
        // Bottom Left
        self().blit(texture, x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder);
        // Bottom Right
        self().blit(texture, x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder);

        for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++)
        {
            // Top Border
            self().blit(texture, x + leftBorder + (i * fillerWidth), y, u + leftBorder, v, (i == xPasses ? remainderWidth : fillerWidth), topBorder);
            // Bottom Border
            self().blit(texture, x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), bottomBorder);

            // Throw in some filler for good measure
            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
                self().blit(texture, x + leftBorder + (i * fillerWidth), y + topBorder + (j * fillerHeight), u + leftBorder, v + topBorder, (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight));
        }

        // Side Borders
        for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
        {
            // Left Border
            self().blit(texture, x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder, (j == yPasses ? remainderHeight : fillerHeight));
            // Right Border
            self().blit(texture, x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight), u + leftBorder + fillerWidth, v + topBorder, rightBorder, (j == yPasses ? remainderHeight : fillerHeight));
        }
    }

    default void blitInscribed(ResourceLocation texture, int x, int y, int boundsWidth, int boundsHeight, int rectWidth, int rectHeight)
    {
        this.blitInscribed(texture, x, y, boundsWidth, boundsHeight, rectWidth, rectHeight, true, true);
    }

    default void blitInscribed(ResourceLocation texture, int x, int y, int boundsWidth, int boundsHeight, int rectWidth, int rectHeight, boolean centerX, boolean centerY)
    {
        if (rectWidth * boundsHeight > rectHeight * boundsWidth)
        {
            int h = boundsHeight;
            boundsHeight = (int) (boundsWidth * ((double) rectHeight / rectWidth));
            if (centerY) y += (h - boundsHeight) / 2;
        }
        else
        {
            int w = boundsWidth;
            boundsWidth = (int) (boundsHeight * ((double) rectWidth / rectHeight));
            if (centerX) x += (w - boundsWidth) / 2;
        }

        self().blit(texture, x, y, boundsWidth, boundsHeight, 0.0f,0.0f, rectWidth, rectHeight, rectWidth, rectHeight);
    }

    /**
     * Version of {@link GuiGraphics#blitNineSliced(ResourceLocation, int, int, int, int, int, int, int, int, int)} that supports specifying the texture's size.
     */
    default void blitNineSlicedSized(ResourceLocation texture, int x, int y, int width, int height, int sliceSize, int uWidth, int vHeight, int uOffset, int vOffset,
          int textureWidth, int textureHeight)
    {
        blitNineSlicedSized(texture, x, y, width, height, sliceSize, sliceSize, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    /**
     * Version of {@link GuiGraphics#blitNineSliced(ResourceLocation, int, int, int, int, int, int, int, int, int, int)} that supports specifying the texture's size.
     */
    default void blitNineSlicedSized(ResourceLocation texture, int x, int y, int width, int height, int sliceWidth, int sliceHeight, int uWidth, int vHeight,
          int uOffset, int vOffset, int textureWidth, int textureHeight)
    {
        blitNineSlicedSized(texture, x, y, width, height, sliceWidth, sliceHeight, sliceWidth, sliceHeight, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    /**
     * Version of {@link GuiGraphics#blitNineSliced(ResourceLocation, int, int, int, int, int, int, int, int, int, int, int, int)} that supports specifying the texture's size.
     */
    default void blitNineSlicedSized(ResourceLocation texture, int x, int y, int width, int height, int cornerWidth, int cornerHeight, int edgeWidth, int edgeHeight,
          int uWidth, int vHeight, int uOffset, int vOffset, int textureWidth, int textureHeight)
    {
        cornerWidth = Math.min(cornerWidth, width / 2);
        edgeWidth = Math.min(edgeWidth, width / 2);
        cornerHeight = Math.min(cornerHeight, height / 2);
        edgeHeight = Math.min(edgeHeight, height / 2);
        GuiGraphics self = self();
        if (width == uWidth && height == vHeight)
        {
            self.blit(texture, x, y, uOffset, vOffset, width, height, textureWidth, textureHeight);
        }
        else if (height == vHeight)
        {
            self.blit(texture, x, y, uOffset, vOffset, cornerWidth, height, textureWidth, textureHeight);
            self.blitRepeating(texture, x + cornerWidth, y, width - edgeWidth - cornerWidth, height, uOffset + cornerWidth, vOffset, uWidth - edgeWidth - cornerWidth, vHeight, textureWidth, textureHeight);
            self.blit(texture, x + width - edgeWidth, y, uOffset + uWidth - edgeWidth, vOffset, edgeWidth, height, textureWidth, textureHeight);
        }
        else if (width == uWidth)
        {
            self.blit(texture, x, y, uOffset, vOffset, width, cornerHeight, textureWidth, textureHeight);
            self.blitRepeating(texture, x, y + cornerHeight, width, height - edgeHeight - cornerHeight, uOffset, vOffset + cornerHeight, uWidth, vHeight - edgeHeight - cornerHeight, textureWidth, textureHeight);
            self.blit(texture, x, y + height - edgeHeight, uOffset, vOffset + vHeight - edgeHeight, width, edgeHeight, textureWidth, textureHeight);
        }
        else
        {
            self.blit(texture, x, y, uOffset, vOffset, cornerWidth, cornerHeight, textureWidth, textureHeight);
            self.blitRepeating(texture, x + cornerWidth, y, width - edgeWidth - cornerWidth, cornerHeight, uOffset + cornerWidth, vOffset, uWidth - edgeWidth - cornerWidth, cornerHeight, textureWidth, textureHeight);
            self.blit(texture, x + width - edgeWidth, y, uOffset + uWidth - edgeWidth, vOffset, edgeWidth, cornerHeight, textureWidth, textureHeight);
            self.blit(texture, x, y + height - edgeHeight, uOffset, vOffset + vHeight - edgeHeight, cornerWidth, edgeHeight, textureWidth, textureHeight);
            self.blitRepeating(texture, x + cornerWidth, y + height - edgeHeight, width - edgeWidth - cornerWidth, edgeHeight, uOffset + cornerWidth, vOffset + vHeight - edgeHeight, uWidth - edgeWidth - cornerWidth, edgeHeight, textureWidth, textureHeight);
            self.blit(texture, x + width - edgeWidth, y + height - edgeHeight, uOffset + uWidth - edgeWidth, vOffset + vHeight - edgeHeight, edgeWidth, edgeHeight, textureWidth, textureHeight);
            self.blitRepeating(texture, x, y + cornerHeight, cornerWidth, height - edgeHeight - cornerHeight, uOffset, vOffset + cornerHeight, cornerWidth, vHeight - edgeHeight - cornerHeight, textureWidth, textureHeight);
            self.blitRepeating(texture, x + cornerWidth, y + cornerHeight, width - edgeWidth - cornerWidth, height - edgeHeight - cornerHeight, uOffset + cornerWidth, vOffset + cornerHeight, uWidth - edgeWidth - cornerWidth, vHeight - edgeHeight - cornerHeight, textureWidth, textureHeight);
            self.blitRepeating(texture, x + width - edgeWidth, y + cornerHeight, cornerWidth, height - edgeHeight - cornerHeight, uOffset + uWidth - edgeWidth, vOffset + cornerHeight, edgeWidth, vHeight - edgeHeight - cornerHeight, textureWidth, textureHeight);
        }
    }
}
