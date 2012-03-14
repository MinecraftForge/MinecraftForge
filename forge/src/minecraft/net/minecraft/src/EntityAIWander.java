package net.minecraft.src;

public class EntityAIWander extends EntityAIBase
{
    private EntityCreature entity;
    private double field_46098_b;
    private double field_46099_c;
    private double field_46097_d;
    private float field_48317_e;

    public EntityAIWander(EntityCreature par1EntityCreature, float par2)
    {
        this.entity = par1EntityCreature;
        this.field_48317_e = par2;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.entity.getAge() >= 100)
        {
            return false;
        }
        else if (this.entity.getRNG().nextInt(120) != 0)
        {
            return false;
        }
        else
        {
            Vec3D var1 = RandomPositionGenerator.func_48622_a(this.entity, 10, 7);

            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.field_46098_b = var1.xCoord;
                this.field_46099_c = var1.yCoord;
                this.field_46097_d = var1.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.entity.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.entity.getNavigator().func_48666_a(this.field_46098_b, this.field_46099_c, this.field_46097_d, this.field_48317_e);
    }
}
