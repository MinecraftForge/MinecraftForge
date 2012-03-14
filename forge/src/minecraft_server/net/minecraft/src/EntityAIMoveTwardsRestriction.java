package net.minecraft.src;

public class EntityAIMoveTwardsRestriction extends EntityAIBase
{
    private EntityCreature field_48152_a;
    private double field_48150_b;
    private double field_48151_c;
    private double field_48148_d;
    private float field_48149_e;

    public EntityAIMoveTwardsRestriction(EntityCreature par1EntityCreature, float par2)
    {
        this.field_48152_a = par1EntityCreature;
        this.field_48149_e = par2;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_48152_a.func_48325_at())
        {
            return false;
        }
        else
        {
            ChunkCoordinates var1 = this.field_48152_a.getHomePosition();
            Vec3D var2 = RandomPositionGenerator.func_48395_a(this.field_48152_a, 16, 7, Vec3D.createVector((double)var1.posX, (double)var1.posY, (double)var1.posZ));

            if (var2 == null)
            {
                return false;
            }
            else
            {
                this.field_48150_b = var2.xCoord;
                this.field_48151_c = var2.yCoord;
                this.field_48148_d = var2.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48152_a.getNavigator().noPath();
    }

    public void startExecuting()
    {
        this.field_48152_a.getNavigator().func_48658_a(this.field_48150_b, this.field_48151_c, this.field_48148_d, this.field_48149_e);
    }
}
