package net.minecraft.src.flora;

import java.util.Random;
import net.minecraft.src.*;

public class RedwoodTreeGen extends WorldGenerator
{
    static final byte otherCoordPairs[] =
    {
        2, 0, 0, 1, 2, 1
    };
    Random rand;
    World worldObj;
    int basePos[] =
    {
        0, 0, 0
    };
    int heightLimit;
    int height;
    double heightAttenuation;
    double field_875_h;
    double field_874_i;
    double field_873_j;
    double field_872_k;
    int trunkSize;
    int heightLimitLimit;
    int leafDistanceLimit;
    int leafNodes[][];

    public RedwoodTreeGen(boolean flag)
    {
        super(flag);
        rand = new Random();
        heightLimit = 0;
        heightAttenuation = 0.61799999999999999D;
        field_875_h = 1.0D;
        field_874_i = 0.38100000000000001D;
        field_873_j = 1.0D;
        field_872_k = 1.0D;
        trunkSize = 1;
        heightLimitLimit = 12;
        leafDistanceLimit = 4;
    }

    public boolean generate(World world, Random random, int i, int j, int k)
    {
        int height = random.nextInt(55) + 50;
        worldObj = world;
        long l1 = random.nextLong();
        rand.setSeed(l1);
        basePos[0] = i;
        basePos[1] = j;
        basePos[2] = k;
        if (heightLimit == 0)
        {
            heightLimit = 5 + rand.nextInt(heightLimitLimit);
        }
        int i1 = world.getBlockId(i, j - 1, k);
        if (!Block.opaqueCubeLookup[world.getBlockId(i, j, k)] && (i1 == Block.dirt.blockID || i1 == Block.grass.blockID))
        {
            int j1 = height;
            int k1 = j;
            if (height > 85)
            {
                if (j1 >= 82)
                {
                    for (int i2 = k1; i2 <= (j + j1) - 82; i2++)
                    {
                        genRing13(world, random, i, k1, k);
                        j1--;
                        k1++;
                    }
                }
                if (j1 >= 60)
                {
                    for (int j2 = k1; j2 <= (j + j1) - 60; j2++)
                    {
                        genRing12(world, random, i, k1, k);
                        j1--;
                        k1++;
                    }
                }
                if (j1 >= 40)
                {
                    for (int k2 = k1; k2 <= (j + j1) - 40; k2++)
                    {
                        genRing11(world, random, i, k1, k);
                        j1--;
                        growLargeLowerBranch(world, random, i, k1, k);
                        k1++;
                    }
                }
                if (j1 >= 20)
                {
                    for (int l2 = k1; l2 <= (j + j1) - 20; l2++)
                    {
                        genRing10(world, random, i, k1, k);
                        j1--;
                        growLargeUpperBranch(world, random, i, k1, k);
                        k1++;
                    }
                }
                if (j1 >= 0)
                {
                    for (int i3 = k1; i3 <= (j + j1) - 0; i3++)
                    {
                        genRing9(world, random, i, k1, k);
                        j1--;
                        growLargeUpperBranch(world, random, i, k1, k);
                        k1++;
                    }
                }
                growBigRoots(world, random, i, j - 1, k);
                growLargeTop(world, random, i, k1, k);
            }
            else if (height > 70)
            {
                if (j1 >= 70)
                {
                    for (int j3 = k1; j3 <= (j + j1) - 70; j3++)
                    {
                        genRing11(world, random, i, k1, k);
                        j1--;
                        k1++;
                    }
                }
                if (j1 >= 58)
                {
                    for (int k3 = k1; k3 <= (j + j1) - 58; k3++)
                    {
                        genRing10(world, random, i, k1, k);
                        j1--;
                        k1++;
                    }
                }
                if (j1 >= 35)
                {
                    for (int l3 = k1; l3 <= (j + j1) - 35; l3++)
                    {
                        genRing9(world, random, i, k1, k);
                        j1--;
                        growMediumLowerBranch(world, random, i, k1, k);
                        k1++;
                    }
                }
                if (j1 >= 15)
                {
                    for (int i4 = k1; i4 <= (j + j1) - 15; i4++)
                    {
                        genRing8(world, random, i, k1, k);
                        j1--;
                        growMediumUpperBranch(world, random, i, k1, k);
                        k1++;
                    }
                }
                if (j1 >= 0)
                {
                    for (int j4 = k1; j4 <= (j + j1) - 0; j4++)
                    {
                        genRing7(world, random, i, k1, k);
                        j1--;
                        growMediumUpperBranch(world, random, i, k1, k);
                        k1++;
                    }
                }
                growMediumRoots(world, random, i, j - 1, k);
                growMediumTop(world, random, i, k1, k);
            }
            else
            {
                if (j1 >= 50)
                {
                    for (int k4 = k1; k4 <= (j + j1) - 50; k4++)
                    {
                        genRing9(world, random, i, k1, k);
                        j1--;
                        k1++;
                    }
                }
                if (j1 >= 25)
                {
                    for (int l4 = k1; l4 <= (j + j1) - 25; l4++)
                    {
                        genRing8(world, random, i, k1, k);
                        j1--;
                        growSmallLowerBranch(world, random, i, k1, k);
                        k1++;
                    }
                }
                if (j1 >= 0)
                {
                    for (int i5 = k1; i5 <= (j + j1) - 0; i5++)
                    {
                        genRing7(world, random, i, k1, k);
                        j1--;
                        growSmallUpperBranch(world, random, i, k1, k);
                        k1++;
                    }
                }
                growSmallRoots(world, random, i, j - 1, k);
                growSmallTop(world, random, i, k1, k);
            }
        }
        return true;
    }

