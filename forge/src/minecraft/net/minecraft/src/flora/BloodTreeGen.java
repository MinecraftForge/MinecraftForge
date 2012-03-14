package net.minecraft.src.flora;

import java.util.Random;
import net.minecraft.src.*;

public class BloodTreeGen extends WorldGenerator
{
    private int mdWood;
    private int mdLeaves;

    public BloodTreeGen(int i, int j)
    {
        mdWood = i;
        mdLeaves = j;
    }

    public boolean generate(World world, Random random, int i, int j, int k)
    {
        int l = findGround(world, i, j, k);
        generateRandomTree(world, random, i, l, k);
        return true;
    }

    int findGround(World world, int i, int j, int k)
    {
        int l = 0;
        int i1 = world.getBlockId(i, j - 1, k);
        if (!Block.opaqueCubeLookup[world.getBlockId(i, j, k)] && (i1 == Block.netherrack.blockID || i1 == Block.slowSand.blockID))
        {
            return j;
        }
        int k1 = 96;
        do
        {
            if (k1 < 32)
            {
                break;
            }
            int j1 = world.getBlockId(i, k1, k);
            if ((j1 == Block.netherrack.blockID || j1 == Block.slowSand.blockID) && !Block.opaqueCubeLookup[world.getBlockId(i, k1 + 1, k)])
            {
                l = k1 + 1;
                break;
            }
            k1--;
        }
        while (true);
        return l;
    }

    public boolean generateRandomTree(World world, Random random, int i, int j, int k)
    {
        int l = random.nextInt(5) + 8;
        boolean flag = true;
        if (j < 1 || j + l + 1 > 256)
        {
            return false;
        }
        for (int i1 = j; i1 <= j + 1 + l; i1++)
        {
            byte byte0 = 1;
            if (i1 == j)
            {
                byte0 = 0;
            }
            if (i1 >= (j + 1 + l) - 2)
            {
                byte0 = 2;
            }
            label0:
            for (int l1 = i - byte0; l1 <= i + byte0 && flag; l1++)
            {
                int j2 = k - byte0;
                do
                {
                    if (j2 > k + byte0 || !flag)
                    {
                        continue label0;
                    }
                    if (i1 >= 0 && i1 < 256)
                    {
                        int k2 = world.getBlockId(l1, i1, j2);
                        if (k2 != 0 && k2 != mod_FloraSoma.floraLeaves.blockID)
                        {
                            flag = false;
                            continue label0;
                        }
                    }
                    else
                    {
                        flag = false;
                        continue label0;
                    }
                    j2++;
                }
                while (true);
            }
        }

        if (!flag)
        {
            return false;
        }
        int j1 = world.getBlockId(i, j - 1, k);
        if (j1 != Block.netherrack.blockID && j1 != Block.slowSand.blockID || j >= 256 - l - 1)
        {
            return false;
        }
        world.setBlock(i, j - 1, k, Block.slowSand.blockID);
        world.setBlock(i + 1, j - 1, k, Block.slowSand.blockID);
        world.setBlock(i, j - 1, k + 1, Block.slowSand.blockID);
        world.setBlock(i + 1, j - 1, k + 1, Block.slowSand.blockID);
        for (int k1 = 0; k1 < l; k1++)
        {
            int i2 = world.getBlockId(i, j + k1, k);
            if (i2 == 0 || i2 == mod_FloraSoma.floraLeaves.blockID)
            {
                setBlockAndMetadata(world, i, j + k1, k, mod_FloraSoma.redwood.blockID, mdWood);
                setBlockAndMetadata(world, i + 1, j + k1, k, mod_FloraSoma.redwood.blockID, mdWood);
                setBlockAndMetadata(world, i, j + k1, k + 1, mod_FloraSoma.redwood.blockID, mdWood);
                setBlockAndMetadata(world, i + 1, j + k1, k + 1, mod_FloraSoma.redwood.blockID, mdWood);
            }
        }

        genBranch(world, random, i, j, k, l, 1);
        genBranch(world, random, i + 1, j, k, l, 2);
        genBranch(world, random, i, j, k + 1, l, 3);
        genBranch(world, random, i + 1, j, k + 1, l, 4);
        genStraightBranch(world, random, i, j, k, l, 1);
        genStraightBranch(world, random, i + 1, j, k, l, 2);
        genStraightBranch(world, random, i, j, k + 1, l, 3);
        genStraightBranch(world, random, i + 1, j, k + 1, l, 4);
        return true;
    }

