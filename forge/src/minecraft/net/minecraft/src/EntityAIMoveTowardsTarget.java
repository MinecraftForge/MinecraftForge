package net.minecraft.src;

public class EntityAIMoveTowardsTarget extends EntityAIBase
{
    private EntityCreature field_48336_a;
    private EntityLiving field_48334_b;
    private double field_48335_c;
    private double field_48332_d;
    private double field_48333_e;
    private float field_48330_f;
    private float field_48331_g;

    public EntityAIMoveTowardsTarget(EntityCreature par1EntityCreature, float par2, float par3)
    {
        this.field_48336_a = par1EntityCreature;
        this.field_48330_f = par2;
        this.field_48331_g = par3;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        this.field_48334_b = this.field_48336_a.func_48094_aS();

        if (this.field_48334_b == null)
        {
            return false;
        }
        else if (this.field_48334_b.getDistanceSqToEntity(this.field_48336_a) > (double)(this.field_48331_g * this.field_48331_g))
        {
            return false;
        }
        else
        {
            Vec3D var1 = RandomPositionGenerator.func_48620_a(this.field_48336_a, 16, 7, Vec3D.createVector(this.field_48334_b.posX, this.field_48334_b.posY, this.field_48334_b.posZ));

            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.field_48335_c = var1.xCoord;
                this.field_48332_d = var1.yCoord;
                this.field_48333_e = var1.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48336_a.getNavigator().noPath() && this.field_48334_b.isEntityAlive() && this.field_48334_b.getDistanceSqToEntity(this.field_48336_a) < (double)(this.field_48331_g * this.field_48331_g);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_48334_b = null;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48336_a.getNavigator().func_48666_a(this.field_48335_c, this.field_48332_d, this.field_48333_e, this.field_48330_f);
    }
}
