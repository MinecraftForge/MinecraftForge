package net.minecraft.src;

public class EntityAITempt extends EntityAIBase
{
    private EntityCreature field_48277_a;
    private float field_48275_b;
    private double field_48276_c;
    private double field_48273_d;
    private double field_48274_e;
    private double field_48271_f;
    private double field_48272_g;
    private EntityPlayer field_48282_h;
    private int field_48283_i = 0;
    private boolean field_48280_j;
    private int field_48281_k;
    private boolean field_48278_l;
    private boolean field_48279_m;

    public EntityAITempt(EntityCreature par1EntityCreature, float par2, int par3, boolean par4)
    {
        this.field_48277_a = par1EntityCreature;
        this.field_48275_b = par2;
        this.field_48281_k = par3;
        this.field_48278_l = par4;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_48283_i > 0)
        {
            --this.field_48283_i;
            return false;
        }
        else
        {
            this.field_48282_h = this.field_48277_a.worldObj.getClosestPlayerToEntity(this.field_48277_a, 10.0D);

            if (this.field_48282_h == null)
            {
                return false;
            }
            else
            {
                ItemStack var1 = this.field_48282_h.getCurrentEquippedItem();
                return var1 == null ? false : var1.itemID == this.field_48281_k;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (this.field_48278_l)
        {
            if (this.field_48277_a.getDistanceSqToEntity(this.field_48282_h) < 36.0D)
            {
                if (this.field_48282_h.getDistanceSq(this.field_48276_c, this.field_48273_d, this.field_48274_e) > 0.010000000000000002D)
                {
                    return false;
                }

                if (Math.abs((double)this.field_48282_h.rotationPitch - this.field_48271_f) > 5.0D || Math.abs((double)this.field_48282_h.rotationYaw - this.field_48272_g) > 5.0D)
                {
                    return false;
                }
            }
            else
            {
                this.field_48276_c = this.field_48282_h.posX;
                this.field_48273_d = this.field_48282_h.posY;
                this.field_48274_e = this.field_48282_h.posZ;
            }

            this.field_48271_f = (double)this.field_48282_h.rotationPitch;
            this.field_48272_g = (double)this.field_48282_h.rotationYaw;
        }

        return this.shouldExecute();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48276_c = this.field_48282_h.posX;
        this.field_48273_d = this.field_48282_h.posY;
        this.field_48274_e = this.field_48282_h.posZ;
        this.field_48280_j = true;
        this.field_48279_m = this.field_48277_a.getNavigator().func_48658_a();
        this.field_48277_a.getNavigator().func_48664_a(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_48282_h = null;
        this.field_48277_a.getNavigator().func_48672_f();
        this.field_48283_i = 100;
        this.field_48280_j = false;
        this.field_48277_a.getNavigator().func_48664_a(this.field_48279_m);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.field_48277_a.getLookHelper().setLookPositionWithEntity(this.field_48282_h, 30.0F, (float)this.field_48277_a.getVerticalFaceSpeed());

        if (this.field_48277_a.getDistanceSqToEntity(this.field_48282_h) < 6.25D)
        {
            this.field_48277_a.getNavigator().func_48672_f();
        }
        else
        {
            this.field_48277_a.getNavigator().func_48667_a(this.field_48282_h, this.field_48275_b);
        }
    }

    public boolean func_48270_h()
    {
        return this.field_48280_j;
    }
}
