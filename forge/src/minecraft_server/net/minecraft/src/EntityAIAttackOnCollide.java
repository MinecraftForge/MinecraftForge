package net.minecraft.src;

public class EntityAIAttackOnCollide extends EntityAIBase
{
    World worldObj;
    EntityLiving field_48156_b;
    EntityLiving entityTarget;
    int field_46095_d;
    float field_48155_e;
    boolean field_48153_f;
    PathEntity field_48154_g;
    Class field_48157_h;
    private int field_48158_i;

    public EntityAIAttackOnCollide(EntityLiving par1EntityLiving, Class par2Class, float par3, boolean par4)
    {
        this(par1EntityLiving, par3, par4);
        this.field_48157_h = par2Class;
    }

    public EntityAIAttackOnCollide(EntityLiving par1EntityLiving, float par2, boolean par3)
    {
        this.field_46095_d = 0;
        this.field_48156_b = par1EntityLiving;
        this.worldObj = par1EntityLiving.worldObj;
        this.field_48155_e = par2;
        this.field_48153_f = par3;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLiving var1 = this.field_48156_b.func_48331_as();

        if (var1 == null)
        {
            return false;
        }
        else if (this.field_48157_h != null && !this.field_48157_h.isAssignableFrom(var1.getClass()))
        {
            return false;
        }
        else
        {
            this.entityTarget = var1;
            this.field_48154_g = this.field_48156_b.getNavigator().func_48661_a(this.entityTarget);
            return this.field_48154_g != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        EntityLiving var1 = this.field_48156_b.func_48331_as();
        return var1 == null ? false : (!this.entityTarget.isEntityAlive() ? false : (!this.field_48153_f ? !this.field_48156_b.getNavigator().noPath() : this.field_48156_b.isWithinHomeDistance(MathHelper.floor_double(this.entityTarget.posX), MathHelper.floor_double(this.entityTarget.posY), MathHelper.floor_double(this.entityTarget.posZ))));
    }

    public void startExecuting()
    {
        this.field_48156_b.getNavigator().func_48647_a(this.field_48154_g, this.field_48155_e);
        this.field_48158_i = 0;
    }

    public void resetTask()
    {
        this.entityTarget = null;
        this.field_48156_b.getNavigator().func_48662_f();
    }

    public void updateTask()
    {
        this.field_48156_b.getLookHelper().setLookPositionWithEntity(this.entityTarget, 30.0F, 30.0F);

        if ((this.field_48153_f || this.field_48156_b.func_48318_al().func_48546_a(this.entityTarget)) && --this.field_48158_i <= 0)
        {
            this.field_48158_i = 4 + this.field_48156_b.getRNG().nextInt(7);
            this.field_48156_b.getNavigator().func_48652_a(this.entityTarget, this.field_48155_e);
        }

        this.field_46095_d = Math.max(this.field_46095_d - 1, 0);
        double var1 = (double)(this.field_48156_b.width * 2.0F * this.field_48156_b.width * 2.0F);

        if (this.field_48156_b.getDistanceSq(this.entityTarget.posX, this.entityTarget.boundingBox.minY, this.entityTarget.posZ) <= var1)
        {
            if (this.field_46095_d <= 0)
            {
                this.field_46095_d = 20;
                this.field_48156_b.attackEntityAsMob(this.entityTarget);
            }
        }
    }
}
