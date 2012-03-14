package net.minecraft.src;

public class EntityAIMoveTowardsTarget extends EntityAIBase
{
    private EntityCreature field_48223_a;
    private EntityLiving field_48221_b;
    private double field_48222_c;
    private double field_48219_d;
    private double field_48220_e;
    private float field_48217_f;
    private float field_48218_g;

    public EntityAIMoveTowardsTarget(EntityCreature par1EntityCreature, float par2, float par3)
    {
        this.field_48223_a = par1EntityCreature;
        this.field_48217_f = par2;
        this.field_48218_g = par3;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        this.field_48221_b = this.field_48223_a.func_48331_as();

        if (this.field_48221_b == null)
        {
            return false;
        }
        else if (this.field_48221_b.getDistanceSqToEntity(this.field_48223_a) > (double)(this.field_48218_g * this.field_48218_g))
        {
            return false;
        }
        else
        {
            Vec3D var1 = RandomPositionGenerator.func_48395_a(this.field_48223_a, 16, 7, Vec3D.createVector(this.field_48221_b.posX, this.field_48221_b.posY, this.field_48221_b.posZ));

            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.field_48222_c = var1.xCoord;
                this.field_48219_d = var1.yCoord;
                this.field_48220_e = var1.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48223_a.getNavigator().noPath() && this.field_48221_b.isEntityAlive() && this.field_48221_b.getDistanceSqToEntity(this.field_48223_a) < (double)(this.field_48218_g * this.field_48218_g);
    }

    public void resetTask()
    {
        this.field_48221_b = null;
    }

    public void startExecuting()
    {
        this.field_48223_a.getNavigator().func_48658_a(this.field_48222_c, this.field_48219_d, this.field_48220_e, this.field_48217_f);
    }
}
