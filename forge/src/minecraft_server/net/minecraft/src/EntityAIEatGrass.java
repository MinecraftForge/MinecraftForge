package net.minecraft.src;

public class EntityAIEatGrass extends EntityAIBase
{
    private EntityLiving field_48228_b;
    private World field_48229_c;
    int field_48230_a = 0;

    public EntityAIEatGrass(EntityLiving par1EntityLiving)
    {
        this.field_48228_b = par1EntityLiving;
        this.field_48229_c = par1EntityLiving.worldObj;
        this.setMutexBits(7);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_48228_b.getRNG().nextInt(this.field_48228_b.isChild() ? 50 : 1000) != 0)
        {
            return false;
        }
        else
        {
            int var1 = MathHelper.floor_double(this.field_48228_b.posX);
            int var2 = MathHelper.floor_double(this.field_48228_b.posY);
            int var3 = MathHelper.floor_double(this.field_48228_b.posZ);
            return this.field_48229_c.getBlockId(var1, var2, var3) == Block.tallGrass.blockID && this.field_48229_c.getBlockMetadata(var1, var2, var3) == 1 ? true : this.field_48229_c.getBlockId(var1, var2 - 1, var3) == Block.grass.blockID;
        }
    }

    public void startExecuting()
    {
        this.field_48230_a = 40;
        this.field_48229_c.setEntityState(this.field_48228_b, (byte)10);
        this.field_48228_b.getNavigator().func_48662_f();
    }

    public void resetTask()
    {
        this.field_48230_a = 0;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.field_48230_a > 0;
    }

    public int func_48227_f()
    {
        return this.field_48230_a;
    }

    public void updateTask()
    {
        this.field_48230_a = Math.max(0, this.field_48230_a - 1);

        if (this.field_48230_a == 4)
        {
            int var1 = MathHelper.floor_double(this.field_48228_b.posX);
            int var2 = MathHelper.floor_double(this.field_48228_b.posY);
            int var3 = MathHelper.floor_double(this.field_48228_b.posZ);

            if (this.field_48229_c.getBlockId(var1, var2, var3) == Block.tallGrass.blockID)
            {
                this.field_48229_c.playAuxSFX(2001, var1, var2, var3, Block.tallGrass.blockID + 4096);
                this.field_48229_c.setBlockWithNotify(var1, var2, var3, 0);
                this.field_48228_b.func_48319_z();
            }
            else if (this.field_48229_c.getBlockId(var1, var2 - 1, var3) == Block.grass.blockID)
            {
                this.field_48229_c.playAuxSFX(2001, var1, var2 - 1, var3, Block.grass.blockID);
                this.field_48229_c.setBlockWithNotify(var1, var2 - 1, var3, Block.dirt.blockID);
                this.field_48228_b.func_48319_z();
            }
        }
    }
}
