package net.minecraft.src;

public class EntityAIMoveIndoors extends EntityAIBase
{
    private EntityCreature entityObj;
    private VillageDoorInfo field_48173_b;
    private int field_48174_c = -1;
    private int field_48172_d = -1;

    public EntityAIMoveIndoors(EntityCreature par1EntityCreature)
    {
        this.entityObj = par1EntityCreature;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if ((!this.entityObj.worldObj.isDaytime() || this.entityObj.worldObj.isRaining()) && !this.entityObj.worldObj.worldProvider.hasNoSky)
        {
            if (this.entityObj.getRNG().nextInt(50) != 0)
            {
                return false;
            }
            else if (this.field_48174_c != -1 && this.entityObj.getDistanceSq((double)this.field_48174_c, this.entityObj.posY, (double)this.field_48172_d) < 4.0D)
            {
                return false;
            }
            else
            {
                Village var1 = this.entityObj.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ), 14);

                if (var1 == null)
                {
                    return false;
                }
                else
                {
                    this.field_48173_b = var1.findNearestDoorUnrestricted(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ));
                    return this.field_48173_b != null;
                }
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.entityObj.getNavigator().noPath();
    }

    public void startExecuting()
    {
        this.field_48174_c = -1;

        if (this.entityObj.getDistanceSq((double)this.field_48173_b.getInsidePosX(), (double)this.field_48173_b.posY, (double)this.field_48173_b.getInsidePosZ()) > 256.0D)
        {
            Vec3D var1 = RandomPositionGenerator.func_48395_a(this.entityObj, 14, 3, Vec3D.createVector((double)this.field_48173_b.getInsidePosX() + 0.5D, (double)this.field_48173_b.getInsidePosY(), (double)this.field_48173_b.getInsidePosZ() + 0.5D));

            if (var1 != null)
            {
                this.entityObj.getNavigator().func_48658_a(var1.xCoord, var1.yCoord, var1.zCoord, 0.3F);
            }
        }
        else
        {
            this.entityObj.getNavigator().func_48658_a((double)this.field_48173_b.getInsidePosX() + 0.5D, (double)this.field_48173_b.getInsidePosY(), (double)this.field_48173_b.getInsidePosZ() + 0.5D, 0.3F);
        }
    }

    public void resetTask()
    {
        this.field_48174_c = this.field_48173_b.getInsidePosX();
        this.field_48172_d = this.field_48173_b.getInsidePosZ();
        this.field_48173_b = null;
    }
}
