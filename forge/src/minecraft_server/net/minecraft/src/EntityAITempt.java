package net.minecraft.src;

public class EntityAITempt extends EntityAIBase
{
    private EntityCreature field_48268_a;
    private float field_48266_b;
    private double field_48267_c;
    private double field_48264_d;
    private double field_48265_e;
    private double field_48262_f;
    private double field_48263_g;
    private EntityPlayer field_48273_h;
    private int field_48274_i = 0;
    private boolean field_48271_j;
    private int field_48272_k;
    private boolean field_48269_l;
    private boolean field_48270_m;

    public EntityAITempt(EntityCreature par1EntityCreature, float par2, int par3, boolean par4)
    {
        this.field_48268_a = par1EntityCreature;
        this.field_48266_b = par2;
        this.field_48272_k = par3;
        this.field_48269_l = par4;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_48274_i > 0)
        {
            --this.field_48274_i;
            return false;
        }
        else
        {
            this.field_48273_h = this.field_48268_a.worldObj.getClosestPlayerToEntity(this.field_48268_a, 10.0D);

            if (this.field_48273_h == null)
            {
                return false;
            }
            else
            {
                ItemStack var1 = this.field_48273_h.getCurrentEquippedItem();
                return var1 == null ? false : var1.itemID == this.field_48272_k;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (this.field_48269_l)
        {
            if (this.field_48268_a.getDistanceSqToEntity(this.field_48273_h) < 36.0D)
            {
                if (this.field_48273_h.getDistanceSq(this.field_48267_c, this.field_48264_d, this.field_48265_e) > 0.010000000000000002D)
                {
                    return false;
                }

                if (Math.abs((double)this.field_48273_h.rotationPitch - this.field_48262_f) > 5.0D || Math.abs((double)this.field_48273_h.rotationYaw - this.field_48263_g) > 5.0D)
                {
                    return false;
                }
            }
            else
            {
                this.field_48267_c = this.field_48273_h.posX;
                this.field_48264_d = this.field_48273_h.posY;
                this.field_48265_e = this.field_48273_h.posZ;
            }

            this.field_48262_f = (double)this.field_48273_h.rotationPitch;
            this.field_48263_g = (double)this.field_48273_h.rotationYaw;
        }

        return this.shouldExecute();
    }

    public void startExecuting()
    {
        this.field_48267_c = this.field_48273_h.posX;
        this.field_48264_d = this.field_48273_h.posY;
        this.field_48265_e = this.field_48273_h.posZ;
        this.field_48271_j = true;
        this.field_48270_m = this.field_48268_a.getNavigator().func_48649_a();
        this.field_48268_a.getNavigator().func_48656_a(false);
    }

    public void resetTask()
    {
        this.field_48273_h = null;
        this.field_48268_a.getNavigator().func_48662_f();
        this.field_48274_i = 100;
        this.field_48271_j = false;
        this.field_48268_a.getNavigator().func_48656_a(this.field_48270_m);
    }

    public void updateTask()
    {
        this.field_48268_a.getLookHelper().setLookPositionWithEntity(this.field_48273_h, 30.0F, (float)this.field_48268_a.getVerticalFaceSpeed());

        if (this.field_48268_a.getDistanceSqToEntity(this.field_48273_h) < 6.25D)
        {
            this.field_48268_a.getNavigator().func_48662_f();
        }
        else
        {
            this.field_48268_a.getNavigator().func_48652_a(this.field_48273_h, this.field_48266_b);
        }
    }

    public boolean func_48261_f()
    {
        return this.field_48271_j;
    }
}
