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

package net.minecraftforge.client.model;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.core.Direction;
import com.mojang.math.Transformation;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;

import java.util.List;

public final class ItemTextureQuadConverter
{
    private ItemTextureQuadConverter()
    {
        // non-instantiable
    }

    /**
     * Takes a texture and converts it into BakedQuads.
     * The conversion is done by scanning the texture horizontally and vertically and creating "strips" of the texture.
     * Strips that are of the same size and follow each other are converted into one bigger quad.
     * </br>
     * The resulting list of quads is the texture represented as a list of horizontal OR vertical quads,
     * depending on which creates less quads. If the amount of quads is equal, horizontal is preferred.
     *
     * @param template The input texture to convert
     * @param sprite   The texture whose UVs shall be used
     * @return The generated quads.
     */
    public static List<BakedQuad> convertTexture(Transformation transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, Direction facing, int color, int tint)
    {
        return convertTexture(transform, template, sprite, z, facing, color, tint, 0);
    }
    public static List<BakedQuad> convertTexture(Transformation transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, Direction facing, int color, int tint, int luminosity)
    {
        List<BakedQuad> horizontal = convertTextureHorizontal(transform, template, sprite, z, facing, color, tint, luminosity);
        List<BakedQuad> vertical = convertTextureVertical(transform, template, sprite, z, facing, color, tint, luminosity);

        return horizontal.size() <= vertical.size() ? horizontal : vertical;
    }

    /**
     * Scans a texture and converts it into a list of horizontal strips stacked on top of each other.
     * The height of the strips is as big as possible.
     */
    public static List<BakedQuad> convertTextureHorizontal(Transformation transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, Direction facing, int color, int tint)
    {
        return convertTextureHorizontal(transform, template, sprite, z, facing, color, tint, 0);
    }
    public static List<BakedQuad> convertTextureHorizontal(Transformation transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, Direction facing, int color, int tint, int luminosity)
    {
        int w = template.getWidth();
        int h = template.getHeight();
        float wScale = 16f / (float)w;
        float hScale = 16f / (float)h;
        List<BakedQuad> quads = Lists.newArrayList();

        // the y-position of the current batch of quads
        int startY = 0;
        for (int y = 1; y <= h; y++)
        {
            if (y < h)
            {
                // we check if the visibility of the next row matches the one for the current row
                // if they are, we can extend the quad downwards
                boolean sameRow = true;
                for (int x = 0; x < w; x++)
                {
                    if (template.isTransparent(0, x, y) != template.isTransparent(0, x, startY))
                    {
                        sameRow = false;
                        break;
                    }
                }

                if (sameRow)
                    continue; // continue to search for rows with same visibility
            }

            // all rows from startX (inclusive) to x (exclusive) have same visibility
            // create a batch of quads
            // the upper left x-position of the current quad
            int startX = -1;
            for (int x = 0; x <= w; x++)
            {
                if (x < w)
                {
                    // current pixel
                    boolean isVisible = !template.isTransparent(0, x, startY);

                    // no current quad but found a new one
                    if (startX < 0 && isVisible)
                    {
                        startX = x;
                    }

                    if (isVisible)
                        continue; // continue to search for visible pixels in the current quad
                }

                // got a current quad, but it ends here
                if (startX >= 0)
                {
                    // create the quad
                    quads.add(genQuad(transform,
                                      (float)startX * wScale,
                                      (float)startY * hScale,
                                      (float)x * wScale,
                                      (float)y * hScale,
                                      z, sprite, facing, color, tint, luminosity));

                    // clear current quad
                    startX = -1;
                }
            }

            // next batch of quads start at the current line
            startY = y;
        }

        return quads;
    }

    /**
     * Scans a texture and converts it into a list of vertical strips stacked next to each other from left to right.
     * The width of the strips is as big as possible.
     */
    public static List<BakedQuad> convertTextureVertical(Transformation transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, Direction facing, int color, int tint)
    {
        return convertTextureVertical(transform, template, sprite, z, facing, color, tint, 0);
    }
    public static List<BakedQuad> convertTextureVertical(Transformation transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, Direction facing, int color, int tint, int luminosity)
    {
        int w = template.getWidth();
        int h = template.getHeight();
        float wScale = 16f / (float)w;
        float hScale = 16f / (float)h;
        List<BakedQuad> quads = Lists.newArrayList();

        // the x-position of the current batch of quads
        int startX = 0;
        for (int x = 1; x <= w; x++)
        {
            if (x < w)
            {
                // we check if the visibility of the next column matches the one for the current row
                // if they are, we can extend the quad downwards
                boolean sameColumn = true;
                for (int y = 0; y < h; y++)
                {
                    if (template.isTransparent(0, x, y) != template.isTransparent(0, startX, y))
                    {
                        sameColumn = false;
                        break;
                    }
                }

                if (sameColumn)
                    continue; // continue to search for columns with same visibility
            }

            // all columns from startY (inclusive) to y (exclusive) have same visibility
            // create a batch of quads
            // the upper left y-position of the current quad
            int startY = -1;
            for (int y = 0; y <= h; y++)
            {
                if (y < h)
                {
                    // current pixel
                    boolean isVisible = !template.isTransparent(0, startX, y);

                    // no current quad but found a new one
                    if (startY < 0 && isVisible)
                    {
                        startY = y;
                    }

                    if (isVisible)
                        continue; // continue to search for visible pixels in the current quad
                }

                // got a current quad, but it ends here
                if (startY >= 0)
                {
                    // create the quad
                    quads.add(genQuad(transform,
                                      (float)startX * wScale,
                                      (float)startY * hScale,
                                      (float)x * wScale,
                                      (float)y * hScale,
                                      z, sprite, facing, color, tint, luminosity));

                    // clear current quad
                    startY = -1;
                }
            }

            // next batch of quads start at the current column
            startX = x;
        }

        return quads;
    }

