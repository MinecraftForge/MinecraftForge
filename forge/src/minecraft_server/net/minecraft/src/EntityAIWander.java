package net.minecraft.src;

public class EntityAIWander extends EntityAIBase
{
    private EntityCreature entity;
    private double field_46102_b;
    private double field_46103_c;
    private double field_46101_d;
    private float field_48209_e;

    public EntityAIWander(EntityCreature par1EntityCreature, float par2)
    {
        this.entity = par1EntityCreature;
        this.field_48209_e = par2;
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
            Vec3D var1 = RandomPositionGenerator.func_48396_a(this.entity, 10, 7);

            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.field_46102_b = var1.xCoord;
                this.field_46103_c = var1.yCoord;
                this.field_46101_d = var1.zCoord;
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

    public void startExecuting()
    {
        this.entity.getNavigator().func_48658_a(this.field_46102_b, this.field_46103_c, this.field_46101_d, this.field_48209_e);
    }
}
