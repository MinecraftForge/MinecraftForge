package net.minecraft.src;

public class EntityAIMoveTwardsRestriction extends EntityAIBase
{
    private EntityCreature field_48355_a;
    private double field_48353_b;
    private double field_48354_c;
    private double field_48351_d;
    private float field_48352_e;

    public EntityAIMoveTwardsRestriction(EntityCreature par1EntityCreature, float par2)
    {
        this.field_48355_a = par1EntityCreature;
        this.field_48352_e = par2;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_48355_a.func_48093_aT())
        {
            return false;
        }
        else
        {
            ChunkCoordinates var1 = this.field_48355_a.getHomePosition();
            Vec3D var2 = RandomPositionGenerator.func_48620_a(this.field_48355_a, 16, 7, Vec3D.createVector((double)var1.posX, (double)var1.posY, (double)var1.posZ));

            if (var2 == null)
            {
                return false;
            }
            else
            {
                this.field_48353_b = var2.xCoord;
                this.field_48354_c = var2.yCoord;
                this.field_48351_d = var2.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48355_a.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48355_a.getNavigator().func_48666_a(this.field_48353_b, this.field_48354_c, this.field_48351_d, this.field_48352_e);
    }
}
