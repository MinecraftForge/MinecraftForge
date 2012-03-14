package net.minecraft.src;

import java.util.Random;

public class Teleporter
{
    /** A private Random() function in Teleporter */
    private Random random = new Random();

    /**
     * Place an entity in a nearby portal, creating one if necessary.
     */
    public void placeInPortal(World par1World, Entity par2Entity)
    {
        if (par1World.worldProvider.worldType != 1)
        {
            if (!this.placeInExistingPortal(par1World, par2Entity))
            {
                this.createPortal(par1World, par2Entity);
                this.placeInExistingPortal(par1World, par2Entity);
            }
        }
        else
        {
            int var3 = MathHelper.floor_double(par2Entity.posX);
            int var4 = MathHelper.floor_double(par2Entity.posY) - 1;
            int var5 = MathHelper.floor_double(par2Entity.posZ);
            byte var6 = 1;
            byte var7 = 0;

            for (int var8 = -2; var8 <= 2; ++var8)
            {
                for (int var9 = -2; var9 <= 2; ++var9)
                {
                    for (int var10 = -1; var10 < 3; ++var10)
                    {
                        int var11 = var3 + var9 * var6 + var8 * var7;
                        int var12 = var4 + var10;
                        int var13 = var5 + var9 * var7 - var8 * var6;
                        boolean var14 = var10 < 0;
                        par1World.setBlockWithNotify(var11, var12, var13, var14 ? Block.obsidian.blockID : 0);
                    }
                }
            }

            par2Entity.setLocationAndAngles((double)var3, (double)var4, (double)var5, par2Entity.rotationYaw, 0.0F);
            par2Entity.motionX = par2Entity.motionY = par2Entity.motionZ = 0.0D;
        }
    }

