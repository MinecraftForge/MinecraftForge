package net.minecraft.src;

public class EntityAIRestrictOpenDoor extends EntityAIBase
{
    private EntityCreature entityObj;
    private VillageDoorInfo frontDoor;

    public EntityAIRestrictOpenDoor(EntityCreature par1EntityCreature)
    {
        this.entityObj = par1EntityCreature;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.entityObj.worldObj.isDaytime())
        {
            return false;
        }
        else
        {
            Village var1 = this.entityObj.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ), 16);

            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.frontDoor = var1.findNearestDoor(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ));
                return this.frontDoor == null ? false : (double)this.frontDoor.getInsideDistanceSquared(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ)) < 2.25D;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.entityObj.worldObj.isDaytime() ? false : !this.frontDoor.isDetachedFromVillageFlag && this.frontDoor.isInside(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posZ));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.entityObj.getNavigator().func_48673_b(false);
        this.entityObj.getNavigator().func_48663_c(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.entityObj.getNavigator().func_48673_b(true);
        this.entityObj.getNavigator().func_48663_c(true);
        this.frontDoor = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.frontDoor.incrementDoorOpeningRestrictionCounter();
    }
}
