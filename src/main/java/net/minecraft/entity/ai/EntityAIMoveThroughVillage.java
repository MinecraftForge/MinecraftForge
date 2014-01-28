package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveThroughVillage extends EntityAIBase
{
    private EntityCreature theEntity;
    private double movementSpeed;
    // JAVADOC FIELD $$ field_75419_c
    private PathEntity entityPathNavigate;
    private VillageDoorInfo doorInfo;
    private boolean isNocturnal;
    private List doorList = new ArrayList();
    private static final String __OBFID = "CL_00001597";

    public EntityAIMoveThroughVillage(EntityCreature par1EntityCreature, double par2, boolean par4)
    {
        this.theEntity = par1EntityCreature;
        this.movementSpeed = par2;
        this.isNocturnal = par4;
        this.setMutexBits(1);
    }

    // JAVADOC METHOD $$ func_75250_a
    public boolean shouldExecute()
    {
        this.func_75414_f();

        if (this.isNocturnal && this.theEntity.worldObj.isDaytime())
        {
            return false;
        }
        else
        {
            Village village = this.theEntity.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.theEntity.posX), MathHelper.floor_double(this.theEntity.posY), MathHelper.floor_double(this.theEntity.posZ), 0);

            if (village == null)
            {
                return false;
            }
            else
            {
                this.doorInfo = this.func_75412_a(village);

                if (this.doorInfo == null)
                {
                    return false;
                }
                else
                {
                    boolean flag = this.theEntity.getNavigator().getCanBreakDoors();
                    this.theEntity.getNavigator().setBreakDoors(false);
                    this.entityPathNavigate = this.theEntity.getNavigator().getPathToXYZ((double)this.doorInfo.posX, (double)this.doorInfo.posY, (double)this.doorInfo.posZ);
                    this.theEntity.getNavigator().setBreakDoors(flag);

                    if (this.entityPathNavigate != null)
                    {
                        return true;
                    }
                    else
                    {
                        Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 10, 7, this.theEntity.worldObj.getWorldVec3Pool().getVecFromPool((double)this.doorInfo.posX, (double)this.doorInfo.posY, (double)this.doorInfo.posZ));

                        if (vec3 == null)
                        {
                            return false;
                        }
                        else
                        {
                            this.theEntity.getNavigator().setBreakDoors(false);
                            this.entityPathNavigate = this.theEntity.getNavigator().getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
                            this.theEntity.getNavigator().setBreakDoors(flag);
                            return this.entityPathNavigate != null;
                        }
                    }
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_75253_b
    public boolean continueExecuting()
    {
        if (this.theEntity.getNavigator().noPath())
        {
            return false;
        }
        else
        {
            float f = this.theEntity.width + 4.0F;
            return this.theEntity.getDistanceSq((double)this.doorInfo.posX, (double)this.doorInfo.posY, (double)this.doorInfo.posZ) > (double)(f * f);
        }
    }

    // JAVADOC METHOD $$ func_75249_e
    public void startExecuting()
    {
        this.theEntity.getNavigator().setPath(this.entityPathNavigate, this.movementSpeed);
    }

    // JAVADOC METHOD $$ func_75251_c
    public void resetTask()
    {
        if (this.theEntity.getNavigator().noPath() || this.theEntity.getDistanceSq((double)this.doorInfo.posX, (double)this.doorInfo.posY, (double)this.doorInfo.posZ) < 16.0D)
        {
            this.doorList.add(this.doorInfo);
        }
    }

    private VillageDoorInfo func_75412_a(Village par1Village)
    {
        VillageDoorInfo villagedoorinfo = null;
        int i = Integer.MAX_VALUE;
        List list = par1Village.getVillageDoorInfoList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext())
        {
            VillageDoorInfo villagedoorinfo1 = (VillageDoorInfo)iterator.next();
            int j = villagedoorinfo1.getDistanceSquared(MathHelper.floor_double(this.theEntity.posX), MathHelper.floor_double(this.theEntity.posY), MathHelper.floor_double(this.theEntity.posZ));

            if (j < i && !this.func_75413_a(villagedoorinfo1))
            {
                villagedoorinfo = villagedoorinfo1;
                i = j;
            }
        }

        return villagedoorinfo;
    }

    private boolean func_75413_a(VillageDoorInfo par1VillageDoorInfo)
    {
        Iterator iterator = this.doorList.iterator();
        VillageDoorInfo villagedoorinfo1;

        do
        {
            if (!iterator.hasNext())
            {
                return false;
            }

            villagedoorinfo1 = (VillageDoorInfo)iterator.next();
        }
        while (par1VillageDoorInfo.posX != villagedoorinfo1.posX || par1VillageDoorInfo.posY != villagedoorinfo1.posY || par1VillageDoorInfo.posZ != villagedoorinfo1.posZ);

        return true;
    }

    private void func_75414_f()
    {
        if (this.doorList.size() > 15)
        {
            this.doorList.remove(0);
        }
    }
}