package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator
{
    // JAVADOC FIELD $$ field_75465_a
    private static Vec3 staticVector = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
    private static final String __OBFID = "CL_00001629";

    // JAVADOC METHOD $$ func_75463_a
    public static Vec3 findRandomTarget(EntityCreature par0EntityCreature, int par1, int par2)
    {
        // JAVADOC METHOD $$ func_75462_c
        return findRandomTargetBlock(par0EntityCreature, par1, par2, (Vec3)null);
    }

    // JAVADOC METHOD $$ func_75464_a
    public static Vec3 findRandomTargetBlockTowards(EntityCreature par0EntityCreature, int par1, int par2, Vec3 par3Vec3)
    {
        staticVector.xCoord = par3Vec3.xCoord - par0EntityCreature.posX;
        staticVector.yCoord = par3Vec3.yCoord - par0EntityCreature.posY;
        staticVector.zCoord = par3Vec3.zCoord - par0EntityCreature.posZ;
        // JAVADOC METHOD $$ func_75462_c
        return findRandomTargetBlock(par0EntityCreature, par1, par2, staticVector);
    }

    // JAVADOC METHOD $$ func_75461_b
    public static Vec3 findRandomTargetBlockAwayFrom(EntityCreature par0EntityCreature, int par1, int par2, Vec3 par3Vec3)
    {
        staticVector.xCoord = par0EntityCreature.posX - par3Vec3.xCoord;
        staticVector.yCoord = par0EntityCreature.posY - par3Vec3.yCoord;
        staticVector.zCoord = par0EntityCreature.posZ - par3Vec3.zCoord;
        // JAVADOC METHOD $$ func_75462_c
        return findRandomTargetBlock(par0EntityCreature, par1, par2, staticVector);
    }

    // JAVADOC METHOD $$ func_75462_c
    private static Vec3 findRandomTargetBlock(EntityCreature par0EntityCreature, int par1, int par2, Vec3 par3Vec3)
    {
        Random random = par0EntityCreature.getRNG();
        boolean flag = false;
        int k = 0;
        int l = 0;
        int i1 = 0;
        float f = -99999.0F;
        boolean flag1;

        if (par0EntityCreature.hasHome())
        {
            double d0 = (double)(par0EntityCreature.getHomePosition().getDistanceSquared(MathHelper.floor_double(par0EntityCreature.posX), MathHelper.floor_double(par0EntityCreature.posY), MathHelper.floor_double(par0EntityCreature.posZ)) + 4.0F);
            double d1 = (double)(par0EntityCreature.func_110174_bM() + (float)par1);
            flag1 = d0 < d1 * d1;
        }
        else
        {
            flag1 = false;
        }

        for (int l1 = 0; l1 < 10; ++l1)
        {
            int j1 = random.nextInt(2 * par1) - par1;
            int i2 = random.nextInt(2 * par2) - par2;
            int k1 = random.nextInt(2 * par1) - par1;

            if (par3Vec3 == null || (double)j1 * par3Vec3.xCoord + (double)k1 * par3Vec3.zCoord >= 0.0D)
            {
                j1 += MathHelper.floor_double(par0EntityCreature.posX);
                i2 += MathHelper.floor_double(par0EntityCreature.posY);
                k1 += MathHelper.floor_double(par0EntityCreature.posZ);

                if (!flag1 || par0EntityCreature.func_110176_b(j1, i2, k1))
                {
                    float f1 = par0EntityCreature.getBlockPathWeight(j1, i2, k1);

                    if (f1 > f)
                    {
                        f = f1;
                        k = j1;
                        l = i2;
                        i1 = k1;
                        flag = true;
                    }
                }
            }
        }

        if (flag)
        {
            return par0EntityCreature.worldObj.getWorldVec3Pool().getVecFromPool((double)k, (double)l, (double)i1);
        }
        else
        {
            return null;
        }
    }
}