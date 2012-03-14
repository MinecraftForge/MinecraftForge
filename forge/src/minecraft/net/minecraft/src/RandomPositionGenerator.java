package net.minecraft.src;

import java.util.Random;

public class RandomPositionGenerator
{
    private static Vec3D field_48624_a = Vec3D.createVectorHelper(0.0D, 0.0D, 0.0D);

    public static Vec3D func_48622_a(EntityCreature par0EntityCreature, int par1, int par2)
    {
        return func_48621_c(par0EntityCreature, par1, par2, (Vec3D)null);
    }

    public static Vec3D func_48620_a(EntityCreature par0EntityCreature, int par1, int par2, Vec3D par3Vec3D)
    {
        field_48624_a.xCoord = par3Vec3D.xCoord - par0EntityCreature.posX;
        field_48624_a.yCoord = par3Vec3D.yCoord - par0EntityCreature.posY;
        field_48624_a.zCoord = par3Vec3D.zCoord - par0EntityCreature.posZ;
        return func_48621_c(par0EntityCreature, par1, par2, field_48624_a);
    }

    public static Vec3D func_48623_b(EntityCreature par0EntityCreature, int par1, int par2, Vec3D par3Vec3D)
    {
        field_48624_a.xCoord = par0EntityCreature.posX - par3Vec3D.xCoord;
        field_48624_a.yCoord = par0EntityCreature.posY - par3Vec3D.yCoord;
        field_48624_a.zCoord = par0EntityCreature.posZ - par3Vec3D.zCoord;
        return func_48621_c(par0EntityCreature, par1, par2, field_48624_a);
    }

    private static Vec3D func_48621_c(EntityCreature par0EntityCreature, int par1, int par2, Vec3D par3Vec3D)
    {
        Random var4 = par0EntityCreature.getRNG();
        boolean var5 = false;
        int var6 = 0;
        int var7 = 0;
        int var8 = 0;
        float var9 = -99999.0F;
        boolean var10;

        if (par0EntityCreature.hasHome())
        {
            double var11 = par0EntityCreature.getHomePosition().getEuclideanDistanceTo(MathHelper.floor_double(par0EntityCreature.posX), MathHelper.floor_double(par0EntityCreature.posY), MathHelper.floor_double(par0EntityCreature.posZ)) + 4.0D;
            var10 = var11 < (double)(par0EntityCreature.getMaximumHomeDistance() + (float)par1);
        }
        else
        {
            var10 = false;
        }

        for (int var16 = 0; var16 < 10; ++var16)
        {
            int var12 = var4.nextInt(2 * par1) - par1;
            int var13 = var4.nextInt(2 * par2) - par2;
            int var14 = var4.nextInt(2 * par1) - par1;

            if (par3Vec3D == null || (double)var12 * par3Vec3D.xCoord + (double)var14 * par3Vec3D.zCoord >= 0.0D)
            {
                var12 += MathHelper.floor_double(par0EntityCreature.posX);
                var13 += MathHelper.floor_double(par0EntityCreature.posY);
                var14 += MathHelper.floor_double(par0EntityCreature.posZ);

                if (!var10 || par0EntityCreature.isWithinHomeDistance(var12, var13, var14))
                {
                    float var15 = par0EntityCreature.getBlockPathWeight(var12, var13, var14);

                    if (var15 > var9)
                    {
                        var9 = var15;
                        var6 = var12;
                        var7 = var13;
                        var8 = var14;
                        var5 = true;
                    }
                }
            }
        }

        if (var5)
        {
            return Vec3D.createVector((double)var6, (double)var7, (double)var8);
        }
        else
        {
            return null;
        }
    }
}
