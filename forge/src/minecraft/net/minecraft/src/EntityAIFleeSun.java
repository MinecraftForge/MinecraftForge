package net.minecraft.src;

import java.util.Random;

public class EntityAIFleeSun extends EntityAIBase
{
    private EntityCreature field_48302_a;
    private double field_48300_b;
    private double field_48301_c;
    private double field_48298_d;
    private float field_48299_e;
    private World field_48297_f;

    public EntityAIFleeSun(EntityCreature par1EntityCreature, float par2)
    {
        this.field_48302_a = par1EntityCreature;
        this.field_48299_e = par2;
        this.field_48297_f = par1EntityCreature.worldObj;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.field_48297_f.isDaytime())
        {
            return false;
        }
        else if (!this.field_48302_a.isBurning())
        {
            return false;
        }
        else if (!this.field_48297_f.canBlockSeeTheSky(MathHelper.floor_double(this.field_48302_a.posX), (int)this.field_48302_a.boundingBox.minY, MathHelper.floor_double(this.field_48302_a.posZ)))
        {
            return false;
        }
        else
        {
            Vec3D var1 = this.func_48296_h();

            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.field_48300_b = var1.xCoord;
                this.field_48301_c = var1.yCoord;
                this.field_48298_d = var1.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48302_a.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48302_a.getNavigator().func_48666_a(this.field_48300_b, this.field_48301_c, this.field_48298_d, this.field_48299_e);
    }

    private Vec3D func_48296_h()
    {
        Random var1 = this.field_48302_a.getRNG();

        for (int var2 = 0; var2 < 10; ++var2)
        {
            int var3 = MathHelper.floor_double(this.field_48302_a.posX + (double)var1.nextInt(20) - 10.0D);
            int var4 = MathHelper.floor_double(this.field_48302_a.boundingBox.minY + (double)var1.nextInt(6) - 3.0D);
            int var5 = MathHelper.floor_double(this.field_48302_a.posZ + (double)var1.nextInt(20) - 10.0D);

            if (!this.field_48297_f.canBlockSeeTheSky(var3, var4, var5) && this.field_48302_a.getBlockPathWeight(var3, var4, var5) < 0.0F)
            {
                return Vec3D.createVector((double)var3, (double)var4, (double)var5);
            }
        }

        return null;
    }
}
