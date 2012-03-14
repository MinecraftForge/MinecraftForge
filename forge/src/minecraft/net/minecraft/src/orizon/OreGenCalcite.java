package net.minecraft.src.orizon;
import net.minecraft.src.*;
import java.util.Random;

public class OreGenCalcite extends WorldGenerator
{
    private int bID;
    private int md;
    private int size;

    public OreGenCalcite(int blockID, int metadata, int sizeOfVein)
    {
        bID = blockID;
        md = metadata;
        size = sizeOfVein;
    }

    public boolean generate(World world, Random random, int i, int j, int k)
    {
        float f = random.nextFloat() * 3.141593F;
        double d = (float)(i + 8) + (MathHelper.sin(f) * (float)size) / 8F;
        double d1 = (float)(i + 8) - (MathHelper.sin(f) * (float)size) / 8F;
        double d2 = (float)(k + 8) + (MathHelper.cos(f) * (float)size) / 8F;
        double d3 = (float)(k + 8) - (MathHelper.cos(f) * (float)size) / 8F;
        double d4 = (j + random.nextInt(3)) - 2;
        double d5 = (j + random.nextInt(3)) - 2;
        for (int l = 0; l <= size; l++)
        {
            double d6 = d + ((d1 - d) * (double)l) / (double)size;
            double d7 = d4 + ((d5 - d4) * (double)l) / (double)size;
            double d8 = d2 + ((d3 - d2) * (double)l) / (double)size;
            double d9 = (random.nextDouble() * (double)size) / 16D;
            double d10 = (double)(MathHelper.sin(((float)l * 3.141593F) / (float)size) + 1.0F) * d9 + 1.0D;
            double d11 = (double)(MathHelper.sin(((float)l * 3.141593F) / (float)size) + 1.0F) * d9 + 1.0D;
            int i1 = MathHelper.floor_double(d6 - d10 / 2D);
            int j1 = MathHelper.floor_double(d7 - d11 / 2D);
            int k1 = MathHelper.floor_double(d8 - d10 / 2D);
            int l1 = MathHelper.floor_double(d6 + d10 / 2D);
            int i2 = MathHelper.floor_double(d7 + d11 / 2D);
            int j2 = MathHelper.floor_double(d8 + d10 / 2D);
            for (int xGen = i1; xGen <= l1; xGen++)
            {
                double d12 = (((double)xGen + 0.5D) - d6) / (d10 / 2D);
                if (d12 * d12 >= 1.0D)
                {
                    continue;
                }
                for (int yGen = j1; yGen <= i2; yGen++)
                {
                    double d13 = (((double)yGen + 0.5D) - d7) / (d11 / 2D);
                    if (d12 * d12 + d13 * d13 >= 1.0D)
                    {
                        continue;
                    }
                    for (int zGen = k1; zGen <= j2; zGen++)
                    {
                        double d14 = (((double)zGen + 0.5D) - d8) / (d10 / 2D);
                        int blID = world.getBlockId(xGen, yGen, zGen);
                        if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D && blID == mod_Orizon.calciteOre.blockID)
                        {
                            world.setBlockAndMetadata(xGen, yGen, zGen, bID, md);
                        }
                    }
                }
            }
        }

        return true;
    }
}
