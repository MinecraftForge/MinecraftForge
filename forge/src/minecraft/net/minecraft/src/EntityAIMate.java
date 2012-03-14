package net.minecraft.src;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EntityAIMate extends EntityAIBase
{
    private EntityAnimal field_48259_d;
    World field_48263_a;
    private EntityAnimal field_48260_e;
    int field_48261_b = 0;
    float field_48262_c;

    public EntityAIMate(EntityAnimal par1EntityAnimal, float par2)
    {
        this.field_48259_d = par1EntityAnimal;
        this.field_48263_a = par1EntityAnimal.worldObj;
        this.field_48262_c = par2;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.field_48259_d.func_48136_o_())
        {
            return false;
        }
        else
        {
            this.field_48260_e = this.func_48258_h();
            return this.field_48260_e != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.field_48260_e.isEntityAlive() && this.field_48260_e.func_48136_o_() && this.field_48261_b < 60;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_48260_e = null;
        this.field_48261_b = 0;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.field_48259_d.getLookHelper().setLookPositionWithEntity(this.field_48260_e, 10.0F, (float)this.field_48259_d.getVerticalFaceSpeed());
        this.field_48259_d.getNavigator().func_48667_a(this.field_48260_e, this.field_48262_c);
        ++this.field_48261_b;

        if (this.field_48261_b == 60)
        {
            this.func_48257_i();
        }
    }

    private EntityAnimal func_48258_h()
    {
        float var1 = 8.0F;
        List var2 = this.field_48263_a.getEntitiesWithinAABB(this.field_48259_d.getClass(), this.field_48259_d.boundingBox.expand((double)var1, (double)var1, (double)var1));
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
        while (!this.field_48259_d.func_48135_b(var5));

        return var5;
    }

    private void func_48257_i()
    {
        EntityAnimal var1 = this.field_48259_d.spawnBabyAnimal(this.field_48260_e);

        if (var1 != null)
        {
            this.field_48259_d.setGrowingAge(6000);
            this.field_48260_e.setGrowingAge(6000);
            this.field_48259_d.func_48134_p_();
            this.field_48260_e.func_48134_p_();
            var1.setGrowingAge(-24000);
            var1.setLocationAndAngles(this.field_48259_d.posX, this.field_48259_d.posY, this.field_48259_d.posZ, 0.0F, 0.0F);
            this.field_48263_a.spawnEntityInWorld(var1);
            Random var2 = this.field_48259_d.getRNG();

            for (int var3 = 0; var3 < 7; ++var3)
            {
                double var4 = var2.nextGaussian() * 0.02D;
                double var6 = var2.nextGaussian() * 0.02D;
                double var8 = var2.nextGaussian() * 0.02D;
                this.field_48263_a.spawnParticle("heart", this.field_48259_d.posX + (double)(var2.nextFloat() * this.field_48259_d.width * 2.0F) - (double)this.field_48259_d.width, this.field_48259_d.posY + 0.5D + (double)(var2.nextFloat() * this.field_48259_d.height), this.field_48259_d.posZ + (double)(var2.nextFloat() * this.field_48259_d.width * 2.0F) - (double)this.field_48259_d.width, var4, var6, var8);
            }
        }
    }
}
