package net.minecraft.src;

public class TextureFlamesFX extends TextureFX
{
    protected float[] field_1133_g = new float[320];
    protected float[] field_1132_h = new float[320];

    public TextureFlamesFX(int par1)
    {
        super(Block.fire.blockIndexInTexture + par1 * 16);
    }

    public void onTick()
    {
        int var3;
        float var4;
        int var6;

        for (int var1 = 0; var1 < 16; ++var1)
        {
            for (int var2 = 0; var2 < 20; ++var2)
            {
                var3 = 18;
                var4 = this.field_1133_g[var1 + (var2 + 1) % 20 * 16] * (float)var3;

                for (int var5 = var1 - 1; var5 <= var1 + 1; ++var5)
                {
                    for (var6 = var2; var6 <= var2 + 1; ++var6)
                    {
                        if (var5 >= 0 && var6 >= 0 && var5 < 16 && var6 < 20)
                        {
                            var4 += this.field_1133_g[var5 + var6 * 16];
                        }

                        ++var3;
                    }
                }

                this.field_1132_h[var1 + var2 * 16] = var4 / ((float)var3 * 1.0600001F);

                if (var2 >= 19)
                {
                    this.field_1132_h[var1 + var2 * 16] = (float)(Math.random() * Math.random() * Math.random() * 4.0D + Math.random() * 0.10000000149011612D + 0.20000000298023224D);
                }
            }
        }

        float[] var13 = this.field_1132_h;
        this.field_1132_h = this.field_1133_g;
        this.field_1133_g = var13;

        for (var3 = 0; var3 < 256; ++var3)
        {
            var4 = this.field_1133_g[var3] * 1.8F;

            if (var4 > 1.0F)
            {
                var4 = 1.0F;
            }

            if (var4 < 0.0F)
            {
                var4 = 0.0F;
            }

            var6 = (int)(var4 * 155.0F + 100.0F);
            int var7 = (int)(var4 * var4 * 255.0F);
            int var8 = (int)(var4 * var4 * var4 * var4 * var4 * var4 * var4 * var4 * var4 * var4 * 255.0F);
            short var9 = 255;

            if (var4 < 0.5F)
            {
                var9 = 0;
            }

            float var14 = (var4 - 0.5F) * 2.0F;

            if (this.anaglyphEnabled)
            {
                int var10 = (var6 * 30 + var7 * 59 + var8 * 11) / 100;
                int var11 = (var6 * 30 + var7 * 70) / 100;
                int var12 = (var6 * 30 + var8 * 70) / 100;
                var6 = var10;
                var7 = var11;
                var8 = var12;
            }

            this.imageData[var3 * 4 + 0] = (byte)var6;
            this.imageData[var3 * 4 + 1] = (byte)var7;
            this.imageData[var3 * 4 + 2] = (byte)var8;
            this.imageData[var3 * 4 + 3] = (byte)var9;
        }
    }
}
