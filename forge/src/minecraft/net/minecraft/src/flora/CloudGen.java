package net.minecraft.src.flora;

import java.util.Random;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

public class CloudGen extends WorldGenerator
{
    private int bID;
    private int md;
    private int numberOfBlocks;
    private boolean isFlat;

    public CloudGen(int i, int j, int k, boolean flag)
    {
        bID = i;
        md = j;
        numberOfBlocks = k;
        isFlat = flag;
    }

    public boolean generate(World world, Random random, int i, int j, int k)
    {
        int l = random.nextInt(3) - 1;
        int i1 = random.nextInt(3) - 1;
        for (int j1 = 0; j1 < numberOfBlocks; j1++)
        {
            i += (random.nextInt(3) - 1) + l;
            k += (random.nextInt(3) - 1) + i1;
            if (random.nextBoolean() && !isFlat || isFlat && random.nextInt(10) == 0)
            {
                j += random.nextInt(3) - 1;
            }
            for (int k1 = i; k1 < i + random.nextInt(4) + 3 * (isFlat ? 3 : 1); k1++)
            {
                for (int l1 = j; l1 < j + random.nextInt(1) + 2; l1++)
                {
                    for (int i2 = k; i2 < k + random.nextInt(4) + 3 * (isFlat ? 3 : 1); i2++)
                    {
                        if (world.getBlockId(k1, l1, i2) == 0 && Math.abs(k1 - i) + Math.abs(l1 - j) + Math.abs(i2 - k) < 4 * (isFlat ? 3 : 1) + random.nextInt(2))
                        {
                            setBlockAndMetadata(world, k1, l1, i2, bID, md);
                        }
                    }
                }
            }
        }

        return true;
    }
}