    /**
     * Place an entity in a nearby portal which already exists.
     */
    public boolean placeInExistingPortal(World par1World, Entity par2Entity)
    {
        short var3 = 128;
        double var4 = -1.0D;
        int var6 = 0;
        int var7 = 0;
        int var8 = 0;
        int var9 = MathHelper.floor_double(par2Entity.posX);
        int var10 = MathHelper.floor_double(par2Entity.posZ);
        double var18;

        for (int var11 = var9 - var3; var11 <= var9 + var3; ++var11)
        {
            double var12 = (double)var11 + 0.5D - par2Entity.posX;

            for (int var14 = var10 - var3; var14 <= var10 + var3; ++var14)
            {
                double var15 = (double)var14 + 0.5D - par2Entity.posZ;

                for (int var17 = 127; var17 >= 0; --var17)
                {
                    if (par1World.getBlockId(var11, var17, var14) == Block.portal.blockID)
                    {
                        while (par1World.getBlockId(var11, var17 - 1, var14) == Block.portal.blockID)
                        {
                            --var17;
                        }

                        var18 = (double)var17 + 0.5D - par2Entity.posY;
                        double var20 = var12 * var12 + var18 * var18 + var15 * var15;

                        if (var4 < 0.0D || var20 < var4)
                        {
                            var4 = var20;
                            var6 = var11;
                            var7 = var17;
                            var8 = var14;
                        }
                    }
                }
            }
        }

        if (var4 >= 0.0D)
        {
            double var22 = (double)var6 + 0.5D;
            double var16 = (double)var7 + 0.5D;
            var18 = (double)var8 + 0.5D;

            if (par1World.getBlockId(var6 - 1, var7, var8) == Block.portal.blockID)
            {
                var22 -= 0.5D;
            }

            if (par1World.getBlockId(var6 + 1, var7, var8) == Block.portal.blockID)
            {
                var22 += 0.5D;
            }

            if (par1World.getBlockId(var6, var7, var8 - 1) == Block.portal.blockID)
            {
                var18 -= 0.5D;
            }

            if (par1World.getBlockId(var6, var7, var8 + 1) == Block.portal.blockID)
            {
                var18 += 0.5D;
            }

            par2Entity.setLocationAndAngles(var22, var16, var18, par2Entity.rotationYaw, 0.0F);
            par2Entity.motionX = par2Entity.motionY = par2Entity.motionZ = 0.0D;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Create a new portal near an entity.
     */
    public boolean createPortal(World par1World, Entity par2Entity)
    {
        byte var3 = 16;
        double var4 = -1.0D;
        int var6 = MathHelper.floor_double(par2Entity.posX);
        int var7 = MathHelper.floor_double(par2Entity.posY);
        int var8 = MathHelper.floor_double(par2Entity.posZ);
        int var9 = var6;
        int var10 = var7;
        int var11 = var8;
        int var12 = 0;
        int var13 = this.random.nextInt(4);
        int var14;
        double var15;
        int var17;
        double var18;
        int var21;
        int var20;
        int var23;
        int var22;
        int var25;
        int var24;
        int var27;
        int var26;
        int var28;
        double var34;
        double var32;

        for (var14 = var6 - var3; var14 <= var6 + var3; ++var14)
        {
            var15 = (double)var14 + 0.5D - par2Entity.posX;

            for (var17 = var8 - var3; var17 <= var8 + var3; ++var17)
            {
                var18 = (double)var17 + 0.5D - par2Entity.posZ;
                label274:

                for (var20 = 127; var20 >= 0; --var20)
                {
                    if (par1World.isAirBlock(var14, var20, var17))
                    {
                        while (var20 > 0 && par1World.isAirBlock(var14, var20 - 1, var17))
                        {
                            --var20;
                        }

                        for (var21 = var13; var21 < var13 + 4; ++var21)
                        {
                            var22 = var21 % 2;
                            var23 = 1 - var22;

                            if (var21 % 4 >= 2)
                            {
                                var22 = -var22;
                                var23 = -var23;
                            }

                            for (var24 = 0; var24 < 3; ++var24)
                            {
                                for (var25 = 0; var25 < 4; ++var25)
                                {
                                    for (var26 = -1; var26 < 4; ++var26)
                                    {
                                        var27 = var14 + (var25 - 1) * var22 + var24 * var23;
                                        var28 = var20 + var26;
                                        int var29 = var17 + (var25 - 1) * var23 - var24 * var22;

                                        if (var26 < 0 && !par1World.getBlockMaterial(var27, var28, var29).isSolid() || var26 >= 0 && !par1World.isAirBlock(var27, var28, var29))
                                        {
                                            continue label274;
                                        }
                                    }
                                }
                            }

                            var32 = (double)var20 + 0.5D - par2Entity.posY;
                            var34 = var15 * var15 + var32 * var32 + var18 * var18;

                            if (var4 < 0.0D || var34 < var4)
                            {
                                var4 = var34;
                                var9 = var14;
                                var10 = var20;
                                var11 = var17;
                                var12 = var21 % 4;
                            }
                        }
                    }
                }
            }
        }

        if (var4 < 0.0D)
        {
            for (var14 = var6 - var3; var14 <= var6 + var3; ++var14)
            {
                var15 = (double)var14 + 0.5D - par2Entity.posX;

                for (var17 = var8 - var3; var17 <= var8 + var3; ++var17)
                {
                    var18 = (double)var17 + 0.5D - par2Entity.posZ;
                    label222:

                    for (var20 = 127; var20 >= 0; --var20)
                    {
                        if (par1World.isAirBlock(var14, var20, var17))
                        {
                            while (var20 > 0 && par1World.isAirBlock(var14, var20 - 1, var17))
                            {
                                --var20;
                            }

                            for (var21 = var13; var21 < var13 + 2; ++var21)
                            {
                                var22 = var21 % 2;
                                var23 = 1 - var22;

                                for (var24 = 0; var24 < 4; ++var24)
                                {
                                    for (var25 = -1; var25 < 4; ++var25)
                                    {
                                        var26 = var14 + (var24 - 1) * var22;
                                        var27 = var20 + var25;
                                        var28 = var17 + (var24 - 1) * var23;

                                        if (var25 < 0 && !par1World.getBlockMaterial(var26, var27, var28).isSolid() || var25 >= 0 && !par1World.isAirBlock(var26, var27, var28))
                                        {
                                            continue label222;
                                        }
                                    }
                                }

                                var32 = (double)var20 + 0.5D - par2Entity.posY;
                                var34 = var15 * var15 + var32 * var32 + var18 * var18;

                                if (var4 < 0.0D || var34 < var4)
                                {
                                    var4 = var34;
                                    var9 = var14;
                                    var10 = var20;
                                    var11 = var17;
                                    var12 = var21 % 2;
                                }
                            }
                        }
                    }
                }
            }
        }

        int var30 = var9;
        int var16 = var10;
        var17 = var11;
        int var31 = var12 % 2;
        int var19 = 1 - var31;

        if (var12 % 4 >= 2)
        {
            var31 = -var31;
            var19 = -var19;
        }

        boolean var33;

        if (var4 < 0.0D)
        {
            if (var10 < 70)
            {
                var10 = 70;
            }

            if (var10 > 118)
            {
                var10 = 118;
            }

            var16 = var10;

            for (var20 = -1; var20 <= 1; ++var20)
            {
                for (var21 = 1; var21 < 3; ++var21)
                {
                    for (var22 = -1; var22 < 3; ++var22)
                    {
                        var23 = var30 + (var21 - 1) * var31 + var20 * var19;
                        var24 = var16 + var22;
                        var25 = var17 + (var21 - 1) * var19 - var20 * var31;
                        var33 = var22 < 0;
                        par1World.setBlockWithNotify(var23, var24, var25, var33 ? Block.obsidian.blockID : 0);
                    }
                }
            }
        }

        for (var20 = 0; var20 < 4; ++var20)
        {
            par1World.editingBlocks = true;

            for (var21 = 0; var21 < 4; ++var21)
            {
                for (var22 = -1; var22 < 4; ++var22)
                {
                    var23 = var30 + (var21 - 1) * var31;
                    var24 = var16 + var22;
                    var25 = var17 + (var21 - 1) * var19;
                    var33 = var21 == 0 || var21 == 3 || var22 == -1 || var22 == 3;
                    par1World.setBlockWithNotify(var23, var24, var25, var33 ? Block.obsidian.blockID : Block.portal.blockID);
                }
            }

            par1World.editingBlocks = false;

            for (var21 = 0; var21 < 4; ++var21)
            {
                for (var22 = -1; var22 < 4; ++var22)
                {
                    var23 = var30 + (var21 - 1) * var31;
                    var24 = var16 + var22;
                    var25 = var17 + (var21 - 1) * var19;
                    par1World.notifyBlocksOfNeighborChange(var23, var24, var25, par1World.getBlockId(var23, var24, var25));
                }
            }
        }

        return true;
    }
}
