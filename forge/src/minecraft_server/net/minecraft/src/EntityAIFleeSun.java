package net.minecraft.src;

import java.util.Random;

public class EntityAIFleeSun extends EntityAIBase
{
    private EntityCreature field_48260_a;
    private double field_48258_b;
    private double field_48259_c;
    private double field_48256_d;
    private float field_48257_e;
    private World field_48255_f;

    public EntityAIFleeSun(EntityCreature par1EntityCreature, float par2)
    {
        this.field_48260_a = par1EntityCreature;
        this.field_48257_e = par2;
        this.field_48255_f = par1EntityCreature.worldObj;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.field_48255_f.isDaytime())
        {
            return false;
        }
        else if (!this.field_48260_a.isBurning())
        {
            return false;
        }
        else if (!this.field_48255_f.canBlockSeeTheSky(MathHelper.floor_double(this.field_48260_a.posX), (int)this.field_48260_a.boundingBox.minY, MathHelper.floor_double(this.field_48260_a.posZ)))
        {
            return false;
        }
        else
        {
            Vec3D var1 = this.func_48254_f();

            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.field_48258_b = var1.xCoord;
                this.field_48259_c = var1.yCoord;
                this.field_48256_d = var1.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48260_a.getNavigator().noPath();
    }

    public void startExecuting()
    {
        this.field_48260_a.getNavigator().func_48658_a(this.field_48258_b, this.field_48259_c, this.field_48256_d, this.field_48257_e);
    }

    private Vec3D func_48254_f()
    {
        Random var1 = this.field_48260_a.getRNG();

        for (int var2 = 0; var2 < 10; ++var2)
        {
            int var3 = MathHelper.floor_double(this.field_48260_a.posX + (double)var1.nextInt(20) - 10.0D);
            int var4 = MathHelper.floor_double(this.field_48260_a.boundingBox.minY + (double)var1.nextInt(6) - 3.0D);
            int var5 = MathHelper.floor_double(this.field_48260_a.posZ + (double)var1.nextInt(20) - 10.0D);

            if (!this.field_48255_f.canBlockSeeTheSky(var3, var4, var5) && this.field_48260_a.getBlockPathWeight(var3, var4, var5) < 0.0F)
            {
                return Vec3D.createVector((double)var3, (double)var4, (double)var5);
            }
        }

        return null;
    }
}
