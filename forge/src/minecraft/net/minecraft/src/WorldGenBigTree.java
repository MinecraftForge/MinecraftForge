package net.minecraft.src;

import java.util.Random;

public class WorldGenBigTree extends WorldGenerator
{
    /**
     * Contains three sets of two values that provide complimentary indices for a given 'major' index - 1 and 2 for 0, 0
     * and 2 for 1, and 0 and 1 for 2.
     */
    static final byte[] otherCoordPairs = new byte[] {(byte)2, (byte)0, (byte)0, (byte)1, (byte)2, (byte)1};
    Random rand = new Random();

    /** Reference to the World object. */
    World worldObj;
    int[] basePos = new int[] {0, 0, 0};
    int heightLimit = 0;
    int height;
    double heightAttenuation = 0.618D;
    double branchDensity = 1.0D;
    double branchSlope = 0.381D;
    double scaleWidth = 1.0D;
    double leafDensity = 1.0D;

    /**
     * Currently always 1, can be set to 2 in the class constructor to generate a double-sized tree trunk for big trees.
     */
    int trunkSize = 1;

    /**
     * Sets the limit of the random value used to initialize the height limit.
     */
    int heightLimitLimit = 12;

    /**
     * Sets the distance limit for how far away the generator will populate leaves from the base leaf node.
     */
    int leafDistanceLimit = 4;

    /** Contains a list of a points at which to generate groups of leaves. */
    int[][] leafNodes;

    public WorldGenBigTree(boolean par1)
    {
        super(par1);
    }

    /**
     * Generates a list of leaf nodes for the tree, to be populated by generateLeaves.
     */
    void generateLeafNodeList()
    {
        this.height = (int)((double)this.heightLimit * this.heightAttenuation);

        if (this.height >= this.heightLimit)
        {
            this.height = this.heightLimit - 1;
        }

        int var1 = (int)(1.382D + Math.pow(this.leafDensity * (double)this.heightLimit / 13.0D, 2.0D));

        if (var1 < 1)
        {
            var1 = 1;
        }

        int[][] var2 = new int[var1 * this.heightLimit][4];
        int var3 = this.basePos[1] + this.heightLimit - this.leafDistanceLimit;
        int var4 = 1;
        int var5 = this.basePos[1] + this.height;
        int var6 = var3 - this.basePos[1];
        var2[0][0] = this.basePos[0];
        var2[0][1] = var3;
        var2[0][2] = this.basePos[2];
        var2[0][3] = var5;
        --var3;

        while (var6 >= 0)
        {
            int var7 = 0;
            float var8 = this.layerSize(var6);

            if (var8 < 0.0F)
            {
                --var3;
                --var6;
            }
            else
            {
                for (double var9 = 0.5D; var7 < var1; ++var7)
                {
                    double var11 = this.scaleWidth * (double)var8 * ((double)this.rand.nextFloat() + 0.328D);
                    double var13 = (double)this.rand.nextFloat() * 2.0D * Math.PI;
                    int var15 = MathHelper.floor_double(var11 * Math.sin(var13) + (double)this.basePos[0] + var9);
                    int var16 = MathHelper.floor_double(var11 * Math.cos(var13) + (double)this.basePos[2] + var9);
                    int[] var17 = new int[] {var15, var3, var16};
                    int[] var18 = new int[] {var15, var3 + this.leafDistanceLimit, var16};

                    if (this.checkBlockLine(var17, var18) == -1)
                    {
                        int[] var19 = new int[] {this.basePos[0], this.basePos[1], this.basePos[2]};
                        double var20 = Math.sqrt(Math.pow((double)Math.abs(this.basePos[0] - var17[0]), 2.0D) + Math.pow((double)Math.abs(this.basePos[2] - var17[2]), 2.0D));
                        double var22 = var20 * this.branchSlope;

                        if ((double)var17[1] - var22 > (double)var5)
                        {
                            var19[1] = var5;
                        }
                        else
                        {
                            var19[1] = (int)((double)var17[1] - var22);
                        }

                        if (this.checkBlockLine(var19, var17) == -1)
                        {
                            var2[var4][0] = var15;
                            var2[var4][1] = var3;
                            var2[var4][2] = var16;
                            var2[var4][3] = var19[1];
                            ++var4;
                        }
                    }
                }

                --var3;
                --var6;
            }
        }

        this.leafNodes = new int[var4][4];
        System.arraycopy(var2, 0, this.leafNodes, 0, var4);
    }

