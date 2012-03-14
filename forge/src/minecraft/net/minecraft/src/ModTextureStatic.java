package net.minecraft.src;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import org.lwjgl.opengl.GL11;

public class ModTextureStatic extends TextureFX
{
    private boolean oldanaglyph;
    private int[] pixels;

    public ModTextureStatic(int var1, int var2, BufferedImage var3)
    {
        this(var1, 1, var2, var3);
    }

    public ModTextureStatic(int var1, int var2, int var3, BufferedImage var4)
    {
        super(var1);
        this.pixels = null;
        this.tileSize = var2;
        this.tileImage = var3;
        this.bindImage(ModLoader.getMinecraftInstance().renderEngine);
        int var5 = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH) / 16;
        int var6 = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT) / 16;
        int var7 = var4.getWidth();
        int var8 = var4.getHeight();
        this.pixels = new int[var5 * var6];
        this.imageData = new byte[var5 * var6 * 4];

        if (var7 == var8 && var7 == var5)
        {
            var4.getRGB(0, 0, var7, var8, this.pixels, 0, var7);
        }
        else
        {
            BufferedImage var9 = new BufferedImage(var5, var6, 6);
            Graphics2D var10 = var9.createGraphics();
            var10.drawImage(var4, 0, 0, var5, var6, 0, 0, var7, var8, (ImageObserver)null);
            var9.getRGB(0, 0, var5, var6, this.pixels, 0, var5);
            var10.dispose();
        }

        this.update();
    }

    public void update()
    {
        for (int var1 = 0; var1 < this.pixels.length; ++var1)
        {
            int var2 = this.pixels[var1] >> 24 & 255;
            int var3 = this.pixels[var1] >> 16 & 255;
            int var4 = this.pixels[var1] >> 8 & 255;
            int var5 = this.pixels[var1] >> 0 & 255;

            if (this.anaglyphEnabled)
            {
                int var6 = (var3 + var4 + var5) / 3;
                var5 = var6;
                var4 = var6;
                var3 = var6;
            }

            this.imageData[var1 * 4 + 0] = (byte)var3;
            this.imageData[var1 * 4 + 1] = (byte)var4;
            this.imageData[var1 * 4 + 2] = (byte)var5;
            this.imageData[var1 * 4 + 3] = (byte)var2;
        }

        this.oldanaglyph = this.anaglyphEnabled;
    }

    public void onTick()
    {
        if (this.oldanaglyph != this.anaglyphEnabled)
        {
            this.update();
        }
    }

    public static BufferedImage scale2x(BufferedImage var0)
    {
        int var10 = var0.getWidth();
        int var11 = var0.getHeight();
        BufferedImage var12 = new BufferedImage(var10 * 2, var11 * 2, 2);

        for (int var13 = 0; var13 < var11; ++var13)
        {
            for (int var14 = 0; var14 < var10; ++var14)
            {
                int var1 = var0.getRGB(var14, var13);
                int var6;

                if (var13 == 0)
                {
                    var6 = var1;
                }
                else
                {
                    var6 = var0.getRGB(var14, var13 - 1);
                }

                int var7;

                if (var14 == 0)
                {
                    var7 = var1;
                }
                else
                {
                    var7 = var0.getRGB(var14 - 1, var13);
                }

                int var8;

                if (var14 >= var10 - 1)
                {
                    var8 = var1;
                }
                else
                {
                    var8 = var0.getRGB(var14 + 1, var13);
                }

                int var9;

                if (var13 >= var11 - 1)
                {
                    var9 = var1;
                }
                else
                {
                    var9 = var0.getRGB(var14, var13 + 1);
                }

                int var2;
                int var3;
                int var4;
                int var5;

                if (var6 != var9 && var7 != var8)
                {
                    var2 = var7 == var6 ? var7 : var1;
                    var3 = var6 == var8 ? var8 : var1;
                    var4 = var7 == var9 ? var7 : var1;
                    var5 = var9 == var8 ? var8 : var1;
                }
                else
                {
                    var2 = var1;
                    var3 = var1;
                    var4 = var1;
                    var5 = var1;
                }

                var12.setRGB(var14 * 2, var13 * 2, var2);
                var12.setRGB(var14 * 2 + 1, var13 * 2, var3);
                var12.setRGB(var14 * 2, var13 * 2 + 1, var4);
                var12.setRGB(var14 * 2 + 1, var13 * 2 + 1, var5);
            }
        }

        return var12;
    }
}
