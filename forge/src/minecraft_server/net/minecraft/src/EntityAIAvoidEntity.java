package net.minecraft.src;

import java.util.List;

public class EntityAIAvoidEntity extends EntityAIBase
{
    private EntityCreature field_48237_a;
    private float field_48235_b;
    private float field_48236_c;
    private Entity field_48233_d;
    private float field_48234_e;
    private PathEntity field_48231_f;
    private PathNavigate field_48232_g;
    private Class field_48238_h;

    public EntityAIAvoidEntity(EntityCreature par1EntityCreature, Class par2Class, float par3, float par4, float par5)
    {
        this.field_48237_a = par1EntityCreature;
        this.field_48238_h = par2Class;
        this.field_48234_e = par3;
        this.field_48235_b = par4;
        this.field_48236_c = par5;
        this.field_48232_g = par1EntityCreature.getNavigator();
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.field_48238_h == EntityPlayer.class)
        {
            if (this.field_48237_a instanceof EntityTameable && ((EntityTameable)this.field_48237_a).isTamed())
            {
                return false;
            }

            this.field_48233_d = this.field_48237_a.worldObj.getClosestPlayerToEntity(this.field_48237_a, (double)this.field_48234_e);

            if (this.field_48233_d == null)
            {
                return false;
            }
        }
        else
        {
            List var1 = this.field_48237_a.worldObj.getEntitiesWithinAABB(this.field_48238_h, this.field_48237_a.boundingBox.expand((double)this.field_48234_e, 3.0D, (double)this.field_48234_e));

            if (var1.size() == 0)
            {
                return false;
            }

            this.field_48233_d = (Entity)var1.get(0);
        }

        if (!this.field_48237_a.func_48318_al().func_48546_a(this.field_48233_d))
        {
            return false;
        }
        else
        {
            Vec3D var2 = RandomPositionGenerator.func_48394_b(this.field_48237_a, 16, 7, Vec3D.createVector(this.field_48233_d.posX, this.field_48233_d.posY, this.field_48233_d.posZ));

            if (var2 == null)
            {
                return false;
            }
            else if (this.field_48233_d.getDistanceSq(var2.xCoord, var2.yCoord, var2.zCoord) < this.field_48233_d.getDistanceSqToEntity(this.field_48237_a))
            {
                return false;
            }
            else
            {
                this.field_48231_f = this.field_48232_g.func_48650_a(var2.xCoord, var2.yCoord, var2.zCoord);
                return this.field_48231_f == null ? false : this.field_48231_f.func_48426_a(var2);
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.field_48232_g.noPath();
    }

    public void startExecuting()
    {
        this.field_48232_g.func_48647_a(this.field_48231_f, this.field_48235_b);
    }

    public void resetTask()
    {
        this.field_48233_d = null;
    }

    public void updateTask()
    {
        if (this.field_48237_a.getDistanceSqToEntity(this.field_48233_d) < 49.0D)
        {
            this.field_48237_a.getNavigator().func_48654_a(this.field_48236_c);
        }
        else
        {
            this.field_48237_a.getNavigator().func_48654_a(this.field_48235_b);
        }
    }
}
