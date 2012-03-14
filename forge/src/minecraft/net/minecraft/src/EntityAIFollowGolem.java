package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class EntityAIFollowGolem extends EntityAIBase
{
    private EntityVillager field_48403_a;
    private EntityIronGolem field_48401_b;
    private int field_48402_c;
    private boolean field_48400_d = false;

    public EntityAIFollowGolem(EntityVillager par1EntityVillager)
    {
        this.field_48403_a = par1EntityVillager;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_48403_a.getGrowingAge() >= 0)
        {
            return false;
        }
        else if (!this.field_48403_a.worldObj.isDaytime())
        {
            return false;
        }
        else
        {
            List var1 = this.field_48403_a.worldObj.getEntitiesWithinAABB(EntityIronGolem.class, this.field_48403_a.boundingBox.expand(6.0D, 2.0D, 6.0D));

            if (var1.size() == 0)
            {
                return false;
            }
            else
            {
                Iterator var2 = var1.iterator();

                while (var2.hasNext())
                {
                    Entity var3 = (Entity)var2.next();
                    EntityIronGolem var4 = (EntityIronGolem)var3;

                    if (var4.func_48117_D_() > 0)
                    {
                        this.field_48401_b = var4;
                        break;
                    }
                }

                return this.field_48401_b != null;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.field_48401_b.func_48117_D_() > 0;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48402_c = this.field_48403_a.getRNG().nextInt(320);
        this.field_48400_d = false;
        this.field_48401_b.getNavigator().func_48672_f();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_48401_b = null;
        this.field_48403_a.getNavigator().func_48672_f();
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.field_48403_a.getLookHelper().setLookPositionWithEntity(this.field_48401_b, 30.0F, 30.0F);

        if (this.field_48401_b.func_48117_D_() == this.field_48402_c)
        {
            this.field_48403_a.getNavigator().func_48667_a(this.field_48401_b, 0.15F);
            this.field_48400_d = true;
        }

        if (this.field_48400_d && this.field_48403_a.getDistanceSqToEntity(this.field_48401_b) < 4.0D)
        {
            this.field_48401_b.func_48116_a(false);
            this.field_48403_a.getNavigator().func_48672_f();
        }
    }
}
