package net.minecraft.src.flora;

import java.util.Random;
import net.minecraft.src.*;

public class BerryBushGen extends WorldGenerator
{
    private int metadata;

    public BerryBushGen(int i)
    {
        metadata = i;
    }

    public boolean generate(World world, Random random, int i, int j, int k)
    {
        int l = findGround(world, i, j, k);
        int i1 = i;
        int j1 = k;
        if (l != 0)
        {
            generateNode(world, random, i1, l, j1);
        }
        return true;
    }

    int findCloseGround(World world, int i, int j, int k)
    {
        int l = 0;
        int i1 = world.getBlockId(i, j - 1, k);
        if (!Block.opaqueCubeLookup[world.getBlockId(i, j, k)] && (i1 == Block.dirt.blockID || i1 == Block.grass.blockID))
        {
            return j;
        }
        int k1 = j + 2;
        do
        {
            if (k1 < j - 2)
            {
                break;
            }
            int j1 = world.getBlockId(i, k1, k);
            if (j1 == Block.dirt.blockID || j1 == Block.grass.blockID)
            {
                if (!Block.opaqueCubeLookup[world.getBlockId(i, k1 + 1, k)])
                {
                    l = k1 + 1;
                }
                break;
            }
            k1--;
        }
        while (true);
        return l;
    }

    int findGround(World world, int i, int j, int k)
    {
        int l = 0;
        int i1 = world.getBlockId(i, j - 1, k);
        if (!Block.opaqueCubeLookup[world.getBlockId(i, j, k)] && (i1 == Block.dirt.blockID || i1 == Block.grass.blockID))
        {
            return j;
        }
        int k1 = 96;
        do
        {
            if (k1 < 64)
            {
                break;
            }
            int j1 = world.getBlockId(i, k1, k);
            if (j1 == Block.dirt.blockID || j1 == Block.grass.blockID)
            {
                if (!Block.opaqueCubeLookup[world.getBlockId(i, k1 + 1, k)])
                {
                    l = k1 + 1;
                }
                break;
            }
            k1--;
        }
        while (true);
        return l;
    }

    public boolean generateNode(World world, Random random, int i, int j, int k)
    {
        for (int l = i - 2; l <= i + 2; l++)
        {
            for (int l1 = k - 1; l1 <= k + 1; l1++)
            {
                for (int l2 = j - 1; l2 <= j; l2++)
                {
                    if (Block.opaqueCubeLookup[world.getBlockId(l, l2, l1)])
                    {
                        continue;
                    }
                    int l3 = random.nextInt(5);
                    int l4 = 0;
                    if (l3 == 4)
                    {
                        l4 = 1;
                    }
                    setBlockAndMetadata(world, l, l2, l1, mod_FloraSoma.berryBush.blockID, metadata + 8 + l4 * 4);
                }
            }
        }

        for (int i1 = i - 1; i1 <= i + 1; i1++)
        {
            for (int i2 = k - 2; i2 <= k - 2; i2++)
            {
                for (int i3 = j - 1; i3 <= j; i3++)
                {
                    if (Block.opaqueCubeLookup[world.getBlockId(i1, i3, i2)])
                    {
                        continue;
                    }
                    int i4 = random.nextInt(5);
                    int i5 = 0;
                    if (i4 == 4)
                    {
                        i5 = 1;
                    }
                    setBlockAndMetadata(world, i1, i3, i2, mod_FloraSoma.berryBush.blockID, metadata + 8 + i5 * 4);
                }
            }
        }

        for (int j1 = i - 1; j1 <= i + 1; j1++)
        {
            for (int j2 = k + 2; j2 <= k + 2; j2++)
            {
                for (int j3 = j - 1; j3 <= j; j3++)
                {
                    if (Block.opaqueCubeLookup[world.getBlockId(j1, j3, j2)])
                    {
                        continue;
                    }
                    int j4 = random.nextInt(5);
                    int j5 = 0;
                    if (j4 == 4)
                    {
                        j5 = 1;
                    }
                    setBlockAndMetadata(world, j1, j3, j2, mod_FloraSoma.berryBush.blockID, metadata + 8 + j5 * 4);
                }
            }
        }

        for (int k1 = i - 1; k1 <= i + 1; k1++)
        {
            for (int k2 = k - 1; k2 <= k + 1; k2++)
            {
                int k3 = j + 1;
                if (Block.opaqueCubeLookup[world.getBlockId(k1, k3, k2)])
                {
                    continue;
                }
                int k4 = random.nextInt(5);
                int k5 = 0;
                if (k4 == 4)
                {
                    k5 = 1;
                }
                setBlockAndMetadata(world, k1, k3, k2, mod_FloraSoma.berryBush.blockID, metadata + 8 + k5 * 4);
            }
        }

        return true;
    }
}
