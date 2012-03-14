package net.minecraft.src;

public class EntityAIArrowAttack extends EntityAIBase
{
    World field_48183_a;
    EntityLiving field_48181_b;
    EntityLiving field_48182_c;
    int field_48179_d = 0;
    float field_48180_e;
    int field_48177_f = 0;
    int field_48178_g;
    int field_48184_h;

    public EntityAIArrowAttack(EntityLiving par1EntityLiving, float par2, int par3, int par4)
    {
        this.field_48181_b = par1EntityLiving;
        this.field_48183_a = par1EntityLiving.worldObj;
        this.field_48180_e = par2;
        this.field_48178_g = par3;
        this.field_48184_h = par4;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLiving var1 = this.field_48181_b.func_48331_as();

        if (var1 == null)
        {
            return false;
        }
        else
        {
            this.field_48182_c = var1;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.shouldExecute() || !this.field_48181_b.getNavigator().noPath();
    }

    public void resetTask()
    {
        this.field_48182_c = null;
    }

    public void updateTask()
    {
        double var1 = 100.0D;
        double var3 = this.field_48181_b.getDistanceSq(this.field_48182_c.posX, this.field_48182_c.boundingBox.minY, this.field_48182_c.posZ);
        boolean var5 = this.field_48181_b.func_48318_al().func_48546_a(this.field_48182_c);

        if (var5)
        {
            ++this.field_48177_f;
        }
        else
        {
            this.field_48177_f = 0;
        }

        if (var3 <= var1 && this.field_48177_f >= 20)
        {
            this.field_48181_b.getNavigator().func_48662_f();
        }
        else
        {
            this.field_48181_b.getNavigator().func_48652_a(this.field_48182_c, this.field_48180_e);
        }

        this.field_48181_b.getLookHelper().setLookPositionWithEntity(this.field_48182_c, 30.0F, 30.0F);
        this.field_48179_d = Math.max(this.field_48179_d - 1, 0);

        if (this.field_48179_d <= 0)
        {
            if (var3 <= var1 && var5)
            {
                this.func_48176_f();
                this.field_48179_d = this.field_48184_h;
            }
        }
    }

    private void func_48176_f()
    {
        if (this.field_48178_g == 1)
        {
            EntityArrow var1 = new EntityArrow(this.field_48183_a, this.field_48181_b, this.field_48182_c, 1.6F, 12.0F);
            this.field_48183_a.playSoundAtEntity(this.field_48181_b, "random.bow", 1.0F, 1.0F / (this.field_48181_b.getRNG().nextFloat() * 0.4F + 0.8F));
            this.field_48183_a.spawnEntityInWorld(var1);
        }
        else if (this.field_48178_g == 2)
        {
            EntitySnowball var9 = new EntitySnowball(this.field_48183_a, this.field_48181_b);
            double var2 = this.field_48182_c.posX - this.field_48181_b.posX;
            double var4 = this.field_48182_c.posY + (double)this.field_48182_c.getEyeHeight() - 1.100000023841858D - var9.posY;
            double var6 = this.field_48182_c.posZ - this.field_48181_b.posZ;
            float var8 = MathHelper.sqrt_double(var2 * var2 + var6 * var6) * 0.2F;
            var9.setThrowableHeading(var2, var4 + (double)var8, var6, 1.6F, 12.0F);
            this.field_48183_a.playSoundAtEntity(this.field_48181_b, "random.bow", 1.0F, 1.0F / (this.field_48181_b.getRNG().nextFloat() * 0.4F + 0.8F));
            this.field_48183_a.spawnEntityInWorld(var9);
        }
    }
}
