package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class EntityAIFollowParent extends EntityAIBase
{
    EntityAnimal field_48249_a;
    EntityAnimal field_48247_b;
    float field_48248_c;
    private int field_48246_d;

    public EntityAIFollowParent(EntityAnimal par1EntityAnimal, float par2)
    {
        this.field_48249_a = par1EntityAnimal;
        this.field_48248_c = par2;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_48249_a.getGrowingAge() >= 0)
        {
            return false;
        }
        else
        {
            List var1 = this.field_48249_a.worldObj.getEntitiesWithinAABB(this.field_48249_a.getClass(), this.field_48249_a.boundingBox.expand(8.0D, 4.0D, 8.0D));
            EntityAnimal var2 = null;
            double var3 = Double.MAX_VALUE;
            Iterator var5 = var1.iterator();

            while (var5.hasNext())
            {
                Entity var6 = (Entity)var5.next();
                EntityAnimal var7 = (EntityAnimal)var6;

                if (var7.getGrowingAge() >= 0)
                {
                    double var8 = this.field_48249_a.getDistanceSqToEntity(var7);

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
                this.field_48247_b = var2;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (!this.field_48247_b.isEntityAlive())
        {
            return false;
        }
        else
        {
            double var1 = this.field_48249_a.getDistanceSqToEntity(this.field_48247_b);
            return var1 >= 9.0D && var1 <= 256.0D;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48246_d = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_48247_b = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (--this.field_48246_d <= 0)
        {
            this.field_48246_d = 10;
            this.field_48249_a.getNavigator().func_48667_a(this.field_48247_b, this.field_48248_c);
        }
    }
}
