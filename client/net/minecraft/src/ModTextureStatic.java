/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.minecraft.src;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.FMLTextureFX;
import cpw.mods.fml.common.FMLCommonHandler;
import static org.lwjgl.opengl.GL11.*;

public class ModTextureStatic extends FMLTextureFX
{
    private boolean oldanaglyph = false;
    private int[] pixels = null;
    private String targetTex = null;
    private int storedSize;
    
    
    public ModTextureStatic(int icon, int target, BufferedImage image)
    {
        this(icon, 1, target, image);
    }

    public ModTextureStatic(int icon, int size, int target, BufferedImage image)    
    {
        this(icon, size, (target == 0 ? "/terrain.png" : "/gui/items.png"), image);
    }
    
    public ModTextureStatic(int icon, int size, String target, BufferedImage image)
    {
        super(icon);
        RenderEngine re = FMLClientHandler.instance().getClient().field_6315_n;
        
        targetTex = target;
        storedSize = size;
        field_1129_e = size;
        field_1128_f = re.func_1070_a(target);
        
        func_782_a(re);

        int sWidth  = image.getWidth();
        int sHeight = image.getHeight();
        int tWidth  = GL11.glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH ) >> 4;
        int tHeight = GL11.glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT) >> 4;

        if (tWidth != tileSizeBase || tHeight != tileSizeBase)
        {
            log.warning(String.format("Override %s is not applied - there is a mismatch between the underlying texture (%s) size %d,%d and the current texture tile size %d", target, tWidth, tHeight, tileSizeBase));
            errored=true;
            return;
        }
        pixels = new int[tileSizeSquare];
        
        if (tWidth == sWidth && tHeight == sHeight)
        {
            image.getRGB(0, 0, sWidth, sHeight, pixels, 0, sWidth);
        }
        else
        {
            BufferedImage tmp = new BufferedImage(tWidth, tHeight, 6);
            Graphics2D gfx = tmp.createGraphics();
            gfx.drawImage(image, 0, 0, tWidth, tHeight, 0, 0, sWidth, sHeight, (ImageObserver)null);
            tmp.getRGB(0, 0, tWidth, tHeight, pixels, 0, tWidth);
            gfx.dispose();
        }

        update();
    }
    
    public void func_783_a()
    {
        // Force the tile size to zero: generally we only need to stamp our static image once
        field_1129_e = 0;
        if (oldanaglyph != field_1131_c)
        {
            update();
            field_1129_e = storedSize;
        }
    }

    public void func_782_a(RenderEngine p_782_1_)
    {
        GL11.glBindTexture(GL_TEXTURE_2D, p_782_1_.func_1070_a(targetTex));
    }
    
    public void update()
    {
        for (int idx = 0; idx < pixels.length; idx++)
        {
            int i = idx * 4;
            int a = pixels[idx] >> 24 & 255;
            int r = pixels[idx] >> 16 & 255;
            int g = pixels[idx] >> 8 & 255;
            int b = pixels[idx] >> 0 & 255;

            if (field_1131_c)
            {
                r = g = b = (r + g + b) / 3;
            }

            field_1127_a[i + 0] = (byte)r;
            field_1127_a[i + 1] = (byte)g;
            field_1127_a[i + 2] = (byte)b;
            field_1127_a[i + 3] = (byte)a;
        }

        oldanaglyph = field_1131_c;
    }
    
    //Implementation of http://scale2x.sourceforge.net/algorithm.html
    public static BufferedImage scale2x(BufferedImage image)
    {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage tmp = new BufferedImage(w * 2, h * 2, 2);

        for (int x = 0; x < h; ++x)
        {
            int x2 = x * 2;
            for (int y = 0; y < w; ++y)
            {
                int y2 = y * 2;
                int E = image.getRGB(y, x);
                int D = (x == 0     ? E : image.getRGB(y,     x - 1));
                int B = (y == 0     ? E : image.getRGB(y - 1, x    ));
                int H = (y >= w - 1 ? E : image.getRGB(y + 1, x    ));
                int F = (x >= h - 1 ? E : image.getRGB(y,     x + 1));

                int e0, e1, e2, e3;

                if (B != H && D != F)
                {
                    e0 = D == B ? D : E;
                    e1 = B == F ? F : E;
                    e2 = D == H ? D : E;
                    e3 = H == F ? F : E;
                }
                else
                {
                    e0 = e1 = e2 = e3 = E;
                }

                tmp.setRGB(y2,     x2,     e0);
                tmp.setRGB(y2 + 1, x2,     e1);
                tmp.setRGB(y2,     x2 + 1, e2);
                tmp.setRGB(y2 + 1, x2 + 1, e3);
            }
        }

        return tmp;
    }

    
    @Override
    public String toString() {
        return String.format("ModTextureStatic %s @ %d", targetTex, field_1126_b);
    }
}
