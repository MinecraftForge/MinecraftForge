package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class EntityAIFollowParent extends EntityAIBase
{
    EntityAnimal field_48141_a;
    EntityAnimal field_48139_b;
    float field_48140_c;
    private int field_48138_d;

    public EntityAIFollowParent(EntityAnimal par1EntityAnimal, float par2)
    {
        this.field_48141_a = par1EntityAnimal;
        this.field_48140_c = par2;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_48141_a.getGrowingAge() >= 0)
        {
            return false;
        }
        else
        {
            List var1 = this.field_48141_a.worldObj.getEntitiesWithinAABB(this.field_48141_a.getClass(), this.field_48141_a.boundingBox.expand(8.0D, 4.0D, 8.0D));
            EntityAnimal var2 = null;
            double var3 = Double.MAX_VALUE;
            Iterator var5 = var1.iterator();

            while (var5.hasNext())
            {
                Entity var6 = (Entity)var5.next();
                EntityAnimal var7 = (EntityAnimal)var6;

                if (var7.getGrowingAge() >= 0)
                {
                    double var8 = this.field_48141_a.getDistanceSqToEntity(var7);

                    if (var8 <= var3)
                    {
                        var3 = var8;
                        var2 = var7;
                    }
                }
            }

            if (var2 == null)
            {
                return false;
            }
            else if (var3 < 9.0D)
            {
                return false;
            }
            else
            {
                this.field_48139_b = var2;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (!this.field_48139_b.isEntityAlive())
        {
            return false;
        }
        else
        {
            double var1 = this.field_48141_a.getDistanceSqToEntity(this.field_48139_b);
            return var1 >= 9.0D && var1 <= 256.0D;
        }
    }

    public void startExecuting()
    {
        this.field_48138_d = 0;
    }

    public void resetTask()
    {
        this.field_48139_b = null;
    }

    public void updateTask()
    {
        if (--this.field_48138_d <= 0)
        {
            this.field_48138_d = 10;
            this.field_48141_a.getNavigator().func_48652_a(this.field_48139_b, this.field_48140_c);
        }
    }
}
