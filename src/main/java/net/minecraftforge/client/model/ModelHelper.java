package net.minecraftforge.client.model;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.List;

import javax.vecmath.Vector3f;

public final class ModelHelper {
  private ModelHelper() {} // non-instantiable

  /**
   * Takes a texture and converts it into BakedQuads.
   * The conversion is done by scanning the texture horizontally and vertically and creating "strips" of the texture.
   * Strips that are of the same size and follow each other are converted into one bigger quad.
   * </br>
   * The resulting list of quads is the texture represented as a list of horizontal OR vertical quads,
   * depending on which creates less quads. If the amount of quads is equal, horizontal is preferred.
   *
   * @param buf
   * @param format
   *@param template The input texture to convert
   * @param sprite The texture whose UVs shall be used   @return The generated quads.
   */
  public static List<BakedQuad> convertTexture(ByteBuffer buf, VertexFormat format, TextureAtlasSprite template, TextureAtlasSprite sprite, int color) {
    List<BakedQuad> horizontal = convertTextureHorizontal(buf, format, template, sprite, color);
    List<BakedQuad> vertical = convertTextureVertical(buf, format, template, sprite, color);

    return horizontal.size() >= vertical.size() ? horizontal : vertical;
  }

  /**
   * Scans a texture and converts it into a list of horizontal strips stacked on top of each other.
   * The height of the strips is as big as possible.
   */
  public static List<BakedQuad> convertTextureHorizontal(ByteBuffer buf, VertexFormat format, TextureAtlasSprite template, TextureAtlasSprite sprite, int color) {
    int w = template.getIconWidth();
    int h = template.getIconHeight();
    int[] data = template.getFrameTextureData(0)[0];
    List<BakedQuad> quads = Lists.newArrayList();

    // the upper left x-position of the current quad
    int start = -1;
    for(int y = 0; y < h; y++) {
      for(int x = 0; x < w; x++) {
        // current pixel
        int pixel = data[y*w + x];

        // no current quad but found a new one
        if(start < 0 && isVisible(pixel)) {
          start = x;
        }
        // got a current quad, but it ends here
        if(start >= 0 && !isVisible(pixel)) {
          // we now check if the visibility of the next row matches the one fo the current row
          // if they are, we can extend the quad downwards
          int endY = y+1;
          boolean sameRow = true;
          while(sameRow) {
            for(int i = 0; i < w; i++) {
              int px1 = data[y*w + i];
              int px2 = data[endY*w + i];
              if(isVisible(px1) != isVisible(px2)) {
                sameRow = false;
                break;
              }
            }
            if(sameRow) {
              endY++;
            }
          }

          // create the quad
          quads.add(genQuad(buf, format, start,y, x,endY, sprite, 0.01f, EnumFacing.SOUTH, color));
          quads.add(genQuad(buf, format, start,y, x,endY, sprite, 0.01f, EnumFacing.NORTH, color));

          // update Y if all the rows match. no need to rescan
          if(endY - y > 1) {
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
  public static List<BakedQuad> convertTextureVertical(ByteBuffer buf, VertexFormat format, TextureAtlasSprite template, TextureAtlasSprite sprite, int color) {
    int w = template.getIconWidth();
    int h = template.getIconHeight();
    int[] data = template.getFrameTextureData(0)[0];
    List<BakedQuad> quads = Lists.newArrayList();

    // the upper left y-position of the current quad
    int start = -1;
    for(int x = 0; x < w; x++) {
      for(int y = 0; y < h; y++) {
        // current pixel
        int pixel = data[y*w + x];

        // no current quad but found a new one
        if(start < 0 && isVisible(pixel)) {
          start = y;
        }
        // got a current quad, but it ends here
        if(start >= 0 && !isVisible(pixel)) {
          // we now check if the visibility of the next column matches the one fo the current row
          // if they are, we can extend the quad downwards
          int endX = x+1;
          boolean sameColumn = true;
          while(sameColumn) {
            for(int i = 0; i < h; i++) {
              int px1 = data[i*w + x];
              int px2 = data[i*w + endX];
              if(isVisible(px1) != isVisible(px2)) {
                sameColumn = false;
                break;
              }
            }
            if(sameColumn) {
              endX++;
            }
          }

          // create the quad
          quads.add(genQuad(buf, format, x,start, endX,y, sprite, 0.01f, EnumFacing.SOUTH, color));
          quads.add(genQuad(buf, format, x,start, endX,y, sprite, 0.01f, EnumFacing.NORTH, color));

          // update X if all the columns match. no need to rescan
          if(endX - x > 1) {
            x = endX - 1;
          }
          // clear current quad
          start = -1;
        }
      }
    }

    return quads;
  }


  private static FaceBakery faceBakery = new FaceBakery();
  // FIXME
  // todo: change this to the generic quad-generation function in forge.. as soon as we get one
  public static BakedQuad genQuad(ByteBuffer buf, VertexFormat format, float x1, float y1, float x2, float y2, TextureAtlasSprite sprite, float offset, EnumFacing facing, int color) {
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

    BakedQuad quad;
    if(facing == EnumFacing.SOUTH) {
      offset *= -1;
      quad = ItemLayerModel.buildFrontQuad(buf, format, 0, x1, y1, x2, y2, u1, v1, u2, v2);
    } else if(facing == EnumFacing.NORTH) {
      quad = ItemLayerModel.buildBackQuad(buf, format, 0, x1, y1, x2, y2, u1, v1, u2, v2);
    } else {
      throw new UnsupportedOperationException("This function only supports front(SOUTH) and back(NORTH) quads");
    }

    int[] data = quad.getVertexData();

    /*
    //y1 = 16f-y1;
    //y2 = 16f-y2;
    //float tmp = x1;
    //x1 = x2;
    //x2 = tmp;

    float u1 = x1;
    float v1 = y1;
    float u2 = x2;
    float v2 = y2;

    BakedQuad quad = faceBakery.makeBakedQuad(new Vector3f(x1, y1, 9.5f + offset),
                                    new Vector3f(x2, y2, 6.5f - offset),
                                    new BlockPartFace(null, -1, sprite.getIconName(), new BlockFaceUV(new float[] {u1, v1, u2, v2}, 0)),
                                    sprite, facing, ModelRotation.X0_Y0, null, false, true);

    int[] data = quad.getVertexData();

    int c = 0;
    c |= ((color >> 16) & 0xFF) << 0; // red
    c |= ((color >>  8) & 0xFF) << 8; // green
    c |= ((color >>  0) & 0xFF) << 16; // blue
    c |= ((color >> 24) & 0xFF) << 24; // alpha

    /*
    int[] data = new int[28];

    float z = 8;
    if(facing == EnumFacing.NORTH)
        z -= 0.5+offset;
    if(facing == EnumFacing.SOUTH)
        z += 0.5+offset;
    
    x1 /= 16f;
    x2 /= 16f;
    y1 /= 16f;
    y2 /= 16f;
    z /= 16f;
    
    int i = 0;
    data[i*7 + 0] = Float.floatToRawIntBits(x1);
    data[i*7 + 1] = Float.floatToRawIntBits(y1);
    data[i*7 + 2] = Float.floatToRawIntBits(z);
    data[i*7 + 3] = c;
    data[i*7 + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU(x1));
    data[i*7 + 5] = Float.floatToRawIntBits(sprite.getInterpolatedV(y1));
    
    i++;
    data[i*7 + 0] = Float.floatToRawIntBits(x1);
    data[i*7 + 1] = Float.floatToRawIntBits(y2);
    data[i*7 + 2] = Float.floatToRawIntBits(z);
    data[i*7 + 3] = c;
    data[i*7 + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU(x1));
    data[i*7 + 5] = Float.floatToRawIntBits(sprite.getInterpolatedV(y2));
    
    i++;
    data[i*7 + 0] = Float.floatToRawIntBits(x2);
    data[i*7 + 1] = Float.floatToRawIntBits(y2);
    data[i*7 + 2] = Float.floatToRawIntBits(z);
    data[i*7 + 3] = c;
    data[i*7 + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU(x2));
    data[i*7 + 5] = Float.floatToRawIntBits(sprite.getInterpolatedV(y2));
    
    i++;
    data[i*7 + 0] = Float.floatToRawIntBits(x2);
    data[i*7 + 1] = Float.floatToRawIntBits(y1);
    data[i*7 + 2] = Float.floatToRawIntBits(z);
    data[i*7 + 3] = c;
    data[i*7 + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU(x2));
    data[i*7 + 5] = Float.floatToRawIntBits(sprite.getInterpolatedV(y1));
*/

    int c = 0;
    c |= ((color >> 16) & 0xFF) << 0; // red
    c |= ((color >>  8) & 0xFF) << 8; // green
    c |= ((color >>  0) & 0xFF) << 16; // blue
    c |= ((color >> 24) & 0xFF) << 24; // alpha

    // data that's the same on all vertices
    for(int i = 0; i < 4; i++) {
      data[i*7 + 2] = Float.floatToRawIntBits(Float.intBitsToFloat(data[i*7 + 2]) + offset);
      data[i*7 + 3] = c;
    }
    
    return new IColoredBakedQuad.ColoredBakedQuad(data, -1, facing);
  }


  // true if alpha != 0
  private static boolean isVisible(int color) {
    return (color >> 24 & 255) > 0;
  }
}
