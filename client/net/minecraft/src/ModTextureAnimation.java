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

/**
 * A texture override for animations, it takes a vertical image of 
 * texture frames and constantly rotates them in the texture.
 */
public class ModTextureAnimation extends FMLTextureFX
{
    private final int tickRate;
    private byte[][] images;
    private int index = 0;
    private int ticks = 0;
    
    private String targetTex = null;
    private BufferedImage imgData = null;
    
    public ModTextureAnimation(int icon, int target, BufferedImage image, int tickCount)
    {
        this(icon, 1, target, image, tickCount);
    }

    public ModTextureAnimation(int icon, int size, int target, BufferedImage image, int tickCount)    
    {
        this(icon, size, (target == 0 ? "/terrain.png" : "/gui/items.png"), image, tickCount);
    }
    
    public ModTextureAnimation(int icon, int size, String target, BufferedImage image, int tickCount)
    {
        super(icon);
        RenderEngine re = FMLClientHandler.instance().getClient().field_6315_n;
        
        targetTex = target;
        field_1129_e = size;
        field_1128_f = re.func_1070_a(target);
        
        tickRate = tickCount;
        ticks = tickCount;
        imgData = image;
    }
    
    @Override
    public void setup()
    {
        super.setup();
        
        int sWidth  = imgData.getWidth();
        int sHeight = imgData.getHeight();
        int tWidth  = tileSizeBase;
        int tHeight = tileSizeBase;
        
        
        int frames = (int)Math.floor((double)(sHeight / sWidth));

        if (frames < 1)
        {
            throw new IllegalArgumentException(String.format("Attempted to create a TextureAnimation with no complete frames: %dx%d", sWidth, sHeight));
        }
        else
        {
            images = new byte[frames][];
            BufferedImage image = imgData;
            
            if (sWidth != tWidth)
            {
                BufferedImage b = new BufferedImage(tWidth, tHeight * frames, 6);
                Graphics2D g = b.createGraphics();
                g.drawImage(imgData, 0, 0, tWidth, tHeight * frames, 0, 0, sWidth, sHeight, (ImageObserver)null);
                g.dispose();
                image = b;
            }

            for (int frame = 0; frame < frames; frame++)
            {
                int[] pixels = new int[tileSizeSquare];
                image.getRGB(0, tHeight * frame, tWidth, tHeight, pixels, 0, tWidth);
                images[frame] = new byte[tileSizeSquare << 2];

                for (int i = 0; i < pixels.length; i++)
                {
                    int i4 = i * 4;
                    images[frame][i4 + 0] = (byte)(pixels[i] >> 16 & 255);
                    images[frame][i4 + 1] = (byte)(pixels[i] >> 8  & 255);
                    images[frame][i4 + 2] = (byte)(pixels[i] >> 0  & 255);
                    images[frame][i4 + 3] = (byte)(pixels[i] >> 24 & 255);
                }
            }
        }
    }
    
    public void func_783_a()
    {
        if (++ticks >= tickRate)
        {
            if (++index >= images.length)
            {
                index = 0;
            }

            field_1127_a = images[index];
            ticks = 0;
        }
    }
}
