package net.minecraft.src;

public class TextureWaterFX extends TextureFX
{
    /** red RGB value for water texture */
    protected float[] red = new float[256];

    /** green RGB value for water texture */
    protected float[] green = new float[256];

    /** blue RGB value for water texture */
    protected float[] blue = new float[256];

    /** alpha RGB value for water texture */
    protected float[] alpha = new float[256];
    private int tickCounter = 0;

    public TextureWaterFX()
    {
        super(Block.waterMoving.blockIndexInTexture);
    }

    public void onTick()
    {
        ++this.tickCounter;
        int var1;
        int var2;
        float var3;
        int var5;
        int var6;

        for (var1 = 0; var1 < 16; ++var1)
        {
            for (var2 = 0; var2 < 16; ++var2)
            {
                var3 = 0.0F;

                for (int var4 = var1 - 1; var4 <= var1 + 1; ++var4)
                {
                    var5 = var4 & 15;
                    var6 = var2 & 15;
                    var3 += this.red[var5 + var6 * 16];
                }

                this.green[var1 + var2 * 16] = var3 / 3.3F + this.blue[var1 + var2 * 16] * 0.8F;
            }
        }

        for (var1 = 0; var1 < 16; ++var1)
        {
            for (var2 = 0; var2 < 16; ++var2)
            {
                this.blue[var1 + var2 * 16] += this.alpha[var1 + var2 * 16] * 0.05F;

                if (this.blue[var1 + var2 * 16] < 0.0F)
                {
                    this.blue[var1 + var2 * 16] = 0.0F;
                }

                this.alpha[var1 + var2 * 16] -= 0.1F;

                if (Math.random() < 0.05D)
                {
                    this.alpha[var1 + var2 * 16] = 0.5F;
                }
            }
        }

        float[] var12 = this.green;
        this.green = this.red;
        this.red = var12;

        for (var2 = 0; var2 < 256; ++var2)
        {
            var3 = this.red[var2];

            if (var3 > 1.0F)
            {
                var3 = 1.0F;
            }

            if (var3 < 0.0F)
            {
                var3 = 0.0F;
            }

            float var13 = var3 * var3;
            var5 = (int)(32.0F + var13 * 32.0F);
            var6 = (int)(50.0F + var13 * 64.0F);
            int var7 = 255;
            int var8 = (int)(146.0F + var13 * 50.0F);

            if (this.anaglyphEnabled)
            {
                int var9 = (var5 * 30 + var6 * 59 + var7 * 11) / 100;
                int var10 = (var5 * 30 + var6 * 70) / 100;
                int var11 = (var5 * 30 + var7 * 70) / 100;
                var5 = var9;
                var6 = var10;
                var7 = var11;
            }

            this.imageData[var2 * 4 + 0] = (byte)var5;
            this.imageData[var2 * 4 + 1] = (byte)var6;
            this.imageData[var2 * 4 + 2] = (byte)var7;
            this.imageData[var2 * 4 + 3] = (byte)var8;
        }
    }
}
