package net.minecraft.src;

public class EntityAIFollowOwner extends EntityAIBase
{
    private EntityTameable field_48247_d;
    private EntityLiving field_48248_e;
    World field_48251_a;
    private float field_48245_f;
    private PathNavigate field_48246_g;
    private int field_48252_h;
    float field_48249_b;
    float field_48250_c;
    private boolean field_48253_i;

    public EntityAIFollowOwner(EntityTameable par1EntityTameable, float par2, float par3, float par4)
    {
        this.field_48247_d = par1EntityTameable;
        this.field_48251_a = par1EntityTameable.worldObj;
        this.field_48245_f = par2;
        this.field_48246_g = par1EntityTameable.getNavigator();
        this.field_48250_c = par3;
        this.field_48249_b = par4;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLiving var1 = this.field_48247_d.getOwner();

        if (var1 == null)
        {
            return false;
        }
        else if (this.field_48247_d.func_48371_v_())
        {
            return false;
        }
        else if (this.field_48247_d.getDistanceSqToEntity(var1) < (double)(this.field_48250_c * this.field_48250_c))
        {
            return false;
        }
        else
        {
            this.field_48248_e = var1;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48246_g.noPath() && this.field_48247_d.getDistanceSqToEntity(this.field_48248_e) > (double)(this.field_48249_b * this.field_48249_b) && !this.field_48247_d.func_48371_v_();
    }

    public void startExecuting()
    {
        this.field_48252_h = 0;
        this.field_48253_i = this.field_48247_d.getNavigator().func_48649_a();
        this.field_48247_d.getNavigator().func_48656_a(false);
    }

    public void resetTask()
    {
        this.field_48248_e = null;
        this.field_48246_g.func_48662_f();
        this.field_48247_d.getNavigator().func_48656_a(this.field_48253_i);
    }

    public void updateTask()
    {
        this.field_48247_d.getLookHelper().setLookPositionWithEntity(this.field_48248_e, 10.0F, (float)this.field_48247_d.getVerticalFaceSpeed());

        if (!this.field_48247_d.func_48371_v_())
        {
            if (--this.field_48252_h <= 0)
            {
                this.field_48252_h = 10;

                if (!this.field_48246_g.func_48652_a(this.field_48248_e, this.field_48245_f))
                {
                    if (this.field_48247_d.getDistanceSqToEntity(this.field_48248_e) >= 144.0D)
                    {
                        int var1 = MathHelper.floor_double(this.field_48248_e.posX) - 2;
                        int var2 = MathHelper.floor_double(this.field_48248_e.posZ) - 2;
                        int var3 = MathHelper.floor_double(this.field_48248_e.boundingBox.minY);

                        for (int var4 = 0; var4 <= 4; ++var4)
                        {
                            for (int var5 = 0; var5 <= 4; ++var5)
                            {
                                if ((var4 < 1 || var5 < 1 || var4 > 3 || var5 > 3) && this.field_48251_a.isBlockNormalCube(var1 + var4, var3 - 1, var2 + var5) && !this.field_48251_a.isBlockNormalCube(var1 + var4, var3, var2 + var5) && !this.field_48251_a.isBlockNormalCube(var1 + var4, var3 + 1, var2 + var5))
                                {
                                    this.field_48247_d.setLocationAndAngles((double)((float)(var1 + var4) + 0.5F), (double)var3, (double)((float)(var2 + var5) + 0.5F), this.field_48247_d.rotationYaw, this.field_48247_d.rotationPitch);
                                    this.field_48246_g.func_48662_f();
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
