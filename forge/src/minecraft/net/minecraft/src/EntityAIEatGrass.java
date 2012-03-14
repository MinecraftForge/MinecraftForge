package net.minecraft.src;

public class EntityAIEatGrass extends EntityAIBase
{
    private EntityLiving field_48397_b;
    private World field_48398_c;
    int field_48399_a = 0;

    public EntityAIEatGrass(EntityLiving par1EntityLiving)
    {
        this.field_48397_b = par1EntityLiving;
        this.field_48398_c = par1EntityLiving.worldObj;
        this.setMutexBits(7);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_48397_b.getRNG().nextInt(this.field_48397_b.isChild() ? 50 : 1000) != 0)
        {
            return false;
        }
        else
        {
            int var1 = MathHelper.floor_double(this.field_48397_b.posX);
            int var2 = MathHelper.floor_double(this.field_48397_b.posY);
            int var3 = MathHelper.floor_double(this.field_48397_b.posZ);
            return this.field_48398_c.getBlockId(var1, var2, var3) == Block.tallGrass.blockID && this.field_48398_c.getBlockMetadata(var1, var2, var3) == 1 ? true : this.field_48398_c.getBlockId(var1, var2 - 1, var3) == Block.grass.blockID;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48399_a = 40;
        this.field_48398_c.setEntityState(this.field_48397_b, (byte)10);
        this.field_48397_b.getNavigator().func_48672_f();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_48399_a = 0;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.field_48399_a > 0;
    }

    public int func_48396_h()
    {
        return this.field_48399_a;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.field_48399_a = Math.max(0, this.field_48399_a - 1);

        if (this.field_48399_a == 4)
        {
            int var1 = MathHelper.floor_double(this.field_48397_b.posX);
            int var2 = MathHelper.floor_double(this.field_48397_b.posY);
            int var3 = MathHelper.floor_double(this.field_48397_b.posZ);

            if (this.field_48398_c.getBlockId(var1, var2, var3) == Block.tallGrass.blockID)
            {
                this.field_48398_c.playAuxSFX(2001, var1, var2, var3, Block.tallGrass.blockID + 4096);
                this.field_48398_c.setBlockWithNotify(var1, var2, var3, 0);
                this.field_48397_b.func_48095_u();
            }
            else if (this.field_48398_c.getBlockId(var1, var2 - 1, var3) == Block.grass.blockID)
            {
                this.field_48398_c.playAuxSFX(2001, var1, var2 - 1, var3, Block.grass.blockID);
                this.field_48398_c.setBlockWithNotify(var1, var2 - 1, var3, Block.dirt.blockID);
                this.field_48397_b.func_48095_u();
            }
        }
    }
}
