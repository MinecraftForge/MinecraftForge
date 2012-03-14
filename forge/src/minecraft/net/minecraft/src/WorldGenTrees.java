package net.minecraft.src;

import java.util.Random;

public class WorldGenTrees extends WorldGenerator
{
    private final int height;
    private final boolean field_48200_b;
    private final int field_48201_c;
    private final int field_48199_d;

    public WorldGenTrees(boolean setWithNotify)
    {
        this(setWithNotify, 8, 0, 0, false);
    }

    public WorldGenTrees(boolean setWithNotify, int treeHeight, int par3, int par4, boolean par5)
    {
        super(setWithNotify);
        this.height = treeHeight;
        this.field_48201_c = par3;
        this.field_48199_d = par4;
        this.field_48200_b = par5;
    }

    public boolean generate(World par1World, Random par2Random, int posX, int posY, int posZ)
    {
        int treeHeight = par2Random.nextInt(5) + this.height;
        boolean var7 = true;

        if (posY >= 1 && posY + treeHeight + 1 <= 256)
        {
            int iterY;
            byte leafWidthHeight;
            int iterZ;
            int blockID;

            for (iterY = posY; iterY <= posY + 1 + treeHeight; ++iterY)
            {
                leafWidthHeight = 1;

                if (iterY == posY)
                {
                    leafWidthHeight = 0;
                }

                if (iterY >= posY + 1 + treeHeight - 2)
                {
                    leafWidthHeight = 2;
                }

                for (int iterX = posX - leafWidthHeight; iterX <= posX + leafWidthHeight && var7; ++iterX)
                {
                    for (iterZ = posZ - leafWidthHeight; iterZ <= posZ + leafWidthHeight && var7; ++iterZ)
                    {
                        if (iterY >= 0 && iterY < 256)
                        {
                            blockID = par1World.getBlockId(iterX, iterY, iterZ);

                            if (blockID != 0 && blockID != Block.leaves.blockID && blockID != Block.grass.blockID && blockID != Block.dirt.blockID && blockID != Block.wood.blockID)
                            {
                                var7 = false;
                            }
                        }
                        else
                        {
                            var7 = false;
                        }
                    }
                }
            }

            if (!var7)
            {
                return false;
            }
            else
            {
                iterY = par1World.getBlockId(posX, posY - 1, posZ);

                if ((iterY == Block.grass.blockID || iterY == Block.dirt.blockID) && posY < 256 - treeHeight - 1)
                {
                    par1World.setBlock(posX, posY - 1, posZ, Block.dirt.blockID);
                    par1World.setBlock(posX-1, posY - 1, posZ, Block.dirt.blockID);
                    par1World.setBlock(posX, posY - 1, posZ-1, Block.dirt.blockID);
                    par1World.setBlock(posX-1, posY - 1, posZ-1, Block.dirt.blockID);
                    leafWidthHeight = 3;
                    byte var18 = 0;
                    int var13;
                    int iterSubX;
                    int var15;

                    for (iterZ = posY - leafWidthHeight + treeHeight; iterZ <= posY + treeHeight; ++iterZ)
                    { //leaves Generation
                        blockID = iterZ - (posY + treeHeight);
                        var13 = var18 + 3 - blockID / 2;

                        for (iterSubX = posX - var13; iterSubX < posX + var13; ++iterSubX)
                        {
                            var15 = iterSubX - posX;

                            for (int iterSubZ = posZ - var13; iterSubZ < posZ + var13; ++iterSubZ)
                            {
                                int var17 = iterSubZ - posZ;

                                if ((Math.abs(var15) != var13 || Math.abs(var17) != var13 || par2Random.nextInt(2) != 0 && blockID != 0) && !Block.opaqueCubeLookup[par1World.getBlockId(iterSubX, iterZ, iterSubZ)])
                                {
                                    this.setBlockAndMetadata(par1World, iterSubX, iterZ, iterSubZ, Block.leaves.blockID, this.field_48199_d);
                                }
                            }
                        }
                    }

                    for (iterZ = 0; iterZ < treeHeight; ++iterZ)
                    {
                        blockID = par1World.getBlockId(posX, posY + iterZ, posZ);

                        if (blockID == 0 || blockID == Block.leaves.blockID)
                        {
                            this.setBlockAndMetadata(par1World, posX, posY + iterZ, posZ, Block.wood.blockID, this.field_48201_c);
                            this.setBlockAndMetadata(par1World, posX-1, posY + iterZ, posZ, Block.wood.blockID, this.field_48201_c);
                            this.setBlockAndMetadata(par1World, posX, posY + iterZ, posZ-1, Block.wood.blockID, this.field_48201_c);
                            this.setBlockAndMetadata(par1World, posX-1, posY + iterZ, posZ-1, Block.wood.blockID, this.field_48201_c);

                            /*if (this.field_48200_b && iterZ > 0)
                            {
                                if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(posX - 1, posY + iterZ, posZ))
                                {
                                    this.setBlockAndMetadata(par1World, posX - 1, posY + iterZ, posZ, Block.vine.blockID, 8);
                                }

                                if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(posX + 1, posY + iterZ, posZ))
                                {
                                    this.setBlockAndMetadata(par1World, posX + 1, posY + iterZ, posZ, Block.vine.blockID, 2);
                                }

                                if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(posX, posY + iterZ, posZ - 1))
                                {
                                    this.setBlockAndMetadata(par1World, posX, posY + iterZ, posZ - 1, Block.vine.blockID, 1);
                                }

                                if (par2Random.nextInt(3) > 0 && par1World.isAirBlock(posX, posY + iterZ, posZ + 1))
                                {
                                    this.setBlockAndMetadata(par1World, posX, posY + iterZ, posZ + 1, Block.vine.blockID, 4);
                                }
                            }*/
                        }
                    }

                    /*if (this.field_48200_b)
                    {
                        for (iterZ = posY - 3 + treeHeight; iterZ <= posY + treeHeight; ++iterZ)
                        {
                            blockID = iterZ - (posY + treeHeight);
                            var13 = 2 - blockID / 2;

                            for (iterSubX = posX - var13; iterSubX <= posX + var13; ++iterSubX)
                            {
                                for (var15 = posZ - var13; var15 <= posZ + var13; ++var15)
                                {
                                    if (par1World.getBlockId(iterSubX, iterZ, var15) == Block.leaves.blockID)
                                    {
                                        if (par2Random.nextInt(4) == 0 && par1World.getBlockId(iterSubX - 1, iterZ, var15) == 0)
                                        {
                                            this.func_48198_a(par1World, iterSubX - 1, iterZ, var15, 8);
                                        }

                                        if (par2Random.nextInt(4) == 0 && par1World.getBlockId(iterSubX + 1, iterZ, var15) == 0)
                                        {
                                            this.func_48198_a(par1World, iterSubX + 1, iterZ, var15, 2);
                                        }

                                        if (par2Random.nextInt(4) == 0 && par1World.getBlockId(iterSubX, iterZ, var15 - 1) == 0)
                                        {
                                            this.func_48198_a(par1World, iterSubX, iterZ, var15 - 1, 1);
                                        }

                                        if (par2Random.nextInt(4) == 0 && par1World.getBlockId(iterSubX, iterZ, var15 + 1) == 0)
                                        {
                                            this.func_48198_a(par1World, iterSubX, iterZ, var15 + 1, 4);
                                        }
                                    }
                                }
                            }
                        }
                    }*/

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }

    /*private void func_48198_a(World par1World, int par2, int par3, int par4, int par5)
    {
        par1World.setBlockAndMetadataWithNotify(par2, par3, par4, Block.vine.blockID, par5);
        int var6 = 4;

        while (true)
        {
            --par3;

            if (par1World.getBlockId(par2, par3, par4) != 0 || var6 <= 0)
            {
                return;
            }

            par1World.setBlockAndMetadataWithNotify(par2, par3, par4, Block.vine.blockID, par5);
            --var6;
        }
    }*/
}