    private static boolean isVisible(int color)
    {
        return (color >> 24 & 255) / 255f > 0.1f;
    }

    /**
     * Generates a Front/Back quad for an itemmodel. Therefore only supports facing NORTH and SOUTH.
     * Coordinates are [0,16] to match the usual coordinates used in TextureAtlasSprites
     */
    public static BakedQuad genQuad(Transformation transform, float x1, float y1, float x2, float y2, float z, TextureAtlasSprite sprite, Direction facing, int color, int tint)
    {
        return genQuad(transform, x1, y1, x2, y2, z, sprite, facing, color, tint, 0);
    }
    public static BakedQuad genQuad(Transformation transform, float x1, float y1, float x2, float y2, float z, TextureAtlasSprite sprite, Direction facing, int color, int tint, int luminosity)
    {
        float u1 = sprite.getU(x1);
        float v1 = sprite.getV(y1);
        float u2 = sprite.getU(x2);
        float v2 = sprite.getV(y2);

        x1 /= 16f;
        y1 /= 16f;
        x2 /= 16f;
        y2 /= 16f;

        float tmp = y1;
        y1 = 1f - y2;
        y2 = 1f - tmp;

        return putQuad(transform, facing, sprite, color, tint, x1, y1, x2, y2, z, u1, v1, u2, v2, luminosity);
    }

    private static BakedQuad putQuad(Transformation transform, Direction side, TextureAtlasSprite sprite, int color, int tint,
                                             float x1, float y1, float x2, float y2, float z,
                                             float u1, float v1, float u2, float v2, int luminosity)
    {
        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);

        builder.setQuadTint(tint);
        builder.setQuadOrientation(side);
        builder.setApplyDiffuseLighting(luminosity == 0);

        // only apply the transform if it's not identity
        boolean hasTransform = !transform.isIdentity();
        IVertexConsumer consumer = hasTransform ? new TRSRTransformer(builder, transform) : builder;

        if (side == Direction.SOUTH)
        {
            putVertex(consumer, side, x1, y1, z, u1, v2, color, luminosity);
            putVertex(consumer, side, x2, y1, z, u2, v2, color, luminosity);
            putVertex(consumer, side, x2, y2, z, u2, v1, color, luminosity);
            putVertex(consumer, side, x1, y2, z, u1, v1, color, luminosity);
        }
        else
        {
            putVertex(consumer, side, x1, y1, z, u1, v2, color, luminosity);
            putVertex(consumer, side, x1, y2, z, u1, v1, color, luminosity);
            putVertex(consumer, side, x2, y2, z, u2, v1, color, luminosity);
            putVertex(consumer, side, x2, y1, z, u2, v2, color, luminosity);
        }
        return builder.build();
    }

    private static void putVertex(IVertexConsumer consumer, Direction side,
                                  float x, float y, float z, float u, float v, int color, int luminosity)
    {
        VertexFormat format = consumer.getVertexFormat();
        for (int e = 0; e < format.getElements().size(); e++)
        {
            VertexFormatElement element = format.getElements().get(e);
            switch (element.getUsage())
            {
                case POSITION:
                    consumer.put(e, x, y, z, 1f);
                    break;
                case COLOR:
                    float r = ((color >> 16) & 0xFF) / 255f; // red
                    float g = ((color >>  8) & 0xFF) / 255f; // green
                    float b = ((color >>  0) & 0xFF) / 255f; // blue
                    float a = ((color >> 24) & 0xFF) / 255f; // alpha
                    consumer.put(e, r, g, b, a);
                    break;
                case NORMAL:
                    float offX = (float) side.getStepX();
                    float offY = (float) side.getStepY();
                    float offZ = (float) side.getStepZ();
                    consumer.put(e, offX, offY, offZ, 0f);
                    break;
                case UV:
                    if (element.getIndex() == 0)
                    {
                        consumer.put(e, u, v, 0f, 1f);
                        break;
                    }
                    else if (element.getIndex() == 2)
                    {
                        consumer.put(e, (luminosity<<4)/32768.0f, (luminosity<<4)/32768.0f, 0f, 1f);
                        break;
                    }
                    // else fallthrough to default
                default:
                    consumer.put(e);
                    break;
            }
        }
    }
}
