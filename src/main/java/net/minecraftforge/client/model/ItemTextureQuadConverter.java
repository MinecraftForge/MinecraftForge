/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
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
     * @param format
     * @param template The input texture to convert
     * @param sprite   The texture whose UVs shall be used
     * @return The generated quads.
     */
    public static List<BakedQuad> convertTexture(TransformationMatrix transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, Direction facing, int color, int tint)
    {
        List<BakedQuad> horizontal = convertTextureHorizontal(transform, template, sprite, z, facing, color, tint);
        List<BakedQuad> vertical = convertTextureVertical(transform, template, sprite, z, facing, color, tint);

        return horizontal.size() <= vertical.size() ? horizontal : vertical;
    }

    /**
     * Scans a texture and converts it into a list of horizontal strips stacked on top of each other.
     * The height of the strips is as big as possible.
     */
    public static List<BakedQuad> convertTextureHorizontal(TransformationMatrix transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, Direction facing, int color, int tint)
    {
        int w = template.getWidth();
        int h = template.getHeight();
        float wScale = 16f / (float)w;
        float hScale = 16f / (float)h;
        List<BakedQuad> quads = Lists.newArrayList();

        // the upper left x-position of the current quad
        int start = -1;
        for (int y = 0; y < h; y++)
        {
            for (int x = 0; x < w; x++)
            {
                // current pixel
                boolean isVisible = !template.isPixelTransparent(0, x, y);

                // no current quad but found a new one
                if (start < 0 && isVisible)
                {
                    start = x;
                }
                // got a current quad, but it ends here
                if (start >= 0 && !isVisible)
                {
                    // we now check if the visibility of the next row matches the one fo the current row
                    // if they are, we can extend the quad downwards
                    int endY = y + 1;
                    boolean sameRow = true;
                    while (sameRow && endY < h)
                    {
                        for (int i = 0; i < w; i++)
                        {
                            if (template.isPixelTransparent(0, i, y) != template.isPixelTransparent(0, i, endY))
                            {
                                sameRow = false;
                                break;
                            }
                        }
                        if (sameRow)
                        {
                            endY++;
                        }
                    }

                    // create the quad
                    quads.add(genQuad(transform,
                                      (float)start * wScale,
                                      (float)y * hScale,
                                      (float)x * wScale,
                                      (float)endY * hScale,
                                      z, sprite, facing, color, tint));

                    // update Y if all the rows match. no need to rescan
                    if (endY - y > 1)
                    {
                        y = endY - 1;
                    }
                    // clear current quad
                    start = -1;
                }
            }
        }

        return quads;
    }

    /**
     * Scans a texture and converts it into a list of vertical strips stacked next to each other from left to right.
     * The width of the strips is as big as possible.
     */
    public static List<BakedQuad> convertTextureVertical(TransformationMatrix transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, Direction facing, int color, int tint)
    {
        int w = template.getWidth();
        int h = template.getHeight();
        float wScale = 16f / (float)w;
        float hScale = 16f / (float)h;
        List<BakedQuad> quads = Lists.newArrayList();

        // the upper left y-position of the current quad
        int start = -1;
        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                // current pixel
                boolean isVisible = !template.isPixelTransparent(0, x, y);

                // no current quad but found a new one
                if (start < 0 && isVisible)
                {
                    start = y;
                }
                // got a current quad, but it ends here
                if (start >= 0 && !isVisible)
                {
                    // we now check if the visibility of the next column matches the one fo the current row
                    // if they are, we can extend the quad downwards
                    int endX = x + 1;
                    boolean sameColumn = true;
                    while (sameColumn && endX < w)
                    {
                        for (int i = 0; i < h; i++)
                        {
                            if (template.isPixelTransparent(0, x, i) != template.isPixelTransparent(0, endX, i))
                            {
                                sameColumn = false;
                                break;
                            }
                        }
                        if (sameColumn)
                        {
                            endX++;
                        }
                    }

                    // create the quad
                    quads.add(genQuad(transform,
                                      (float)x * wScale,
                                      (float)start * hScale,
                                      (float)endX * wScale,
                                      (float)y * hScale,
                                      z, sprite, facing, color, tint));

                    // update X if all the columns match. no need to rescan
                    if (endX - x > 1)
                    {
                        x = endX - 1;
                    }
                    // clear current quad
                    start = -1;
                }
            }
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
    public static BakedQuad genQuad(TransformationMatrix transform, float x1, float y1, float x2, float y2, float z, TextureAtlasSprite sprite, Direction facing, int color, int tint)
    {
        float u1 = sprite.getInterpolatedU(x1);
        float v1 = sprite.getInterpolatedV(y1);
        float u2 = sprite.getInterpolatedU(x2);
        float v2 = sprite.getInterpolatedV(y2);

        x1 /= 16f;
        y1 /= 16f;
        x2 /= 16f;
        y2 /= 16f;

        float tmp = y1;
        y1 = 1f - y2;
        y2 = 1f - tmp;

        return putQuad(transform, facing, sprite, color, tint, x1, y1, x2, y2, z, u1, v1, u2, v2);
    }

    private static BakedQuad putQuad(TransformationMatrix transform, Direction side, TextureAtlasSprite sprite, int color, int tint,
                                             float x1, float y1, float x2, float y2, float z,
                                             float u1, float v1, float u2, float v2)
    {
        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);

        builder.setQuadTint(tint);
        builder.setQuadOrientation(side);

        // only apply the transform if it's not identity
        boolean hasTransform = !transform.isIdentity();
        IVertexConsumer consumer = hasTransform ? new TRSRTransformer(builder, transform) : builder;

        if (side == Direction.SOUTH)
        {
            putVertex(consumer, side, x1, y1, z, u1, v2, color);
            putVertex(consumer, side, x2, y1, z, u2, v2, color);
            putVertex(consumer, side, x2, y2, z, u2, v1, color);
            putVertex(consumer, side, x1, y2, z, u1, v1, color);
        }
        else
        {
            putVertex(consumer, side, x1, y1, z, u1, v2, color);
            putVertex(consumer, side, x1, y2, z, u1, v1, color);
            putVertex(consumer, side, x2, y2, z, u2, v1, color);
            putVertex(consumer, side, x2, y1, z, u2, v2, color);
        }
        return builder.build();
    }

    private static void putVertex(IVertexConsumer consumer, Direction side,
                                  float x, float y, float z, float u, float v, int color)
    {
        VertexFormat format = consumer.getVertexFormat();
        for (int e = 0; e < format.getElements().size(); e++)
        {
            switch (format.getElements().get(e).getUsage())
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
                    float offX = (float) side.getXOffset();
                    float offY = (float) side.getYOffset();
                    float offZ = (float) side.getZOffset();
                    consumer.put(e, offX, offY, offZ, 0f);
                    break;
                case UV:
                    if (format.getElements().get(e).getIndex() == 0)
                    {
                        consumer.put(e, u, v, 0f, 1f);
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
