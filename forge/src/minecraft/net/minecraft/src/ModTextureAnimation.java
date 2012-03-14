package net.minecraft.src;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import org.lwjgl.opengl.GL11;

public class ModTextureAnimation extends TextureFX
{
    private final int tickRate;
    private final byte[][] images;
    private int index;
    private int ticks;

    public ModTextureAnimation(int var1, int var2, BufferedImage var3, int var4)
    {
        this(var1, 1, var2, var3, var4);
    }

    public ModTextureAnimation(int var1, int var2, int var3, BufferedImage var4, int var5)
    {
        super(var1);
        this.index = 0;
        this.ticks = 0;
        this.tileSize = var2;
        this.tileImage = var3;
        this.tickRate = var5;
        this.ticks = var5;
        this.bindImage(ModLoader.getMinecraftInstance().renderEngine);
        int var6 = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH) / 16;
        int var7 = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT) / 16;
        int var8 = var4.getWidth();
        int var9 = var4.getHeight();
        int var10 = (int)Math.floor((double)(var9 / var8));

        if (var10 <= 0)
        {
            throw new IllegalArgumentException("source has no complete images");
        }
        else
        {
            this.images = new byte[var10][];

            if (var8 != var6)
            {
                BufferedImage var11 = new BufferedImage(var6, var7 * var10, 6);
                Graphics2D var12 = var11.createGraphics();
                var12.drawImage(var4, 0, 0, var6, var7 * var10, 0, 0, var8, var9, (ImageObserver)null);
                var12.dispose();
                var4 = var11;
            }

            for (int var18 = 0; var18 < var10; ++var18)
            {
                int[] var19 = new int[var6 * var7];
                var4.getRGB(0, var7 * var18, var6, var7, var19, 0, var6);
                this.images[var18] = new byte[var6 * var7 * 4];

                for (int var13 = 0; var13 < var19.length; ++var13)
                {
                    int var14 = var19[var13] >> 24 & 255;
                    int var15 = var19[var13] >> 16 & 255;
                    int var16 = var19[var13] >> 8 & 255;
                    int var17 = var19[var13] >> 0 & 255;
                    this.images[var18][var13 * 4 + 0] = (byte)var15;
                    this.images[var18][var13 * 4 + 1] = (byte)var16;
                    this.images[var18][var13 * 4 + 2] = (byte)var17;
                    this.images[var18][var13 * 4 + 3] = (byte)var14;
                }
            }
        }
    }

    public void onTick()
    {
        if (this.ticks >= this.tickRate)
        {
            ++this.index;

            if (this.index >= this.images.length)
            {
                this.index = 0;
            }

            this.imageData = this.images[this.index];
            this.ticks = 0;
        }

        ++this.ticks;
    }
}