    void genTreeLayer(int par1, int par2, int par3, float par4, byte par5, int par6)
    {
        int var7 = (int)((double)par4 + 0.618D);
        byte var8 = otherCoordPairs[par5];
        byte var9 = otherCoordPairs[par5 + 3];
        int[] var10 = new int[] {par1, par2, par3};
        int[] var11 = new int[] {0, 0, 0};
        int var12 = -var7;
        int var13 = -var7;

        for (var11[par5] = var10[par5]; var12 <= var7; ++var12)
        {
            var11[var8] = var10[var8] + var12;
            var13 = -var7;

            while (var13 <= var7)
            {
                double var15 = Math.sqrt(Math.pow((double)Math.abs(var12) + 0.5D, 2.0D) + Math.pow((double)Math.abs(var13) + 0.5D, 2.0D));

                if (var15 > (double)par4)
                {
                    ++var13;
                }
                else
                {
                    var11[var9] = var10[var9] + var13;
                    int var14 = this.worldObj.getBlockId(var11[0], var11[1], var11[2]);

                    if (var14 != 0 && var14 != 18)
                    {
                        ++var13;
                    }
                    else
                    {
                        this.setBlockAndMetadata(this.worldObj, var11[0], var11[1], var11[2], par6, 0);
                        ++var13;
                    }
                }
            }
        }
    }

    /**
     * Gets the rough size of a layer of the tree.
     */
    float layerSize(int par1)
    {
        if ((double)par1 < (double)((float)this.heightLimit) * 0.3D)
        {
            return -1.618F;
        }
        else
        {
            float var2 = (float)this.heightLimit / 2.0F;
            float var3 = (float)this.heightLimit / 2.0F - (float)par1;
            float var4;

            if (var3 == 0.0F)
            {
                var4 = var2;
            }
            else if (Math.abs(var3) >= var2)
            {
                var4 = 0.0F;
            }
            else
            {
                var4 = (float)Math.sqrt(Math.pow((double)Math.abs(var2), 2.0D) - Math.pow((double)Math.abs(var3), 2.0D));
            }

            var4 *= 0.5F;
            return var4;
        }
    }

    float leafSize(int par1)
    {
        return par1 >= 0 && par1 < this.leafDistanceLimit ? (par1 != 0 && par1 != this.leafDistanceLimit - 1 ? 3.0F : 2.0F) : -1.0F;
    }

    /**
     * Generates the leaves surrounding an individual entry in the leafNodes list.
     */
    void generateLeafNode(int par1, int par2, int par3)
    {
        int var4 = par2;

        for (int var5 = par2 + this.leafDistanceLimit; var4 < var5; ++var4)
        {
            float var6 = this.leafSize(var4 - par2);
            this.genTreeLayer(par1, var4, par3, var6, (byte)1, 18);
        }
    }

    /**
     * Places a line of the specified block ID into the world from the first coordinate triplet to the second.
     */
    void placeBlockLine(int[] par1ArrayOfInteger, int[] par2ArrayOfInteger, int par3)
    {
        int[] var4 = new int[] {0, 0, 0};
        byte var5 = 0;
        byte var6;

        for (var6 = 0; var5 < 3; ++var5)
        {
            var4[var5] = par2ArrayOfInteger[var5] - par1ArrayOfInteger[var5];

            if (Math.abs(var4[var5]) > Math.abs(var4[var6]))
            {
                var6 = var5;
            }
        }

        if (var4[var6] != 0)
        {
            byte var7 = otherCoordPairs[var6];
            byte var8 = otherCoordPairs[var6 + 3];
            byte var9;

            if (var4[var6] > 0)
            {
                var9 = 1;
            }
            else
            {
                var9 = -1;
            }

            double var10 = (double)var4[var7] / (double)var4[var6];
            double var12 = (double)var4[var8] / (double)var4[var6];
            int[] var14 = new int[] {0, 0, 0};
            int var15 = 0;

            for (int var16 = var4[var6] + var9; var15 != var16; var15 += var9)
            {
                var14[var6] = MathHelper.floor_double((double)(par1ArrayOfInteger[var6] + var15) + 0.5D);
                var14[var7] = MathHelper.floor_double((double)par1ArrayOfInteger[var7] + (double)var15 * var10 + 0.5D);
                var14[var8] = MathHelper.floor_double((double)par1ArrayOfInteger[var8] + (double)var15 * var12 + 0.5D);
                this.setBlockAndMetadata(this.worldObj, var14[0], var14[1], var14[2], par3, 0);
            }
        }
    }

    /**
     * Generates the leaf portion of the tree as specified by the leafNodes list.
     */
    void generateLeaves()
    {
        int var1 = 0;

        for (int var2 = this.leafNodes.length; var1 < var2; ++var1)
        {
            int var3 = this.leafNodes[var1][0];
            int var4 = this.leafNodes[var1][1];
            int var5 = this.leafNodes[var1][2];
            this.generateLeafNode(var3, var4, var5);
        }
    }

    /**
     * Indicates whether or not a leaf node requires additional wood to be added to preserve integrity.
     */
    boolean leafNodeNeedsBase(int par1)
    {
        return (double)par1 >= (double)this.heightLimit * 0.2D;
    }