    private void genBranch(World world, Random random, int i, int j, int k, int l, int i1)
    {
        int j1 = i;
        int k1 = j + l;
        int l1 = k;
        byte byte0 = 0;
        byte byte1 = 0;
        switch (i1)
        {
            case 1:
                byte0 = 1;
                byte1 = 1;
                break;

            case 2:
                byte0 = -1;
                byte1 = 1;
                break;

            case 3:
                byte0 = 1;
                byte1 = -1;
                break;

            case 4:
                byte0 = -1;
                byte1 = -1;
                break;
        }
        int i2 = random.nextInt(15);
        for (int j2 = 4; j2 > 0; j2--)
        {
            if (i2 % 3 != 0)
            {
                j1 += byte0;
            }
            if (i2 % 3 != 1)
            {
                l1 += byte1;
            }
            k1 += i2 % 3 - 1;
            generateNode(world, random, j1, k1, l1);
            i2 = random.nextInt(15);
        }
    }

    private void genStraightBranch(World world, Random random, int i, int j, int k, int l, int i1)
    {
        int j1 = i;
        int k1 = j + l;
        int l1 = k;
        byte byte0 = 0;
        byte byte1 = 0;
        switch (i1)
        {
            case 1:
                byte0 = 1;
                byte1 = 0;
                break;

            case 2:
                byte0 = 0;
                byte1 = 1;
                break;

            case 3:
                byte0 = -1;
                byte1 = 0;
                break;

            case 4:
                byte0 = 0;
                byte1 = -1;
                break;
        }
        int i2 = random.nextInt(5);
        for (int j2 = 4; j2 > 0; j2--)
        {
            if (byte0 == 0)
            {
                j1 = (j1 + random.nextInt(3)) - 1;
                l1 += byte1;
            }
            if (byte1 == 0)
            {
                j1 += byte0;
                l1 = (l1 + random.nextInt(3)) - 1;
            }
            k1 += i2 % 3 - 1;
            generateNode(world, random, j1, k1, l1);
            i2 = random.nextInt(5);
        }
    }

    public boolean generateNode(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, mdWood);
        for (int l = i - 1; l <= i + 1; l++)
        {
            for (int k1 = k - 1; k1 <= k + 1; k1++)
            {
                for (int j2 = j - 1; j2 <= j + 1; j2++)
                {
                    int i3 = world.getBlockId(l, j2, k1);
                    if (i3 != mod_FloraSoma.floraLeaves.blockID && !Block.opaqueCubeLookup[i3])
                    {
                        setBlockAndMetadata(world, l, j2, k1, mod_FloraSoma.floraLeaves.blockID, mdLeaves);
                    }
                }
            }
        }

        for (int i1 = i - 1; i1 <= i + 1; i1++)
        {
            for (int l1 = k - 2; l1 <= k + 2; l1++)
            {
                int k2 = world.getBlockId(i1, j, l1);
                if (k2 != mod_FloraSoma.floraLeaves.blockID && !Block.opaqueCubeLookup[k2])
                {
                    setBlockAndMetadata(world, i1, j, l1, mod_FloraSoma.floraLeaves.blockID, mdLeaves);
                }
            }
        }

        for (int j1 = i - 2; j1 <= i + 2; j1++)
        {
            for (int i2 = k - 1; i2 <= k + 1; i2++)
            {
                int l2 = world.getBlockId(j1, j+1, i2);
                if (l2 != mod_FloraSoma.floraLeaves.blockID && !Block.opaqueCubeLookup[l2])
                {
                    setBlockAndMetadata(world, j1, j, i2, mod_FloraSoma.floraLeaves.blockID, mdLeaves);
                }
            }
        }

        return true;
    }
}