    public boolean growSmallUpperBranch(World world, Random random, int i, int j, int k)
    {
        basePos[0] = (i + random.nextInt(12)) - 6;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(12)) - 6;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(12)) - 6;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(12)) - 6;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        return false;
    }

    public boolean growSmallLowerBranch(World world, Random random, int i, int j, int k)
    {
        basePos[0] = (i + random.nextInt(16)) - 8;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(16)) - 8;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(16)) - 8;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(16)) - 8;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        return false;
    }

    public boolean growSmallTop(World world, Random random, int i, int j, int k)
    {
        basePos[0] = (i + random.nextInt(4)) - 2;
        basePos[1] = j + 4;
        basePos[2] = (k + random.nextInt(4)) - 2;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(4)) - 2;
        basePos[1] = j + 4;
        basePos[2] = (k + random.nextInt(4)) - 2;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(4)) - 2;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(4)) - 2;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(4)) - 2;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(4)) - 2;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        return false;
    }

    public boolean growMediumUpperBranch(World world, Random random, int i, int j, int k)
    {
        basePos[0] = (i + random.nextInt(14)) - 7;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(14)) - 7;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(14)) - 7;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(14)) - 7;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        if (random.nextInt(2) == 0)
        {
            basePos[0] = (i + random.nextInt(14)) - 7;
            basePos[1] = j;
            basePos[2] = (k + random.nextInt(14)) - 7;
            generateLeafNodeList();
            generateLeaves();
            generateLeafNodeBases();
        }
        return false;
    }

    public boolean growMediumLowerBranch(World world, Random random, int i, int j, int k)
    {
        basePos[0] = (i + random.nextInt(18)) - 9;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(18)) - 9;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(18)) - 9;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(18)) - 9;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        return false;
    }

    public boolean growMediumTop(World world, Random random, int i, int j, int k)
    {
        basePos[0] = (i + random.nextInt(6)) - 3;
        basePos[1] = j + 4;
        basePos[2] = (k + random.nextInt(6)) - 3;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(6)) - 3;
        basePos[1] = j + 4;
        basePos[2] = (k + random.nextInt(6)) - 3;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(6)) - 3;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(6)) - 3;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(6)) - 3;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(6)) - 3;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        return false;
    }

    public boolean growLargeUpperBranch(World world, Random random, int i, int j, int k)
    {
        basePos[0] = (i + random.nextInt(14)) - 7;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(14)) - 7;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        if (random.nextInt(2) == 0)
        {
            basePos[0] = (i + random.nextInt(14)) - 7;
            basePos[1] = j;
            basePos[2] = (k + random.nextInt(14)) - 7;
            generateLeafNodeList();
            generateLeaves();
            generateLeafNodeBases();
        }
        basePos[0] = (i + random.nextInt(14)) - 7;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(14)) - 7;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        return false;
    }

    public boolean growLargeLowerBranch(World world, Random random, int i, int j, int k)
    {
        basePos[0] = (i + random.nextInt(18)) - 9;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(18)) - 9;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(18)) - 9;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(18)) - 9;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        return false;
    }

    public boolean growLargeTop(World world, Random random, int i, int j, int k)
    {
        basePos[0] = (i + random.nextInt(8)) - 4;
        basePos[1] = j + 4;
        basePos[2] = (k + random.nextInt(8)) - 4;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(8)) - 4;
        basePos[1] = j + 4;
        basePos[2] = (k + random.nextInt(8)) - 4;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(8)) - 4;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(8)) - 4;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        basePos[0] = (i + random.nextInt(8)) - 4;
        basePos[1] = j;
        basePos[2] = (k + random.nextInt(8)) - 4;
        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        return false;
    }

    public boolean growSmallRoots(World world, Random random, int i, int j, int k)
    {
        genRing9(world, random, i, j, k);
        smallRoot1(world, random, i, j - 1, k);
        smallRoot1(world, random, i, j - 2, k);
        smallRoot1(world, random, i, j - 3, k);
        smallRoot2(world, random, i, j - 4, k);
        smallRoot2(world, random, i, j - 5, k);
        smallRoot3(world, random, i, j - 6, k);
        smallRoot3(world, random, i, j - 7, k);
        smallRoot3(world, random, i, j - 8, k);
        smallRoot3(world, random, i, j - 9, k);
        smallRoot4(world, random, i, j - 10, k);
        smallRoot4(world, random, i, j - 11, k);
        return true;
    }

    public boolean growMediumRoots(World world, Random random, int i, int j, int k)
    {
        genRing11(world, random, i, j, k);
        mediumRoot1(world, random, i, j - 1, k);
        mediumRoot1(world, random, i, j - 2, k);
        mediumRoot1(world, random, i, j - 3, k);
        mediumRoot2(world, random, i, j - 4, k);
        mediumRoot2(world, random, i, j - 5, k);
        mediumRoot3(world, random, i, j - 6, k);
        mediumRoot3(world, random, i, j - 7, k);
        mediumRoot3(world, random, i, j - 8, k);
        mediumRoot3(world, random, i, j - 9, k);
        mediumRoot4(world, random, i, j - 10, k);
        mediumRoot4(world, random, i, j - 11, k);
        mediumRoot5(world, random, i, j - 12, k);
        mediumRoot5(world, random, i, j - 13, k);
        mediumRoot5(world, random, i, j - 14, k);
        return true;
    }

    public boolean growBigRoots(World world, Random random, int i, int j, int k)
    {
        genRing13(world, random, i, j, k);
        bigRoot1(world, random, i, j - 1, k);
        bigRoot1(world, random, i, j - 2, k);
        bigRoot1(world, random, i, j - 3, k);
        bigRoot2(world, random, i, j - 4, k);
        bigRoot2(world, random, i, j - 5, k);
        bigRoot3(world, random, i, j - 6, k);
        bigRoot3(world, random, i, j - 7, k);
        bigRoot3(world, random, i, j - 8, k);
        bigRoot3(world, random, i, j - 9, k);
        bigRoot4(world, random, i, j - 10, k);
        bigRoot4(world, random, i, j - 11, k);
        bigRoot5(world, random, i, j - 12, k);
        bigRoot5(world, random, i, j - 13, k);
        bigRoot5(world, random, i, j - 14, k);
        bigRoot6(world, random, i, j - 15, k);
        bigRoot6(world, random, i, j - 16, k);
        bigRoot6(world, random, i, j - 17, k);
        bigRoot6(world, random, i, j - 18, k);
        return true;
    }

    public boolean smallRoot1(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 4, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean smallRoot2(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 4, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean smallRoot3(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean smallRoot4(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean mediumRoot1(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 5, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean mediumRoot2(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 4, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean mediumRoot3(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean mediumRoot4(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean mediumRoot5(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean bigRoot1(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 6, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 6, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 6, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 6, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 5, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 5, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 5, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 5, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 6, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 6, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 6, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 6, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean bigRoot2(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 5, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 5, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 5, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 5, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 5, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean bigRoot3(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 4, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean bigRoot4(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 4, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean bigRoot5(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean bigRoot6(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing13(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 6, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 6, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 6, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 6, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 6, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 5, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 5, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 5, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 5, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 5, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 5, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 6, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 6, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 6, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 6, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 6, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing12(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 6, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 6, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 6, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 5, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 5, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 5, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 5, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 6, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 5, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 5, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 5, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 6, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 6, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 6, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing11(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 5, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 5, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 4, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 5, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 4, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 5, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing10(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 4, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing9(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 4, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 4, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 4, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 4, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing8(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing7(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 3, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 3, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 3, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 3, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing6(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing5(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i + 1, j, k + 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 2, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing4(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 2, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 2, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 2, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 1, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing3s(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing3(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 1);
        setBlockAndMetadata(world, i, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i + 1, j, k + 1, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing2(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i - 1, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i - 1, j, k, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k - 1, mod_FloraSoma.redwood.blockID, 0);
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    public boolean genRing1(World world, Random random, int i, int j, int k)
    {
        setBlockAndMetadata(world, i, j, k, mod_FloraSoma.redwood.blockID, 0);
        return true;
    }

    void generateLeafNodeList()
    {
        height = (int)((double)heightLimit * heightAttenuation);
        if (height >= heightLimit)
        {
            height = heightLimit - 1;
        }
        int i = (int)(1.3819999999999999D + Math.pow((field_872_k * (double)heightLimit) / 13D, 2D));
        if (i < 1)
        {
            i = 1;
        }
        int ai[][] = new int[i * heightLimit][4];
        int j = (basePos[1] + heightLimit) - leafDistanceLimit;
        int k = 1;
        int l = basePos[1] + height;
        int i1 = j - basePos[1];
        ai[0][0] = basePos[0];
        ai[0][1] = j;
        ai[0][2] = basePos[2];
        ai[0][3] = l;
        j--;
        while (i1 >= 0)
        {
            int j1 = 0;
            float f = func_528_a(i1);
            if (f < 0.0F)
            {
                j--;
                i1--;
            }
            else
            {
                double d = 0.5D;
                for (; j1 < i; j1++)
                {
                    double d1 = field_873_j * ((double)f * ((double)rand.nextFloat() + 0.32800000000000001D));
                    double d2 = (double)rand.nextFloat() * 2D * 3.1415899999999999D;
                    int k1 = MathHelper.floor_double(d1 * Math.sin(d2) + (double)basePos[0] + d);
                    int l1 = MathHelper.floor_double(d1 * Math.cos(d2) + (double)basePos[2] + d);
                    int ai1[] =
                    {
                        k1, j, l1
                    };
                    int ai2[] =
                    {
                        k1, j + leafDistanceLimit, l1
                    };
                    if (checkBlockLine(ai1, ai2) != -1)
                    {
                        continue;
                    }
                    int ai3[] =
                    {
                        basePos[0], basePos[1], basePos[2]
                    };
                    double d3 = Math.sqrt(Math.pow(Math.abs(basePos[0] - ai1[0]), 2D) + Math.pow(Math.abs(basePos[2] - ai1[2]), 2D));
                    double d4 = d3 * field_874_i;
                    if ((double)ai1[1] - d4 > (double)l)
                    {
                        ai3[1] = l;
                    }
                    else
                    {
                        ai3[1] = (int)((double)ai1[1] - d4);
                    }
                    if (checkBlockLine(ai3, ai1) == -1)
                    {
                        ai[k][0] = k1;
                        ai[k][1] = j;
                        ai[k][2] = l1;
                        ai[k][3] = ai3[1];
                        k++;
                    }
                }

                j--;
                i1--;
            }
        }
        leafNodes = new int[k][4];
        System.arraycopy(ai, 0, leafNodes, 0, k);
    }

    void func_523_a(int i, int j, int k, float f, byte byte0, int l)
    {
        int i1 = (int)((double)f + 0.61799999999999999D);
        byte byte1 = otherCoordPairs[byte0];
        byte byte2 = otherCoordPairs[byte0 + 3];
        int ai[] =
        {
            i, j, k
        };
        int ai1[] =
        {
            0, 0, 0
        };
        int j1 = -i1;
        int k1 = -i1;
        ai1[byte0] = ai[byte0];
        for (; j1 <= i1; j1++)
        {
            ai1[byte1] = ai[byte1] + j1;
            for (int l1 = -i1; l1 <= i1;)
            {
                double d = Math.sqrt(Math.pow((double)Math.abs(j1) + 0.5D, 2D) + Math.pow((double)Math.abs(l1) + 0.5D, 2D));
                if (d > (double)f)
                {
                    l1++;
                }
                else
                {
                    ai1[byte2] = ai[byte2] + l1;
                    int i2 = worldObj.getBlockId(ai1[0], ai1[1], ai1[2]);
                    if (i2 != 0 && i2 != 18)
                    {
                        l1++;
                    }
                    else
                    {
                        setBlockAndMetadata(worldObj, ai1[0], ai1[1], ai1[2], l, 0);
                        l1++;
                    }
                }
            }
        }
    }

    float func_528_a(int i)
    {
        if ((double)i < (double)(float)heightLimit * 0.29999999999999999D)
        {
            return -1.618F;
        }
        float f = (float)heightLimit / 2.0F;
        float f1 = (float)heightLimit / 2.0F - (float)i;
        float f2;
        if (f1 == 0.0F)
        {
            f2 = f;
        }
        else if (Math.abs(f1) >= f)
        {
            f2 = 0.0F;
        }
        else
        {
            f2 = (float)Math.sqrt(Math.pow(Math.abs(f), 2D) - Math.pow(Math.abs(f1), 2D));
        }
        f2 *= 0.5F;
        return f2;
    }

    float func_526_b(int i)
    {
        if (i < 0 || i >= leafDistanceLimit)
        {
            return -1F;
        }
        else
        {
            return i == 0 || i == leafDistanceLimit - 1 ? 2.0F : 3F;
        }
    }

    void generateLeafNode(int i, int j, int k)
    {
        int l = j;
        for (int i1 = j + leafDistanceLimit; l < i1; l++)
        {
            float f = func_526_b(l - j);
            func_523_a(i, l, k, f, (byte)1, mod_FloraSoma.floraLeaves.blockID);
        }
    }

    void placeBlockLine(int ai[], int ai1[], int i)
    {
        int ai2[] =
        {
            0, 0, 0
        };
        byte byte0 = 0;
        int j = 0;
        for (; byte0 < 3; byte0++)
        {
            ai2[byte0] = ai1[byte0] - ai[byte0];
            if (Math.abs(ai2[byte0]) > Math.abs(ai2[j]))
            {
                j = byte0;
            }
        }

        if (ai2[j] == 0)
        {
            return;
        }
        byte byte1 = otherCoordPairs[j];
        byte byte2 = otherCoordPairs[j + 3];
        byte byte3;
        if (ai2[j] > 0)
        {
            byte3 = 1;
        }
        else
        {
            byte3 = -1;
        }
        double d = (double)ai2[byte1] / (double)ai2[j];
        double d1 = (double)ai2[byte2] / (double)ai2[j];
        int ai3[] =
        {
            0, 0, 0
        };
        int k = 0;
        for (int l = ai2[j] + byte3; k != l; k += byte3)
        {
            ai3[j] = MathHelper.floor_double((double)(ai[j] + k) + 0.5D);
            ai3[byte1] = MathHelper.floor_double((double)ai[byte1] + (double)k * d + 0.5D);
            ai3[byte2] = MathHelper.floor_double((double)ai[byte2] + (double)k * d1 + 0.5D);
            setBlockAndMetadata(worldObj, ai3[0], ai3[1], ai3[2], i, 0);
        }
    }

    void generateLeaves()
    {
        int i = 0;
        for (int j = leafNodes.length; i < j; i++)
        {
            if(i < leafNodes.length) {
            	int k = leafNodes[i][0];
            	int l = leafNodes[i][1];
            	int i1 = leafNodes[i][2];
            	generateLeafNode(k, l, i1);
            }
        }
    }

    boolean leafNodeNeedsBase(int i)
    {
        return (double)i >= (double)heightLimit * 0.20000000000000001D;
    }

    void generateLeafNodeBases()
    {
        int i = 0;
        int j = leafNodes.length;
        int ai[] =
        {
            basePos[0], basePos[1], basePos[2]
        };
        for (; i < j; i++)
        {
            int ai1[] = leafNodes[i];
            int ai2[] =
            {
                ai1[0], ai1[1], ai1[2]
            };
            ai[1] = ai1[3];
            int k = ai[1] - basePos[1];
            if (leafNodeNeedsBase(k))
            {
                placeBlockLine(ai, ai2, mod_FloraSoma.redwood.blockID);
            }
        }
    }

    int checkBlockLine(int ai[], int ai1[])
    {
        int ai2[] =
        {
            0, 0, 0
        };
        byte byte0 = 0;
        int i = 0;
        for (; byte0 < 3; byte0++)
        {
            ai2[byte0] = ai1[byte0] - ai[byte0];
            if (Math.abs(ai2[byte0]) > Math.abs(ai2[i]))
            {
                i = byte0;
            }
        }

        if (ai2[i] == 0)
        {
            return -1;
        }
        byte byte1 = otherCoordPairs[i];
        byte byte2 = otherCoordPairs[i + 3];
        byte byte3;
        if (ai2[i] > 0)
        {
            byte3 = 1;
        }
        else
        {
            byte3 = -1;
        }
        double d = (double)ai2[byte1] / (double)ai2[i];
        double d1 = (double)ai2[byte2] / (double)ai2[i];
        int ai3[] =
        {
            0, 0, 0
        };
        int j = 0;
        int k = ai2[i] + byte3;
        do
        {
            if (j == k)
            {
                break;
            }
            ai3[i] = ai[i] + j;
            ai3[byte1] = MathHelper.floor_double((double)ai[byte1] + (double)j * d);
            ai3[byte2] = MathHelper.floor_double((double)ai[byte2] + (double)j * d1);
            int l = worldObj.getBlockId(ai3[0], ai3[1], ai3[2]);
            if (l != 0 && l != 18)
            {
                break;
            }
            j += byte3;
        }
        while (true);
        if (j == k)
        {
            return -1;
        }
        else
        {
            return Math.abs(j);
        }
    }

    boolean validTreeLocation()
    {
        int ai[] =
        {
            basePos[0], basePos[1], basePos[2]
        };
        int ai1[] =
        {
            basePos[0], (basePos[1] + heightLimit) - 1, basePos[2]
        };
        int i = worldObj.getBlockId(basePos[0], basePos[1] - 1, basePos[2]);
        if (i != 2 && i != 3)
        {
            return false;
        }
        int j = checkBlockLine(ai, ai1);
        if (j == -1)
        {
            return true;
        }
        if (j < 6)
        {
            return false;
        }
        else
        {
            heightLimit = j;
            return true;
        }
    }

    public void func_517_a(double d, double d1, double d2)
    {
        heightLimitLimit = (int)(d * 12D);
        if (d > 0.5D)
        {
            leafDistanceLimit = 5;
        }
        field_873_j = d1;
        field_872_k = d2;
    }
}
