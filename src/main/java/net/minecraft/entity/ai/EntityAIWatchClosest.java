package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIWatchClosest extends EntityAIBase
{
    private EntityLiving theWatcher;
    // JAVADOC FIELD $$ field_75334_a
    protected Entity closestEntity;
    // JAVADOC FIELD $$ field_75333_c
    private float maxDistanceForPlayer;
    private int lookTime;
    private float field_75331_e;
    private Class watchedClass;
    private static final String __OBFID = "CL_00001592";

    public EntityAIWatchClosest(EntityLiving par1EntityLiving, Class par2Class, float par3)
    {
        this.theWatcher = par1EntityLiving;
        this.watchedClass = par2Class;
        this.maxDistanceForPlayer = par3;
        this.field_75331_e = 0.02F;
        this.setMutexBits(2);
    }

    public EntityAIWatchClosest(EntityLiving par1EntityLiving, Class par2Class, float par3, float par4)
    {
        this.theWatcher = par1EntityLiving;
        this.watchedClass = par2Class;
        this.maxDistanceForPlayer = par3;
        this.field_75331_e = par4;
        this.setMutexBits(2);
    }

    // JAVADOC METHOD $$ func_75250_a
    public boolean shouldExecute()
    {
        if (this.theWatcher.getRNG().nextFloat() >= this.field_75331_e)
        {
            return false;
        }
        else
        {
            if (this.theWatcher.getAttackTarget() != null)
            {
                this.closestEntity = this.theWatcher.getAttackTarget();
            }

            if (this.watchedClass == EntityPlayer.class)
            {
                this.closestEntity = this.theWatcher.worldObj.getClosestPlayerToEntity(this.theWatcher, (double)this.maxDistanceForPlayer);
            }
            else
            {
                this.closestEntity = this.theWatcher.worldObj.findNearestEntityWithinAABB(this.watchedClass, this.theWatcher.boundingBox.expand((double)this.maxDistanceForPlayer, 3.0D, (double)this.maxDistanceForPlayer), this.theWatcher);
            }

            return this.closestEntity != null;
        }
    }

    // JAVADOC METHOD $$ func_75253_b
    public boolean continueExecuting()
    {
        return !this.closestEntity.isEntityAlive() ? false : (this.theWatcher.getDistanceSqToEntity(this.closestEntity) > (double)(this.maxDistanceForPlayer * this.maxDistanceForPlayer) ? false : this.lookTime > 0);
    }

    // JAVADOC METHOD $$ func_75249_e
    public void startExecuting()
    {
        this.lookTime = 40 + this.theWatcher.getRNG().nextInt(40);
    }

    // JAVADOC METHOD $$ func_75251_c
    public void resetTask()
    {
        this.closestEntity = null;
    }

    // JAVADOC METHOD $$ func_75246_d
    public void updateTask()
    {
        this.theWatcher.getLookHelper().setLookPosition(this.closestEntity.posX, this.closestEntity.posY + (double)this.closestEntity.getEyeHeight(), this.closestEntity.posZ, 10.0F, (float)this.theWatcher.getVerticalFaceSpeed());
        --this.lookTime;
    }
}