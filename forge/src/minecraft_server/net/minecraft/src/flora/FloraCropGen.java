package net.minecraft.src.flora;

import java.util.Random;
import net.minecraft.src.*;

public class FloraCropGen extends WorldGenerator
{
    private int plantBlockId;

    public FloraCropGen(int i)
    {
        plantBlockId = i;
    }

    public boolean generate(World world, Random random, int i, int j, int k)
    {
        for (int l = 0; l < 64; l++)
        {
            int i1 = (i + random.nextInt(8)) - random.nextInt(8);
            int j1 = (j + random.nextInt(4)) - random.nextInt(4);
            int k1 = (k + random.nextInt(8)) - random.nextInt(8);
            if (world.isAirBlock(i1, j1, k1) && ((BlockFlower)Block.blocksList[Block.plantYellow.blockID]).canBlockStay(world, i1, j1, k1))
            {
                world.setBlockAndMetadata(i1, j1, k1, plantBlockId, 3);
            }
        }

        return true;
    }
}
