package net.minecraft.src;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EntityAIMate extends EntityAIBase
{
    private EntityAnimal field_48199_d;
    World field_48203_a;
    private EntityAnimal field_48200_e;
    int field_48201_b = 0;
    float field_48202_c;

    public EntityAIMate(EntityAnimal par1EntityAnimal, float par2)
    {
        this.field_48199_d = par1EntityAnimal;
        this.field_48203_a = par1EntityAnimal.worldObj;
        this.field_48202_c = par2;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.field_48199_d.func_48363_r_())
        {
            return false;
        }
        else
        {
            this.field_48200_e = this.func_48198_f();
            return this.field_48200_e != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.field_48200_e.isEntityAlive() && this.field_48200_e.func_48363_r_() && this.field_48201_b < 60;
    }

    public void resetTask()
    {
        this.field_48200_e = null;
        this.field_48201_b = 0;
    }

    public void updateTask()
    {
        this.field_48199_d.getLookHelper().setLookPositionWithEntity(this.field_48200_e, 10.0F, (float)this.field_48199_d.getVerticalFaceSpeed());
        this.field_48199_d.getNavigator().func_48652_a(this.field_48200_e, this.field_48202_c);
        ++this.field_48201_b;

        if (this.field_48201_b == 60)
        {
            this.func_48197_i();
        }
    }

    private EntityAnimal func_48198_f()
    {
        float var1 = 8.0F;
        List var2 = this.field_48203_a.getEntitiesWithinAABB(this.field_48199_d.getClass(), this.field_48199_d.boundingBox.expand((double)var1, (double)var1, (double)var1));
        Iterator var3 = var2.iterator();
        EntityAnimal var5;

        do
        {
            if (!var3.hasNext())
            {
                return null;
            }

            Entity var4 = (Entity)var3.next();
            var5 = (EntityAnimal)var4;
        }
        while (!this.field_48199_d.func_48362_b(var5));

        return var5;
    }

    private void func_48197_i()
    {
        EntityAnimal var1 = this.field_48199_d.spawnBabyAnimal(this.field_48200_e);

        if (var1 != null)
        {
            this.field_48199_d.setGrowingAge(6000);
            this.field_48200_e.setGrowingAge(6000);
            this.field_48199_d.func_48364_s_();
            this.field_48200_e.func_48364_s_();
            var1.setGrowingAge(-24000);
            var1.setLocationAndAngles(this.field_48199_d.posX, this.field_48199_d.posY, this.field_48199_d.posZ, 0.0F, 0.0F);
            this.field_48203_a.spawnEntityInWorld(var1);
            Random var2 = this.field_48199_d.getRNG();

            for (int var3 = 0; var3 < 7; ++var3)
            {
                double var4 = var2.nextGaussian() * 0.02D;
                double var6 = var2.nextGaussian() * 0.02D;
                double var8 = var2.nextGaussian() * 0.02D;
                this.field_48203_a.spawnParticle("heart", this.field_48199_d.posX + (double)(var2.nextFloat() * this.field_48199_d.width * 2.0F) - (double)this.field_48199_d.width, this.field_48199_d.posY + 0.5D + (double)(var2.nextFloat() * this.field_48199_d.height), this.field_48199_d.posZ + (double)(var2.nextFloat() * this.field_48199_d.width * 2.0F) - (double)this.field_48199_d.width, var4, var6, var8);
            }
        }
    }
}
