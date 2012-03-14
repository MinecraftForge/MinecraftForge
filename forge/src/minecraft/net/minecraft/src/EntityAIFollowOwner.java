package net.minecraft.src;

public class EntityAIFollowOwner extends EntityAIBase
{
    private EntityTameable field_48305_d;
    private EntityLiving field_48306_e;
    World field_48309_a;
    private float field_48303_f;
    private PathNavigate field_48304_g;
    private int field_48310_h;
    float field_48307_b;
    float field_48308_c;
    private boolean field_48311_i;

    public EntityAIFollowOwner(EntityTameable par1EntityTameable, float par2, float par3, float par4)
    {
        this.field_48305_d = par1EntityTameable;
        this.field_48309_a = par1EntityTameable.worldObj;
        this.field_48303_f = par2;
        this.field_48304_g = par1EntityTameable.getNavigator();
        this.field_48308_c = par3;
        this.field_48307_b = par4;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLiving var1 = this.field_48305_d.getOwner();

        if (var1 == null)
        {
            return false;
        }
        else if (this.field_48305_d.func_48141_af())
        {
            return false;
        }
        else if (this.field_48305_d.getDistanceSqToEntity(var1) < (double)(this.field_48308_c * this.field_48308_c))
        {
            return false;
        }
        else
        {
            this.field_48306_e = var1;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48304_g.noPath() && this.field_48305_d.getDistanceSqToEntity(this.field_48306_e) > (double)(this.field_48307_b * this.field_48307_b) && !this.field_48305_d.func_48141_af();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48310_h = 0;
        this.field_48311_i = this.field_48305_d.getNavigator().func_48658_a();
        this.field_48305_d.getNavigator().func_48664_a(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_48306_e = null;
        this.field_48304_g.func_48672_f();
        this.field_48305_d.getNavigator().func_48664_a(this.field_48311_i);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.field_48305_d.getLookHelper().setLookPositionWithEntity(this.field_48306_e, 10.0F, (float)this.field_48305_d.getVerticalFaceSpeed());

        if (!this.field_48305_d.func_48141_af())
        {
            if (--this.field_48310_h <= 0)
            {
                this.field_48310_h = 10;

                if (!this.field_48304_g.func_48667_a(this.field_48306_e, this.field_48303_f))
                {
                    if (this.field_48305_d.getDistanceSqToEntity(this.field_48306_e) >= 144.0D)
                    {
                        int var1 = MathHelper.floor_double(this.field_48306_e.posX) - 2;
                        int var2 = MathHelper.floor_double(this.field_48306_e.posZ) - 2;
                        int var3 = MathHelper.floor_double(this.field_48306_e.boundingBox.minY);

                        for (int var4 = 0; var4 <= 4; ++var4)
                        {
                            for (int var5 = 0; var5 <= 4; ++var5)
                            {
                                if ((var4 < 1 || var5 < 1 || var4 > 3 || var5 > 3) && this.field_48309_a.isBlockNormalCube(var1 + var4, var3 - 1, var2 + var5) && !this.field_48309_a.isBlockNormalCube(var1 + var4, var3, var2 + var5) && !this.field_48309_a.isBlockNormalCube(var1 + var4, var3 + 1, var2 + var5))
                                {
                                    this.field_48305_d.setLocationAndAngles((double)((float)(var1 + var4) + 0.5F), (double)var3, (double)((float)(var2 + var5) + 0.5F), this.field_48305_d.rotationYaw, this.field_48305_d.rotationPitch);
                                    this.field_48304_g.func_48672_f();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
