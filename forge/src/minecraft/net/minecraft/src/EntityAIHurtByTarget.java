package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class EntityAIHurtByTarget extends EntityAITarget
{
    boolean field_48395_a;

    public EntityAIHurtByTarget(EntityLiving par1EntityLiving, boolean par2)
    {
        super(par1EntityLiving, 16.0F, false);
        this.field_48395_a = par2;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.func_48376_a(this.field_48382_c.getAITarget(), true);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48382_c.func_48092_c(this.field_48382_c.getAITarget());

        if (this.field_48395_a)
        {
            List var1 = this.field_48382_c.worldObj.getEntitiesWithinAABB(this.field_48382_c.getClass(), AxisAlignedBB.getBoundingBoxFromPool(this.field_48382_c.posX, this.field_48382_c.posY, this.field_48382_c.posZ, this.field_48382_c.posX + 1.0D, this.field_48382_c.posY + 1.0D, this.field_48382_c.posZ + 1.0D).expand((double)this.field_48379_d, 4.0D, (double)this.field_48379_d));
            Iterator var2 = var1.iterator();

            while (var2.hasNext())
            {
                Entity var3 = (Entity)var2.next();
                EntityLiving var4 = (EntityLiving)var3;

                if (this.field_48382_c != var4 && var4.func_48094_aS() == null)
                {
                    var4.func_48092_c(this.field_48382_c.getAITarget());
                }
            }
        }

        super.startExecuting();
    }
}
