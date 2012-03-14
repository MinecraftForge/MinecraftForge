package net.minecraft.src;

public abstract class EntityAITarget extends EntityAIBase
{
    protected EntityLiving field_48291_c;
    protected float field_48288_d;
    protected boolean field_48289_e;
    private boolean field_48292_a;
    private int field_48290_b;
    private int field_48286_f;
    private int field_48287_g;

    public EntityAITarget(EntityLiving par1EntityLiving, float par2, boolean par3)
    {
        this(par1EntityLiving, par2, par3, false);
    }

    public EntityAITarget(EntityLiving par1EntityLiving, float par2, boolean par3, boolean par4)
    {
        this.field_48290_b = 0;
        this.field_48286_f = 0;
        this.field_48287_g = 0;
        this.field_48291_c = par1EntityLiving;
        this.field_48288_d = par2;
        this.field_48289_e = par3;
        this.field_48292_a = par4;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        EntityLiving var1 = this.field_48291_c.func_48331_as();

        if (var1 == null)
        {
            return false;
        }
        else if (!var1.isEntityAlive())
        {
            return false;
        }
        else if (this.field_48291_c.getDistanceSqToEntity(var1) > (double)(this.field_48288_d * this.field_48288_d))
        {
            return false;
        }
        else
        {
            if (this.field_48289_e)
            {
                if (!this.field_48291_c.func_48318_al().func_48546_a(var1))
                {
                    if (++this.field_48287_g > 60)
                    {
                        return false;
                    }
                }
                else
                {
                    this.field_48287_g = 0;
                }
            }

            return true;
        }
    }

    public void startExecuting()
    {
        this.field_48290_b = 0;
        this.field_48286_f = 0;
        this.field_48287_g = 0;
    }

    public void resetTask()
    {
        this.field_48291_c.func_48327_b((EntityLiving)null);
    }

    protected boolean func_48284_a(EntityLiving par1EntityLiving, boolean par2)
    {
        if (par1EntityLiving == null)
        {
            return false;
        }
        else if (par1EntityLiving == this.field_48291_c)
        {
            return false;
        }
        else if (!par1EntityLiving.isEntityAlive())
        {
            return false;
        }
        else if (par1EntityLiving.boundingBox.maxY > this.field_48291_c.boundingBox.minY && par1EntityLiving.boundingBox.minY < this.field_48291_c.boundingBox.maxY)
        {
            if (!this.field_48291_c.func_48336_a(par1EntityLiving.getClass()))
            {
                return false;
            }
            else
            {
                if (this.field_48291_c instanceof EntityTameable && ((EntityTameable)this.field_48291_c).isTamed())
                {
                    if (par1EntityLiving instanceof EntityTameable && ((EntityTameable)par1EntityLiving).isTamed())
                    {
                        return false;
                    }

                    if (par1EntityLiving == ((EntityTameable)this.field_48291_c).getOwner())
                    {
                        return false;
                    }
                }
                else if (par1EntityLiving instanceof EntityPlayer && !par2 && ((EntityPlayer)par1EntityLiving).capabilities.disableDamage)
                {
                    return false;
                }

                if (!this.field_48291_c.isWithinHomeDistance(MathHelper.floor_double(par1EntityLiving.posX), MathHelper.floor_double(par1EntityLiving.posY), MathHelper.floor_double(par1EntityLiving.posZ)))
                {
                    return false;
                }
                else if (this.field_48289_e && !this.field_48291_c.func_48318_al().func_48546_a(par1EntityLiving))
                {
                    return false;
                }
                else
                {
                    if (this.field_48292_a)
                    {
                        if (--this.field_48286_f <= 0)
                        {
                            this.field_48290_b = 0;
                        }

                        if (this.field_48290_b == 0)
                        {
                            this.field_48290_b = this.func_48285_a(par1EntityLiving) ? 1 : 2;
                        }

                        if (this.field_48290_b == 2)
                        {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }
        else
        {
            return false;
        }
    }

    private boolean func_48285_a(EntityLiving par1EntityLiving)
    {
        this.field_48286_f = 10 + this.field_48291_c.getRNG().nextInt(5);
        PathEntity var2 = this.field_48291_c.getNavigator().func_48661_a(par1EntityLiving);

        if (var2 == null)
        {
            return false;
        }
        else
        {
            PathPoint var3 = var2.func_48425_c();

            if (var3 == null)
            {
                return false;
            }
            else
            {
                int var4 = var3.xCoord - MathHelper.floor_double(par1EntityLiving.posX);
                int var5 = var3.zCoord - MathHelper.floor_double(par1EntityLiving.posZ);
                return (double)(var4 * var4 + var5 * var5) <= 2.25D;
            }
        }
    }
}
