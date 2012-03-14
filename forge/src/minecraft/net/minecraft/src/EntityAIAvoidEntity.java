package net.minecraft.src;

import java.util.List;

public class EntityAIAvoidEntity extends EntityAIBase
{
    private EntityCreature field_48244_a;
    private float field_48242_b;
    private float field_48243_c;
    private Entity field_48240_d;
    private float field_48241_e;
    private PathEntity field_48238_f;
    private PathNavigate field_48239_g;
    private Class field_48245_h;

    public EntityAIAvoidEntity(EntityCreature par1EntityCreature, Class par2Class, float par3, float par4, float par5)
    {
        this.field_48244_a = par1EntityCreature;
        this.field_48245_h = par2Class;
        this.field_48241_e = par3;
        this.field_48242_b = par4;
        this.field_48243_c = par5;
        this.field_48239_g = par1EntityCreature.getNavigator();
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_48245_h == EntityPlayer.class)
        {
            if (this.field_48244_a instanceof EntityTameable && ((EntityTameable)this.field_48244_a).isTamed())
            {
                return false;
            }

            this.field_48240_d = this.field_48244_a.worldObj.getClosestPlayerToEntity(this.field_48244_a, (double)this.field_48241_e);

            if (this.field_48240_d == null)
            {
                return false;
            }
        }
        else
        {
            List var1 = this.field_48244_a.worldObj.getEntitiesWithinAABB(this.field_48245_h, this.field_48244_a.boundingBox.expand((double)this.field_48241_e, 3.0D, (double)this.field_48241_e));

            if (var1.size() == 0)
            {
                return false;
            }

            this.field_48240_d = (Entity)var1.get(0);
        }

        if (!this.field_48244_a.func_48090_aM().canSee(this.field_48240_d))
        {
            return false;
        }
        else
        {
            Vec3D var2 = RandomPositionGenerator.func_48623_b(this.field_48244_a, 16, 7, Vec3D.createVector(this.field_48240_d.posX, this.field_48240_d.posY, this.field_48240_d.posZ));

            if (var2 == null)
            {
                return false;
            }
            else if (this.field_48240_d.getDistanceSq(var2.xCoord, var2.yCoord, var2.zCoord) < this.field_48240_d.getDistanceSqToEntity(this.field_48244_a))
            {
                return false;
            }
            else
            {
                this.field_48238_f = this.field_48239_g.func_48671_a(var2.xCoord, var2.yCoord, var2.zCoord);
                return this.field_48238_f == null ? false : this.field_48238_f.func_48639_a(var2);
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48239_g.noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_48239_g.func_48678_a(this.field_48238_f, this.field_48242_b);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_48240_d = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (this.field_48244_a.getDistanceSqToEntity(this.field_48240_d) < 49.0D)
        {
            this.field_48244_a.getNavigator().func_48660_a(this.field_48243_c);
        }
        else
        {
            this.field_48244_a.getNavigator().func_48660_a(this.field_48242_b);
        }
    }
}
