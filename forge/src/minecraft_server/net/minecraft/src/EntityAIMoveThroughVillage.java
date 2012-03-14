package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntityAIMoveThroughVillage extends EntityAIBase
{
    private EntityCreature field_48283_a;
    private float field_48281_b;
    private PathEntity field_48282_c;
    private VillageDoorInfo field_48279_d;
    private boolean field_48280_e;
    private List field_48278_f = new ArrayList();

    public EntityAIMoveThroughVillage(EntityCreature par1EntityCreature, float par2, boolean par3)
    {
        this.field_48283_a = par1EntityCreature;
        this.field_48281_b = par2;
        this.field_48280_e = par3;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        this.func_48277_f();

        if (this.field_48280_e && this.field_48283_a.worldObj.isDaytime())
        {
            return false;
        }
        else
        {
            Village var1 = this.field_48283_a.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.field_48283_a.posX), MathHelper.floor_double(this.field_48283_a.posY), MathHelper.floor_double(this.field_48283_a.posZ), 0);

            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.field_48279_d = this.func_48276_a(var1);

                if (this.field_48279_d == null)
                {
                    return false;
                }
                else
                {
                    boolean var2 = this.field_48283_a.getNavigator().func_48657_b();
                    this.field_48283_a.getNavigator().func_48663_b(false);
                    this.field_48282_c = this.field_48283_a.getNavigator().func_48650_a((double)this.field_48279_d.posX, (double)this.field_48279_d.posY, (double)this.field_48279_d.posZ);
                    this.field_48283_a.getNavigator().func_48663_b(var2);

                    if (this.field_48282_c != null)
                    {
                        return true;
                    }
                    else
                    {
                        Vec3D var3 = RandomPositionGenerator.func_48395_a(this.field_48283_a, 10, 7, Vec3D.createVector((double)this.field_48279_d.posX, (double)this.field_48279_d.posY, (double)this.field_48279_d.posZ));

                        if (var3 == null)
                        {
                            return false;
                        }
                        else
                        {
                            this.field_48283_a.getNavigator().func_48663_b(false);
                            this.field_48282_c = this.field_48283_a.getNavigator().func_48650_a(var3.xCoord, var3.yCoord, var3.zCoord);
                            this.field_48283_a.getNavigator().func_48663_b(var2);
                            return this.field_48282_c != null;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (this.field_48283_a.getNavigator().noPath())
        {
            return false;
        }
        else
        {
            float var1 = this.field_48283_a.width + 4.0F;
            return this.field_48283_a.getDistanceSq((double)this.field_48279_d.posX, (double)this.field_48279_d.posY, (double)this.field_48279_d.posZ) > (double)(var1 * var1);
        }
    }

    public void startExecuting()
    {
        this.field_48283_a.getNavigator().func_48647_a(this.field_48282_c, this.field_48281_b);
    }

    public void resetTask()
    {
        if (this.field_48283_a.getNavigator().noPath() || this.field_48283_a.getDistanceSq((double)this.field_48279_d.posX, (double)this.field_48279_d.posY, (double)this.field_48279_d.posZ) < 16.0D)
        {
            this.field_48278_f.add(this.field_48279_d);
        }
    }

    private VillageDoorInfo func_48276_a(Village par1Village)
    {
        VillageDoorInfo var2 = null;
        int var3 = Integer.MAX_VALUE;
        List var4 = par1Village.getVillageDoorInfoList();
        Iterator var5 = var4.iterator();

        while (var5.hasNext())
        {
            VillageDoorInfo var6 = (VillageDoorInfo)var5.next();
            int var7 = var6.getDistanceSquared(MathHelper.floor_double(this.field_48283_a.posX), MathHelper.floor_double(this.field_48283_a.posY), MathHelper.floor_double(this.field_48283_a.posZ));

            if (var7 < var3 && !this.func_48275_a(var6))
            {
                var2 = var6;
                var3 = var7;
            }
        }

        return var2;
    }

    private boolean func_48275_a(VillageDoorInfo par1VillageDoorInfo)
    {
        Iterator var2 = this.field_48278_f.iterator();
        VillageDoorInfo var3;

        do
        {
            if (!var2.hasNext())
            {
                return false;
            }

            var3 = (VillageDoorInfo)var2.next();
        }
        while (par1VillageDoorInfo.posX != var3.posX || par1VillageDoorInfo.posY != var3.posY || par1VillageDoorInfo.posZ != var3.posZ);

        return true;
    }

    private void func_48277_f()
    {
        if (this.field_48278_f.size() > 15)
        {
            this.field_48278_f.remove(0);
        }
    }
}