    /**
     * Places the trunk for the big tree that is being generated. Able to generate double-sized trunks by changing a
     * field that is always 1 to 2.
     */
    void generateTrunk()
    {
        int var1 = this.basePos[0];
        int var2 = this.basePos[1];
        int var3 = this.basePos[1] + this.height;
        int var4 = this.basePos[2];
        int[] var5 = new int[] {var1, var2, var4};
        int[] var6 = new int[] {var1, var3, var4};
        this.placeBlockLine(var5, var6, 17);

        if (this.trunkSize == 2)
        {
            ++var5[0];
            ++var6[0];
            this.placeBlockLine(var5, var6, 17);
            ++var5[2];
            ++var6[2];
            this.placeBlockLine(var5, var6, 17);
            var5[0] += -1;
            var6[0] += -1;
            this.placeBlockLine(var5, var6, 17);
        }
    }

    /**
     * Generates additional wood blocks to fill out the bases of different leaf nodes that would otherwise degrade.
     */
    void generateLeafNodeBases()
    {
        int var1 = 0;
        int var2 = this.leafNodes.length;

        for (int[] var3 = new int[] {this.basePos[0], this.basePos[1], this.basePos[2]}; var1 < var2; ++var1)
        {
            int[] var4 = this.leafNodes[var1];
            int[] var5 = new int[] {var4[0], var4[1], var4[2]};
            var3[1] = var4[3];
            int var6 = var3[1] - this.basePos[1];

            if (this.leafNodeNeedsBase(var6))
            {
                this.placeBlockLine(var3, var5, 17);
            }
        }
    }

    /**
     * Checks a line of blocks in the world from the first coordinate to triplet to the second, returning the distance
     * (in blocks) before a non-air, non-leaf block is encountered and/or the end is encountered.
     */
    int checkBlockLine(int[] par1ArrayOfInteger, int[] par2ArrayOfInteger)
    {
        int[] var3 = new int[] {0, 0, 0};
        byte var4 = 0;
        byte var5;

        for (var5 = 0; var4 < 3; ++var4)
        {
            var3[var4] = par2ArrayOfInteger[var4] - par1ArrayOfInteger[var4];

            if (Math.abs(var3[var4]) > Math.abs(var3[var5]))
            {
                var5 = var4;
            }
        }

        if (var3[var5] == 0)
        {
            return -1;
        }
        else
        {
            byte var6 = otherCoordPairs[var5];
            byte var7 = otherCoordPairs[var5 + 3];
            byte var8;

            if (var3[var5] > 0)
            {
                var8 = 1;
            }
            else
            {
                var8 = -1;
            }

            double var9 = (double)var3[var6] / (double)var3[var5];
            double var11 = (double)var3[var7] / (double)var3[var5];
            int[] var13 = new int[] {0, 0, 0};
            int var14 = 0;
            int var15;

            for (var15 = var3[var5] + var8; var14 != var15; var14 += var8)
            {
                var13[var5] = par1ArrayOfInteger[var5] + var14;
                var13[var6] = MathHelper.floor_double((double)par1ArrayOfInteger[var6] + (double)var14 * var9);
                var13[var7] = MathHelper.floor_double((double)par1ArrayOfInteger[var7] + (double)var14 * var11);
                int var16 = this.worldObj.getBlockId(var13[0], var13[1], var13[2]);

                if (var16 != 0 && var16 != 18)
                {
                    break;
                }
            }

            return var14 == var15 ? -1 : Math.abs(var14);
        }
    }

    /**
     * Returns a boolean indicating whether or not the current location for the tree, spanning basePos to to the height
     * limit, is valid.
     */
    boolean validTreeLocation()
    {
        int[] var1 = new int[] {this.basePos[0], this.basePos[1], this.basePos[2]};
        int[] var2 = new int[] {this.basePos[0], this.basePos[1] + this.heightLimit - 1, this.basePos[2]};
        int var3 = this.worldObj.getBlockId(this.basePos[0], this.basePos[1] - 1, this.basePos[2]);

        if (var3 != 2 && var3 != 3)
        {
            return false;
        }
        else
        {
            int var4 = this.checkBlockLine(var1, var2);

            if (var4 == -1)
            {
                return true;
            }
            else if (var4 < 6)
            {
                return false;
            }
            else
            {
                this.heightLimit = var4;
                return true;
            }
        }
    }

    /**
     * Rescales the generator settings, only used in WorldGenBigTree
     */
    public void setScale(double par1, double par3, double par5)
    {
        this.heightLimitLimit = (int)(par1 * 12.0D);

        if (par1 > 0.5D)
        {
            this.leafDistanceLimit = 5;
        }

        this.scaleWidth = par3;
        this.leafDensity = par5;
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        this.worldObj = par1World;
        long var6 = par2Random.nextLong();
        this.rand.setSeed(var6);
        this.basePos[0] = par3;
        this.basePos[1] = par4;
        this.basePos[2] = par5;

        if (this.heightLimit == 0)
        {
            this.heightLimit = 5 + this.rand.nextInt(this.heightLimitLimit);
        }

        if (!this.validTreeLocation())
        {
            return false;
        }
        else
        {
            this.generateLeafNodeList();
            this.generateLeaves();
            this.generateTrunk();
            this.generateLeafNodeBases();
            return true;
        }
    }
}
