package net.minecraft.src;

public abstract class EntityAIDoorInteract extends EntityAIBase
{
    protected EntityLiving field_48325_a;
    protected int field_48323_b;
    protected int field_48324_c;
    protected int field_48321_d;
    protected BlockDoor field_48322_e;
    boolean field_48319_f;
    float field_48320_g;
    float field_48326_h;

    public EntityAIDoorInteract(EntityLiving par1EntityLiving)
    {
        this.field_48325_a = par1EntityLiving;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.field_48325_a.isCollidedHorizontally)
        {
            return false;
        }
        else
        {
            PathNavigate var1 = this.field_48325_a.getNavigator();
            PathEntity var2 = var1.func_48670_c();

            if (var2 != null && !var2.isFinished() && var1.func_48665_b())
            {
                for (int var3 = 0; var3 < Math.min(var2.getCurrentPathIndex() + 2, var2.getCurrentPathLength()); ++var3)
                {
                    PathPoint var4 = var2.getPathPointFromIndex(var3);
                    this.field_48323_b = var4.xCoord;
                    this.field_48324_c = var4.yCoord + 1;
                    this.field_48321_d = var4.zCoord;

                    if (this.field_48325_a.getDistanceSq((double)this.field_48323_b, this.field_48325_a.posY, (double)this.field_48321_d) <= 2.25D)
                    {
                        this.field_48322_e = this.func_48318_a(this.field_48323_b, this.field_48324_c, this.field_48321_d);

                        if (this.field_48322_e != null)
                        {
                            return true;
                        }
                    }
                }

                this.field_48323_b = MathHelper.floor_double(this.field_48325_a.posX);
                this.field_48324_c = MathHelper.floor_double(this.field_48325_a.posY + 1.0D);
                this.field_48321_d = MathHelper.floor_double(this.field_48325_a.posZ);
                this.field_48322_e = this.func_48318_a(this.field_48323_b, this.field_48324_c, this.field_48321_d);
                return this.field_48322_e != null;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48319_f;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48319_f = false;
        this.field_48320_g = (float)((double)((float)this.field_48323_b + 0.5F) - this.field_48325_a.posX);
        this.field_48326_h = (float)((double)((float)this.field_48321_d + 0.5F) - this.field_48325_a.posZ);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        float var1 = (float)((double)((float)this.field_48323_b + 0.5F) - this.field_48325_a.posX);
        float var2 = (float)((double)((float)this.field_48321_d + 0.5F) - this.field_48325_a.posZ);
        float var3 = this.field_48320_g * var1 + this.field_48326_h * var2;

        if (var3 < 0.0F)
        {
            this.field_48319_f = true;
        }
    }

    private BlockDoor func_48318_a(int par1, int par2, int par3)
    {
        int var4 = this.field_48325_a.worldObj.getBlockId(par1, par2, par3);

        if (var4 != Block.doorWood.blockID)
        {
            return null;
        }
        else
        {
            BlockDoor var5 = (BlockDoor)Block.blocksList[var4];
            return var5;
        }
    }
}
