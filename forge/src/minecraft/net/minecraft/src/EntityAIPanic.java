package net.minecraft.src;

public class EntityAIPanic extends EntityAIBase
{
    private EntityCreature field_48316_a;
    private float field_48314_b;
    private double field_48315_c;
    private double field_48312_d;
    private double field_48313_e;

    public EntityAIPanic(EntityCreature par1EntityCreature, float par2)
    {
        this.field_48316_a = par1EntityCreature;
        this.field_48314_b = par2;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_48316_a.getAITarget() == null)
        {
            return false;
        }
        else
        {
            Vec3D var1 = RandomPositionGenerator.func_48622_a(this.field_48316_a, 5, 4);

            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.field_48315_c = var1.xCoord;
                this.field_48312_d = var1.yCoord;
                this.field_48313_e = var1.zCoord;
                return true;
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48316_a.getNavigator().func_48666_a(this.field_48315_c, this.field_48312_d, this.field_48313_e, this.field_48314_b);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48316_a.getNavigator().noPath();
    }
}
