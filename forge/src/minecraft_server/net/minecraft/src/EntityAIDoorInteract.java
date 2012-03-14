package net.minecraft.src;

public abstract class EntityAIDoorInteract extends EntityAIBase
{
    protected EntityLiving field_48192_a;
    protected int field_48190_b;
    protected int field_48191_c;
    protected int field_48188_d;
    protected BlockDoor field_48189_e;
    boolean field_48186_f;
    float field_48187_g;
    float field_48193_h;

    public EntityAIDoorInteract(EntityLiving par1EntityLiving)
    {
        this.field_48192_a = par1EntityLiving;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.field_48192_a.isCollidedHorizontally)
        {
            return false;
        }
        else
        {
            PathNavigate var1 = this.field_48192_a.getNavigator();
            PathEntity var2 = var1.func_48668_c();

            if (var2 != null && !var2.isFinished() && var1.func_48657_b())
            {
                for (int var3 = 0; var3 < Math.min(var2.func_48423_e() + 2, var2.func_48424_d()); ++var3)
                {
                    PathPoint var4 = var2.func_48429_a(var3);
                    this.field_48190_b = var4.xCoord;
                    this.field_48191_c = var4.yCoord + 1;
                    this.field_48188_d = var4.zCoord;

                    if (this.field_48192_a.getDistanceSq((double)this.field_48190_b, this.field_48192_a.posY, (double)this.field_48188_d) <= 2.25D)
                    {
                        this.field_48189_e = this.func_48185_a(this.field_48190_b, this.field_48191_c, this.field_48188_d);

                        if (this.field_48189_e != null)
                        {
                            return true;
                        }
                    }
                }

                this.field_48190_b = MathHelper.floor_double(this.field_48192_a.posX);
                this.field_48191_c = MathHelper.floor_double(this.field_48192_a.posY + 1.0D);
                this.field_48188_d = MathHelper.floor_double(this.field_48192_a.posZ);
                this.field_48189_e = this.func_48185_a(this.field_48190_b, this.field_48191_c, this.field_48188_d);
                return this.field_48189_e != null;
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
        return !this.field_48186_f;
    }

    public void startExecuting()
    {
        this.field_48186_f = false;
        this.field_48187_g = (float)((double)((float)this.field_48190_b + 0.5F) - this.field_48192_a.posX);
        this.field_48193_h = (float)((double)((float)this.field_48188_d + 0.5F) - this.field_48192_a.posZ);
    }

    public void updateTask()
    {
        float var1 = (float)((double)((float)this.field_48190_b + 0.5F) - this.field_48192_a.posX);
        float var2 = (float)((double)((float)this.field_48188_d + 0.5F) - this.field_48192_a.posZ);
        float var3 = this.field_48187_g * var1 + this.field_48193_h * var2;

        if (var3 < 0.0F)
        {
            this.field_48186_f = true;
        }
    }

    private BlockDoor func_48185_a(int par1, int par2, int par3)
    {
        int var4 = this.field_48192_a.worldObj.getBlockId(par1, par2, par3);

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
