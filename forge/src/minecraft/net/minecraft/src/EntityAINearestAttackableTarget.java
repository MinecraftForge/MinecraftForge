package net.minecraft.src;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class EntityAINearestAttackableTarget extends EntityAITarget
{
    EntityLiving field_48389_a;
    Class field_48388_b;
    int field_48386_f;
    private EntityAINearestAttackableTargetSorter field_48387_g;

    public EntityAINearestAttackableTarget(EntityLiving par1EntityLiving, Class par2Class, float par3, int par4, boolean par5)
    {
        this(par1EntityLiving, par2Class, par3, par4, par5, false);
    }

    public EntityAINearestAttackableTarget(EntityLiving par1EntityLiving, Class par2Class, float par3, int par4, boolean par5, boolean par6)
    {
        super(par1EntityLiving, par3, par5, par6);
        this.field_48388_b = par2Class;
        this.field_48379_d = par3;
        this.field_48386_f = par4;
        this.field_48387_g = new EntityAINearestAttackableTargetSorter(this, par1EntityLiving);
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_48386_f > 0 && this.field_48382_c.getRNG().nextInt(this.field_48386_f) != 0)
        {
            return false;
        }
        else
        {
            if (this.field_48388_b == EntityPlayer.class)
            {
                EntityPlayer var1 = this.field_48382_c.worldObj.getClosestVulnerablePlayerToEntity(this.field_48382_c, (double)this.field_48379_d);

                if (this.func_48376_a(var1, false))
                {
                    this.field_48389_a = var1;
                    return true;
                }
            }
            else
            {
                List var5 = this.field_48382_c.worldObj.getEntitiesWithinAABB(this.field_48388_b, this.field_48382_c.boundingBox.expand((double)this.field_48379_d, 4.0D, (double)this.field_48379_d));
                Collections.sort(var5, this.field_48387_g);
                Iterator var2 = var5.iterator();

                while (var2.hasNext())
                {
                    Entity var3 = (Entity)var2.next();
                    EntityLiving var4 = (EntityLiving)var3;

                    if (this.func_48376_a(var4, false))
                    {
                        this.field_48389_a = var4;
                        return true;
                    }
                }
            }

            return false;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48382_c.func_48092_c(this.field_48389_a);
        super.startExecuting();
    }
}
