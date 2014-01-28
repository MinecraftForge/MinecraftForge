package net.minecraft.village;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;

public class VillageSiege
{
    private World worldObj;
    private boolean field_75535_b;
    private int field_75536_c = -1;
    private int field_75533_d;
    private int field_75534_e;
    // JAVADOC FIELD $$ field_75531_f
    private Village theVillage;
    private int field_75532_g;
    private int field_75538_h;
    private int field_75539_i;
    private static final String __OBFID = "CL_00001634";

    public VillageSiege(World par1World)
    {
        this.worldObj = par1World;
    }

    // JAVADOC METHOD $$ func_75528_a
    public void tick()
    {
        boolean flag = false;

        if (flag)
        {
            if (this.field_75536_c == 2)
            {
                this.field_75533_d = 100;
                return;
            }
        }
        else
        {
            if (this.worldObj.isDaytime())
            {
                this.field_75536_c = 0;
                return;
            }

            if (this.field_75536_c == 2)
            {
                return;
            }

            if (this.field_75536_c == 0)
            {
                float f = this.worldObj.getCelestialAngle(0.0F);

                if ((double)f < 0.5D || (double)f > 0.501D)
                {
                    return;
                }

                this.field_75536_c = this.worldObj.rand.nextInt(10) == 0 ? 1 : 2;
                this.field_75535_b = false;

                if (this.field_75536_c == 2)
                {
                    return;
                }
            }
        }

        if (!this.field_75535_b)
        {
            if (!this.func_75529_b())
            {
                return;
            }

            this.field_75535_b = true;
        }

        if (this.field_75534_e > 0)
        {
            --this.field_75534_e;
        }
        else
        {
            this.field_75534_e = 2;

            if (this.field_75533_d > 0)
            {
                this.spawnZombie();
                --this.field_75533_d;
            }
            else
            {
                this.field_75536_c = 2;
            }
        }
    }

    private boolean func_75529_b()
    {
        List list = this.worldObj.playerEntities;
        Iterator iterator = list.iterator();

        while (iterator.hasNext())
        {
            EntityPlayer entityplayer = (EntityPlayer)iterator.next();
            this.theVillage = this.worldObj.villageCollectionObj.findNearestVillage((int)entityplayer.posX, (int)entityplayer.posY, (int)entityplayer.posZ, 1);

            if (this.theVillage != null && this.theVillage.getNumVillageDoors() >= 10 && this.theVillage.getTicksSinceLastDoorAdding() >= 20 && this.theVillage.getNumVillagers() >= 20)
            {
                ChunkCoordinates chunkcoordinates = this.theVillage.getCenter();
                float f = (float)this.theVillage.getVillageRadius();
                boolean flag = false;
                int i = 0;

                while (true)
                {
                    if (i < 10)
                    {
                        this.field_75532_g = chunkcoordinates.posX + (int)((double)(MathHelper.cos(this.worldObj.rand.nextFloat() * (float)Math.PI * 2.0F) * f) * 0.9D);
                        this.field_75538_h = chunkcoordinates.posY;
                        this.field_75539_i = chunkcoordinates.posZ + (int)((double)(MathHelper.sin(this.worldObj.rand.nextFloat() * (float)Math.PI * 2.0F) * f) * 0.9D);
                        flag = false;
                        Iterator iterator1 = this.worldObj.villageCollectionObj.getVillageList().iterator();

                        while (iterator1.hasNext())
                        {
                            Village village = (Village)iterator1.next();

                            if (village != this.theVillage && village.isInRange(this.field_75532_g, this.field_75538_h, this.field_75539_i))
                            {
                                flag = true;
                                break;
                            }
                        }

                        if (flag)
                        {
                            ++i;
                            continue;
                        }
                    }

                    if (flag)
                    {
                        return false;
                    }

                    Vec3 vec3 = this.func_75527_a(this.field_75532_g, this.field_75538_h, this.field_75539_i);

                    if (vec3 != null)
                    {
                        this.field_75534_e = 0;
                        this.field_75533_d = 20;
                        return true;
                    }

                    break;
                }
            }
        }

        return false;
    }

    private boolean spawnZombie()
    {
        Vec3 vec3 = this.func_75527_a(this.field_75532_g, this.field_75538_h, this.field_75539_i);

        if (vec3 == null)
        {
            return false;
        }
        else
        {
            EntityZombie entityzombie;

            try
            {
                entityzombie = new EntityZombie(this.worldObj);
                entityzombie.onSpawnWithEgg((IEntityLivingData)null);
                entityzombie.setVillager(false);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
                return false;
            }

            entityzombie.setLocationAndAngles(vec3.xCoord, vec3.yCoord, vec3.zCoord, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
            this.worldObj.spawnEntityInWorld(entityzombie);
            ChunkCoordinates chunkcoordinates = this.theVillage.getCenter();
            entityzombie.setHomeArea(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, this.theVillage.getVillageRadius());
            return true;
        }
    }

    private Vec3 func_75527_a(int par1, int par2, int par3)
    {
        for (int l = 0; l < 10; ++l)
        {
            int i1 = par1 + this.worldObj.rand.nextInt(16) - 8;
            int j1 = par2 + this.worldObj.rand.nextInt(6) - 3;
            int k1 = par3 + this.worldObj.rand.nextInt(16) - 8;

            if (this.theVillage.isInRange(i1, j1, k1) && SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, this.worldObj, i1, j1, k1))
            {
                this.worldObj.getWorldVec3Pool().getVecFromPool((double)i1, (double)j1, (double)k1);
            }
        }

        return null;
    }
}