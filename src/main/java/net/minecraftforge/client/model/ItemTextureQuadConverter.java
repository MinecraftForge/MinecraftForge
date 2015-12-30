package net.minecraftforge.client.model;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

import javax.vecmath.Vector4f;
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
     * @param sprite   The texture whose UVs shall be used   @return The generated quads.
     */
    public static List<UnpackedBakedQuad> convertTexture(VertexFormat format, TRSRTransformation transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, EnumFacing facing, int color)
    {
        List<UnpackedBakedQuad> horizontal = convertTextureHorizontal(format, transform, template, sprite, z, facing, color);
        List<UnpackedBakedQuad> vertical = convertTextureVertical(format, transform, template, sprite, z, facing, color);

        return horizontal.size() >= vertical.size() ? horizontal : vertical;
    }

    /**
     * Scans a texture and converts it into a list of horizontal strips stacked on top of each other.
     * The height of the strips is as big as possible.
     */
    public static List<UnpackedBakedQuad> convertTextureHorizontal(VertexFormat format, TRSRTransformation transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, EnumFacing facing, int color)
    {
        int w = template.getIconWidth();
        int h = template.getIconHeight();
        int[] data = template.getFrameTextureData(0)[0];
        List<UnpackedBakedQuad> quads = Lists.newArrayList();

        // the upper left x-position of the current quad
        int start = -1;
        for (int y = 0; y < h; y++)
        {
            for (int x = 0; x < w; x++)
            {
                // current pixel
                int pixel = data[y * w + x];

                // no current quad but found a new one
                if (start < 0 && isVisible(pixel))
                {
                    start = x;
                }
                // got a current quad, but it ends here
                if (start >= 0 && !isVisible(pixel))
                {
                    // we now check if the visibility of the next row matches the one fo the current row
                    // if they are, we can extend the quad downwards
                    int endY = y + 1;
                    boolean sameRow = true;
                    while (sameRow)
                    {
                        for (int i = 0; i < w; i++)
                        {
                            int px1 = data[y * w + i];
                            int px2 = data[endY * w + i];
                            if (isVisible(px1) != isVisible(px2))
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
                    quads.add(genQuad(format, transform, start, y, x, endY, z, sprite, facing, color));

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
    public static List<UnpackedBakedQuad> convertTextureVertical(VertexFormat format, TRSRTransformation transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, EnumFacing facing, int color)
    {
        int w = template.getIconWidth();
        int h = template.getIconHeight();
        int[] data = template.getFrameTextureData(0)[0];
        List<UnpackedBakedQuad> quads = Lists.newArrayList();

        // the upper left y-position of the current quad
        int start = -1;
        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                // current pixel
                int pixel = data[y * w + x];

                // no current quad but found a new one
                if (start < 0 && isVisible(pixel))
                {
                    start = y;
                }
                // got a current quad, but it ends here
                if (start >= 0 && !isVisible(pixel))
                {
                    // we now check if the visibility of the next column matches the one fo the current row
                    // if they are, we can extend the quad downwards
                    int endX = x + 1;
                    boolean sameColumn = true;
                    while (sameColumn)
                    {
                        for (int i = 0; i < h; i++)
                        {
                            int px1 = data[i * w + x];
                            int px2 = data[i * w + endX];
                            if (isVisible(px1) != isVisible(px2))
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
                    quads.add(genQuad(format, transform, x, start, endX, y, z, sprite, facing, color));

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

    // true if alpha != 0
    private static boolean isVisible(int color)
    {
        return (color >> 24 & 255) > 0;
    }

    /**
     * Generates a Front/Back quad for an itemmodel. Therefore only supports facing NORTH and SOUTH.
     */
    public static UnpackedBakedQuad genQuad(VertexFormat format, TRSRTransformation transform, float x1, float y1, float x2, float y2, float z, TextureAtlasSprite sprite, EnumFacing facing, int color)
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

        return putQuad(format, transform, facing, color, x1, y1, x2, y2, z, u1, v1, u2, v2);
    }

    private static UnpackedBakedQuad putQuad(VertexFormat format, TRSRTransformation transform, EnumFacing side, int color,
                                             float x1, float y1, float x2, float y2, float z,
                                             float u1, float v1, float u2, float v2)
    {
        side = side.getOpposite();
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setQuadTint(-1);
        builder.setQuadOrientation(side);
        builder.setQuadColored();

        if (side == EnumFacing.NORTH)
        {
            putVertex(builder, format, transform, side, x1, y1, z, u1, v2, color);
            putVertex(builder, format, transform, side, x2, y1, z, u2, v2, color);
            putVertex(builder, format, transform, side, x2, y2, z, u2, v1, color);
            putVertex(builder, format, transform, side, x1, y2, z, u1, v1, color);
        } else
        {
            putVertex(builder, format, transform, side, x1, y1, z, u1, v2, color);
            putVertex(builder, format, transform, side, x1, y2, z, u1, v1, color);
            putVertex(builder, format, transform, side, x2, y2, z, u2, v1, color);
            putVertex(builder, format, transform, side, x2, y1, z, u2, v2, color);
        }
        return builder.build();
    }

    private static void putVertex(UnpackedBakedQuad.Builder builder, VertexFormat format, TRSRTransformation transform, EnumFacing side,
                                  float x, float y, float z, float u, float v, int color)
    {
        Vector4f vec = new Vector4f();
        for (int e = 0; e < format.getElementCount(); e++)
        {
            switch (format.getElement(e).getUsage())
            {
                case POSITION:
                    if (transform == TRSRTransformation.identity())
                    {
                        builder.put(e, x, y, z, 1);
                    }
                    // only apply the transform if it's not identity
                    else
                    {
                        vec.x = x;
                        vec.y = y;
                        vec.z = z;
                        vec.w = 1;
                        transform.getMatrix().transform(vec);
                        builder.put(e, vec.x, vec.y, vec.z, vec.w);
                    }
                    break;
                case COLOR:
                    float r = ((color >> 16) & 0xFF) / 255f; // red
                    float g = ((color >> 8) & 0xFF) / 255f; // green
                    float b = ((color >> 0) & 0xFF) / 255f; // blue
                    float a = ((color >> 24) & 0xFF) / 255f; // alpha
                    builder.put(e, r, g, b, a);
                    break;
                case UV:
                    if (format.getElement(e).getIndex() == 0)
                    {
                        builder.put(e, u, v, 0f, 1f);
                        break;
                    }
                case NORMAL:
                    builder.put(e, (float) side.getFrontOffsetX(), (float) side.getFrontOffsetY(), (float) side.getFrontOffsetZ(), 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }
}
