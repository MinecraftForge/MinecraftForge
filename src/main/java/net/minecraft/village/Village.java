package net.minecraft.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Village
{
    private World worldObj;
    // JAVADOC FIELD $$ field_75584_b
    private final List villageDoorInfoList = new ArrayList();
    // JAVADOC FIELD $$ field_75585_c
    private final ChunkCoordinates centerHelper = new ChunkCoordinates(0, 0, 0);
    // JAVADOC FIELD $$ field_75582_d
    private final ChunkCoordinates center = new ChunkCoordinates(0, 0, 0);
    private int villageRadius;
    private int lastAddDoorTimestamp;
    private int tickCounter;
    private int numVillagers;
    // JAVADOC FIELD $$ field_82694_i
    private int noBreedTicks;
    // JAVADOC FIELD $$ field_82693_j
    private TreeMap playerReputation = new TreeMap();
    private List villageAgressors = new ArrayList();
    private int numIronGolems;
    private static final String __OBFID = "CL_00001631";

    public Village() {}

    public Village(World par1World)
    {
        this.worldObj = par1World;
    }

    public void func_82691_a(World par1World)
    {
        this.worldObj = par1World;
    }

    // JAVADOC METHOD $$ func_75560_a
    public void tick(int par1)
    {
        this.tickCounter = par1;
        this.removeDeadAndOutOfRangeDoors();
        this.removeDeadAndOldAgressors();

        if (par1 % 20 == 0)
        {
            this.updateNumVillagers();
        }

        if (par1 % 30 == 0)
        {
            this.updateNumIronGolems();
        }

        int j = this.numVillagers / 10;

        if (this.numIronGolems < j && this.villageDoorInfoList.size() > 20 && this.worldObj.rand.nextInt(7000) == 0)
        {
            Vec3 vec3 = this.tryGetIronGolemSpawningLocation(MathHelper.floor_float((float)this.center.posX), MathHelper.floor_float((float)this.center.posY), MathHelper.floor_float((float)this.center.posZ), 2, 4, 2);

            if (vec3 != null)
            {
                EntityIronGolem entityirongolem = new EntityIronGolem(this.worldObj);
                entityirongolem.setPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord);
                this.worldObj.spawnEntityInWorld(entityirongolem);
                ++this.numIronGolems;
            }
        }
    }

    // JAVADOC METHOD $$ func_75559_a
    private Vec3 tryGetIronGolemSpawningLocation(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        for (int k1 = 0; k1 < 10; ++k1)
        {
            int l1 = par1 + this.worldObj.rand.nextInt(16) - 8;
            int i2 = par2 + this.worldObj.rand.nextInt(6) - 3;
            int j2 = par3 + this.worldObj.rand.nextInt(16) - 8;

            if (this.isInRange(l1, i2, j2) && this.isValidIronGolemSpawningLocation(l1, i2, j2, par4, par5, par6))
            {
                return this.worldObj.getWorldVec3Pool().getVecFromPool((double)l1, (double)i2, (double)j2);
            }
        }

        return null;
    }

    private boolean isValidIronGolemSpawningLocation(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        if (!World.func_147466_a(this.worldObj, par1, par2 - 1, par3))
        {
            return false;
        }
        else
        {
            int k1 = par1 - par4 / 2;
            int l1 = par3 - par6 / 2;

            for (int i2 = k1; i2 < k1 + par4; ++i2)
            {
                for (int j2 = par2; j2 < par2 + par5; ++j2)
                {
                    for (int k2 = l1; k2 < l1 + par6; ++k2)
                    {
                        if (this.worldObj.func_147439_a(i2, j2, k2).func_149721_r())
                        {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    private void updateNumIronGolems()
    {
        List list = this.worldObj.getEntitiesWithinAABB(EntityIronGolem.class, AxisAlignedBB.getAABBPool().getAABB((double)(this.center.posX - this.villageRadius), (double)(this.center.posY - 4), (double)(this.center.posZ - this.villageRadius), (double)(this.center.posX + this.villageRadius), (double)(this.center.posY + 4), (double)(this.center.posZ + this.villageRadius)));
        this.numIronGolems = list.size();
    }

    private void updateNumVillagers()
    {
        List list = this.worldObj.getEntitiesWithinAABB(EntityVillager.class, AxisAlignedBB.getAABBPool().getAABB((double)(this.center.posX - this.villageRadius), (double)(this.center.posY - 4), (double)(this.center.posZ - this.villageRadius), (double)(this.center.posX + this.villageRadius), (double)(this.center.posY + 4), (double)(this.center.posZ + this.villageRadius)));
        this.numVillagers = list.size();

        if (this.numVillagers == 0)
        {
            this.playerReputation.clear();
        }
    }

    public ChunkCoordinates getCenter()
    {
        return this.center;
    }

    public int getVillageRadius()
    {
        return this.villageRadius;
    }

    // JAVADOC METHOD $$ func_75567_c
    public int getNumVillageDoors()
    {
        return this.villageDoorInfoList.size();
    }

    public int getTicksSinceLastDoorAdding()
    {
        return this.tickCounter - this.lastAddDoorTimestamp;
    }

    public int getNumVillagers()
    {
        return this.numVillagers;
    }

    // JAVADOC METHOD $$ func_75570_a
    public boolean isInRange(int par1, int par2, int par3)
    {
        return this.center.getDistanceSquared(par1, par2, par3) < (float)(this.villageRadius * this.villageRadius);
    }

    // JAVADOC METHOD $$ func_75558_f
    public List getVillageDoorInfoList()
    {
        return this.villageDoorInfoList;
    }

    public VillageDoorInfo findNearestDoor(int par1, int par2, int par3)
    {
        VillageDoorInfo villagedoorinfo = null;
        int l = Integer.MAX_VALUE;
        Iterator iterator = this.villageDoorInfoList.iterator();

        while (iterator.hasNext())
        {
            VillageDoorInfo villagedoorinfo1 = (VillageDoorInfo)iterator.next();
            int i1 = villagedoorinfo1.getDistanceSquared(par1, par2, par3);

            if (i1 < l)
            {
                villagedoorinfo = villagedoorinfo1;
                l = i1;
            }
        }

        return villagedoorinfo;
    }

    // JAVADOC METHOD $$ func_75569_c
    public VillageDoorInfo findNearestDoorUnrestricted(int par1, int par2, int par3)
    {
        VillageDoorInfo villagedoorinfo = null;
        int l = Integer.MAX_VALUE;
        Iterator iterator = this.villageDoorInfoList.iterator();

        while (iterator.hasNext())
        {
            VillageDoorInfo villagedoorinfo1 = (VillageDoorInfo)iterator.next();
            int i1 = villagedoorinfo1.getDistanceSquared(par1, par2, par3);

            if (i1 > 256)
            {
                i1 *= 1000;
            }
            else
            {
                i1 = villagedoorinfo1.getDoorOpeningRestrictionCounter();
            }

            if (i1 < l)
            {
                villagedoorinfo = villagedoorinfo1;
                l = i1;
            }
        }

        return villagedoorinfo;
    }

    public VillageDoorInfo getVillageDoorAt(int par1, int par2, int par3)
    {
        if (this.center.getDistanceSquared(par1, par2, par3) > (float)(this.villageRadius * this.villageRadius))
        {
            return null;
        }
        else
        {
            Iterator iterator = this.villageDoorInfoList.iterator();
            VillageDoorInfo villagedoorinfo;

            do
            {
                if (!iterator.hasNext())
                {
                    return null;
                }

                villagedoorinfo = (VillageDoorInfo)iterator.next();
            }
            while (villagedoorinfo.posX != par1 || villagedoorinfo.posZ != par3 || Math.abs(villagedoorinfo.posY - par2) > 1);

            return villagedoorinfo;
        }
    }

    public void addVillageDoorInfo(VillageDoorInfo par1VillageDoorInfo)
    {
        this.villageDoorInfoList.add(par1VillageDoorInfo);
        this.centerHelper.posX += par1VillageDoorInfo.posX;
        this.centerHelper.posY += par1VillageDoorInfo.posY;
        this.centerHelper.posZ += par1VillageDoorInfo.posZ;
        this.updateVillageRadiusAndCenter();
        this.lastAddDoorTimestamp = par1VillageDoorInfo.lastActivityTimestamp;
    }

    // JAVADOC METHOD $$ func_75566_g
    public boolean isAnnihilated()
    {
        return this.villageDoorInfoList.isEmpty();
    }

    public void addOrRenewAgressor(EntityLivingBase par1EntityLivingBase)
    {
        Iterator iterator = this.villageAgressors.iterator();
        Village.VillageAgressor villageagressor;

        do
        {
            if (!iterator.hasNext())
            {
                this.villageAgressors.add(new Village.VillageAgressor(par1EntityLivingBase, this.tickCounter));
                return;
            }

            villageagressor = (Village.VillageAgressor)iterator.next();
        }
        while (villageagressor.agressor != par1EntityLivingBase);

        villageagressor.agressionTime = this.tickCounter;
    }

    public EntityLivingBase findNearestVillageAggressor(EntityLivingBase par1EntityLivingBase)
    {
        double d0 = Double.MAX_VALUE;
        Village.VillageAgressor villageagressor = null;

        for (int i = 0; i < this.villageAgressors.size(); ++i)
        {
            Village.VillageAgressor villageagressor1 = (Village.VillageAgressor)this.villageAgressors.get(i);
            double d1 = villageagressor1.agressor.getDistanceSqToEntity(par1EntityLivingBase);

            if (d1 <= d0)
            {
                villageagressor = villageagressor1;
                d0 = d1;
            }
        }

        return villageagressor != null ? villageagressor.agressor : null;
    }

    public EntityPlayer func_82685_c(EntityLivingBase par1EntityLivingBase)
    {
        double d0 = Double.MAX_VALUE;
        EntityPlayer entityplayer = null;
        Iterator iterator = this.playerReputation.keySet().iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();

            if (this.isPlayerReputationTooLow(s))
            {
                EntityPlayer entityplayer1 = this.worldObj.getPlayerEntityByName(s);

                if (entityplayer1 != null)
                {
                    double d1 = entityplayer1.getDistanceSqToEntity(par1EntityLivingBase);

                    if (d1 <= d0)
                    {
                        entityplayer = entityplayer1;
                        d0 = d1;
                    }
                }
            }
        }

        return entityplayer;
    }

    private void removeDeadAndOldAgressors()
    {
        Iterator iterator = this.villageAgressors.iterator();

        while (iterator.hasNext())
        {
            Village.VillageAgressor villageagressor = (Village.VillageAgressor)iterator.next();

            if (!villageagressor.agressor.isEntityAlive() || Math.abs(this.tickCounter - villageagressor.agressionTime) > 300)
            {
                iterator.remove();
            }
        }
    }

    private void removeDeadAndOutOfRangeDoors()
    {
        boolean flag = false;
        boolean flag1 = this.worldObj.rand.nextInt(50) == 0;
        Iterator iterator = this.villageDoorInfoList.iterator();

        while (iterator.hasNext())
        {
            VillageDoorInfo villagedoorinfo = (VillageDoorInfo)iterator.next();

            if (flag1)
            {
                villagedoorinfo.resetDoorOpeningRestrictionCounter();
            }

            if (!this.isBlockDoor(villagedoorinfo.posX, villagedoorinfo.posY, villagedoorinfo.posZ) || Math.abs(this.tickCounter - villagedoorinfo.lastActivityTimestamp) > 1200)
            {
                this.centerHelper.posX -= villagedoorinfo.posX;
                this.centerHelper.posY -= villagedoorinfo.posY;
                this.centerHelper.posZ -= villagedoorinfo.posZ;
                flag = true;
                villagedoorinfo.isDetachedFromVillageFlag = true;
                iterator.remove();
            }
        }

        if (flag)
        {
            this.updateVillageRadiusAndCenter();
        }
    }

    private boolean isBlockDoor(int par1, int par2, int par3)
    {
        return this.worldObj.func_147439_a(par1, par2, par3) == Blocks.wooden_door;
    }

    private void updateVillageRadiusAndCenter()
    {
        int i = this.villageDoorInfoList.size();

        if (i == 0)
        {
            this.center.set(0, 0, 0);
            this.villageRadius = 0;
        }
        else
        {
            this.center.set(this.centerHelper.posX / i, this.centerHelper.posY / i, this.centerHelper.posZ / i);
            int j = 0;
            VillageDoorInfo villagedoorinfo;

            for (Iterator iterator = this.villageDoorInfoList.iterator(); iterator.hasNext(); j = Math.max(villagedoorinfo.getDistanceSquared(this.center.posX, this.center.posY, this.center.posZ), j))
            {
                villagedoorinfo = (VillageDoorInfo)iterator.next();
            }

            this.villageRadius = Math.max(32, (int)Math.sqrt((double)j) + 1);
        }
    }

    // JAVADOC METHOD $$ func_82684_a
    public int getReputationForPlayer(String par1Str)
    {
        Integer integer = (Integer)this.playerReputation.get(par1Str);
        return integer != null ? integer.intValue() : 0;
    }

    // JAVADOC METHOD $$ func_82688_a
    public int setReputationForPlayer(String par1Str, int par2)
    {
        int j = this.getReputationForPlayer(par1Str);
        int k = MathHelper.clamp_int(j + par2, -30, 10);
        this.playerReputation.put(par1Str, Integer.valueOf(k));
        return k;
    }

    // JAVADOC METHOD $$ func_82687_d
    public boolean isPlayerReputationTooLow(String par1Str)
    {
        return this.getReputationForPlayer(par1Str) <= -15;
    }

    // JAVADOC METHOD $$ func_82690_a
    public void readVillageDataFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.numVillagers = par1NBTTagCompound.getInteger("PopSize");
        this.villageRadius = par1NBTTagCompound.getInteger("Radius");
        this.numIronGolems = par1NBTTagCompound.getInteger("Golems");
        this.lastAddDoorTimestamp = par1NBTTagCompound.getInteger("Stable");
        this.tickCounter = par1NBTTagCompound.getInteger("Tick");
        this.noBreedTicks = par1NBTTagCompound.getInteger("MTick");
        this.center.posX = par1NBTTagCompound.getInteger("CX");
        this.center.posY = par1NBTTagCompound.getInteger("CY");
        this.center.posZ = par1NBTTagCompound.getInteger("CZ");
        this.centerHelper.posX = par1NBTTagCompound.getInteger("ACX");
        this.centerHelper.posY = par1NBTTagCompound.getInteger("ACY");
        this.centerHelper.posZ = par1NBTTagCompound.getInteger("ACZ");
        NBTTagList nbttaglist = par1NBTTagCompound.func_150295_c("Doors", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
            VillageDoorInfo villagedoorinfo = new VillageDoorInfo(nbttagcompound1.getInteger("X"), nbttagcompound1.getInteger("Y"), nbttagcompound1.getInteger("Z"), nbttagcompound1.getInteger("IDX"), nbttagcompound1.getInteger("IDZ"), nbttagcompound1.getInteger("TS"));
            this.villageDoorInfoList.add(villagedoorinfo);
        }

        NBTTagList nbttaglist1 = par1NBTTagCompound.func_150295_c("Players", 10);

        for (int j = 0; j < nbttaglist1.tagCount(); ++j)
        {
            NBTTagCompound nbttagcompound2 = nbttaglist1.func_150305_b(j);
            this.playerReputation.put(nbttagcompound2.getString("Name"), Integer.valueOf(nbttagcompound2.getInteger("S")));
        }
    }

    // JAVADOC METHOD $$ func_82689_b
    public void writeVillageDataToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("PopSize", this.numVillagers);
        par1NBTTagCompound.setInteger("Radius", this.villageRadius);
        par1NBTTagCompound.setInteger("Golems", this.numIronGolems);
        par1NBTTagCompound.setInteger("Stable", this.lastAddDoorTimestamp);
        par1NBTTagCompound.setInteger("Tick", this.tickCounter);
        par1NBTTagCompound.setInteger("MTick", this.noBreedTicks);
        par1NBTTagCompound.setInteger("CX", this.center.posX);
        par1NBTTagCompound.setInteger("CY", this.center.posY);
        par1NBTTagCompound.setInteger("CZ", this.center.posZ);
        par1NBTTagCompound.setInteger("ACX", this.centerHelper.posX);
        par1NBTTagCompound.setInteger("ACY", this.centerHelper.posY);
        par1NBTTagCompound.setInteger("ACZ", this.centerHelper.posZ);
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.villageDoorInfoList.iterator();

        while (iterator.hasNext())
        {
            VillageDoorInfo villagedoorinfo = (VillageDoorInfo)iterator.next();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setInteger("X", villagedoorinfo.posX);
            nbttagcompound1.setInteger("Y", villagedoorinfo.posY);
            nbttagcompound1.setInteger("Z", villagedoorinfo.posZ);
            nbttagcompound1.setInteger("IDX", villagedoorinfo.insideDirectionX);
            nbttagcompound1.setInteger("IDZ", villagedoorinfo.insideDirectionZ);
            nbttagcompound1.setInteger("TS", villagedoorinfo.lastActivityTimestamp);
            nbttaglist.appendTag(nbttagcompound1);
        }

        par1NBTTagCompound.setTag("Doors", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator1 = this.playerReputation.keySet().iterator();

        while (iterator1.hasNext())
        {
            String s = (String)iterator1.next();
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            nbttagcompound2.setString("Name", s);
            nbttagcompound2.setInteger("S", ((Integer)this.playerReputation.get(s)).intValue());
            nbttaglist1.appendTag(nbttagcompound2);
        }

        par1NBTTagCompound.setTag("Players", nbttaglist1);
    }

    // JAVADOC METHOD $$ func_82692_h
    public void endMatingSeason()
    {
        this.noBreedTicks = this.tickCounter;
    }

    // JAVADOC METHOD $$ func_82686_i
    public boolean isMatingSeason()
    {
        return this.noBreedTicks == 0 || this.tickCounter - this.noBreedTicks >= 3600;
    }

    public void func_82683_b(int par1)
    {
        Iterator iterator = this.playerReputation.keySet().iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            this.setReputationForPlayer(s, par1);
        }
    }

    class VillageAgressor
    {
        public EntityLivingBase agressor;
        public int agressionTime;
        private static final String __OBFID = "CL_00001632";

        VillageAgressor(EntityLivingBase par2EntityLivingBase, int par3)
        {
            this.agressor = par2EntityLivingBase;
            this.agressionTime = par3;
        }
    }
}