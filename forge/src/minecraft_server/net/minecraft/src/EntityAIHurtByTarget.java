package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class EntityAIHurtByTarget extends EntityAITarget
{
    boolean field_48300_a;

    public EntityAIHurtByTarget(EntityLiving par1EntityLiving, boolean par2)
    {
        super(par1EntityLiving, 16.0F, false);
        this.field_48300_a = par2;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.func_48284_a(this.field_48291_c.getAITarget(), true);
    }

    public void startExecuting()
    {
        this.field_48291_c.func_48327_b(this.field_48291_c.getAITarget());

        if (this.field_48300_a)
        {
            List var1 = this.field_48291_c.worldObj.getEntitiesWithinAABB(this.field_48291_c.getClass(), AxisAlignedBB.getBoundingBoxFromPool(this.field_48291_c.posX, this.field_48291_c.posY, this.field_48291_c.posZ, this.field_48291_c.posX + 1.0D, this.field_48291_c.posY + 1.0D, this.field_48291_c.posZ + 1.0D).expand((double)this.field_48288_d, 4.0D, (double)this.field_48288_d));
            Iterator var2 = var1.iterator();

            while (var2.hasNext())
            {
                Entity var3 = (Entity)var2.next();
                EntityLiving var4 = (EntityLiving)var3;

                if (this.field_48291_c != var4 && var4.func_48331_as() == null)
                {
                    var4.func_48327_b(this.field_48291_c.getAITarget());
                }
            }
        }

        super.startExecuting();
    }
}
