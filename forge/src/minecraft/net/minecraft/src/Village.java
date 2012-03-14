package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Village
{
    private final World worldObj;

    /** list of VillageDoorInfo objects */
    private final List villageDoorInfoList = new ArrayList();

    /**
     * This is the sum of all door coordinates and used to calculate the actual village center by dividing by the number
     * of doors.
     */
    private final ChunkCoordinates centerHelper = new ChunkCoordinates(0, 0, 0);

    /** This is the actual village center. */
    private final ChunkCoordinates center = new ChunkCoordinates(0, 0, 0);
    private int villageRadius = 0;
    private int lastAddDoorTimestamp = 0;
    private int tickCounter = 0;
    private int numVillagers = 0;
    private List villageAgressors = new ArrayList();
    private int numIronGolems = 0;

    public Village(World par1World)
    {
        this.worldObj = par1World;
    }

    /**
     * Called periodically by VillageCollection
     */
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

        int var2 = this.numVillagers / 16;

        if (this.numIronGolems < var2 && this.villageDoorInfoList.size() > 20 && this.worldObj.rand.nextInt(7000) == 0)
        {
            Vec3D var3 = this.tryGetIronGolemSpawningLocation(MathHelper.floor_float((float)this.center.posX), MathHelper.floor_float((float)this.center.posY), MathHelper.floor_float((float)this.center.posZ), 2, 4, 2);

            if (var3 != null)
            {
                EntityIronGolem var4 = new EntityIronGolem(this.worldObj);
                var4.setPosition(var3.xCoord, var3.yCoord, var3.zCoord);
                this.worldObj.spawnEntityInWorld(var4);
                ++this.numIronGolems;
            }
        }
    }

    /**
     * Tries up to 10 times to get a valid spawning location before eventually failing and returning null.
     */
    private Vec3D tryGetIronGolemSpawningLocation(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        for (int var7 = 0; var7 < 10; ++var7)
        {
            int var8 = par1 + this.worldObj.rand.nextInt(16) - 8;
            int var9 = par2 + this.worldObj.rand.nextInt(6) - 3;
            int var10 = par3 + this.worldObj.rand.nextInt(16) - 8;

            if (this.isInRange(var8, var9, var10) && this.isValidIronGolemSpawningLocation(var8, var9, var10, par4, par5, par6))
            {
                return Vec3D.createVector((double)var8, (double)var9, (double)var10);
            }
        }

        return null;
    }

    private boolean isValidIronGolemSpawningLocation(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        if (!this.worldObj.isBlockNormalCube(par1, par2 - 1, par3))
        {
            return false;
        }
        else
        {
            int var7 = par1 - par4 / 2;
            int var8 = par3 - par6 / 2;

            for (int var9 = var7; var9 < var7 + par4; ++var9)
            {
                for (int var10 = par2; var10 < par2 + par5; ++var10)
                {
                    for (int var11 = var8; var11 < var8 + par6; ++var11)
                    {
                        if (this.worldObj.isBlockNormalCube(var9, var10, var11))
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
        List var1 = this.worldObj.getEntitiesWithinAABB(EntityIronGolem.class, AxisAlignedBB.getBoundingBoxFromPool((double)(this.center.posX - this.villageRadius), (double)(this.center.posY - 4), (double)(this.center.posZ - this.villageRadius), (double)(this.center.posX + this.villageRadius), (double)(this.center.posY + 4), (double)(this.center.posZ + this.villageRadius)));
        this.numIronGolems = var1.size();
    }

    private void updateNumVillagers()
    {
        List var1 = this.worldObj.getEntitiesWithinAABB(EntityVillager.class, AxisAlignedBB.getBoundingBoxFromPool((double)(this.center.posX - this.villageRadius), (double)(this.center.posY - 4), (double)(this.center.posZ - this.villageRadius), (double)(this.center.posX + this.villageRadius), (double)(this.center.posY + 4), (double)(this.center.posZ + this.villageRadius)));
        this.numVillagers = var1.size();
    }

    public ChunkCoordinates getCenter()
    {
        return this.center;
    }

    public int getVillageRadius()
    {
        return this.villageRadius;
    }

    /**
     * Actually get num village door info entries, but that boils down to number of doors. Called by
     * EntityAIVillagerMate and VillageSiege
     */
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

    /**
     * Returns true, if the given coordinates are within the bounding box of the village.
     */
    public boolean isInRange(int par1, int par2, int par3)
    {
        return this.center.getDistanceSquared(par1, par2, par3) < (float)(this.villageRadius * this.villageRadius);
    }

    /**
     * called only by class EntityAIMoveThroughVillage
     */
    public List getVillageDoorInfoList()
    {
        return this.villageDoorInfoList;
    }

    public VillageDoorInfo findNearestDoor(int par1, int par2, int par3)
    {
        VillageDoorInfo var4 = null;
        int var5 = Integer.MAX_VALUE;
        Iterator var6 = this.villageDoorInfoList.iterator();

        while (var6.hasNext())
        {
            VillageDoorInfo var7 = (VillageDoorInfo)var6.next();
            int var8 = var7.getDistanceSquared(par1, par2, par3);

            if (var8 < var5)
            {
                var4 = var7;
                var5 = var8;
            }
        }

        return var4;
    }

    /**
     * Find a door suitable for shelter. If there are more doors in a distance of 16 blocks, then the least restricted
     * one (i.e. the one protecting the lowest number of villagers) of them is chosen, else the nearest one regardless
     * of restriction.
     */
    public VillageDoorInfo findNearestDoorUnrestricted(int par1, int par2, int par3)
    {
        VillageDoorInfo var4 = null;
        int var5 = Integer.MAX_VALUE;
        Iterator var6 = this.villageDoorInfoList.iterator();

        while (var6.hasNext())
        {
            VillageDoorInfo var7 = (VillageDoorInfo)var6.next();
            int var8 = var7.getDistanceSquared(par1, par2, par3);

            if (var8 > 256)
            {
                var8 *= 1000;
            }
            else
            {
                var8 = var7.getDoorOpeningRestrictionCounter();
            }

            if (var8 < var5)
            {
                var4 = var7;
                var5 = var8;
            }
        }

        return var4;
    }

    public VillageDoorInfo getVillageDoorAt(int par1, int par2, int par3)
    {
        if (this.center.getDistanceSquared(par1, par2, par3) > (float)(this.villageRadius * this.villageRadius))
        {
            return null;
        }
        else
        {
            Iterator var4 = this.villageDoorInfoList.iterator();
            VillageDoorInfo var5;

            do
            {
                if (!var4.hasNext())
                {
                    return null;
                }

                var5 = (VillageDoorInfo)var4.next();
            }
            while (var5.posX != par1 || var5.posZ != par3 || Math.abs(var5.posY - par2) > 1);

            return var5;
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

    /**
     * Returns true, if there is not a single village door left. Called by VillageCollection
     */
    public boolean isAnnihilated()
    {
        return this.villageDoorInfoList.isEmpty();
    }

    public void addOrRenewAgressor(EntityLiving par1EntityLiving)
    {
        Iterator var2 = this.villageAgressors.iterator();
        VillageAgressor var3;

        do
        {
            if (!var2.hasNext())
            {
                this.villageAgressors.add(new VillageAgressor(this, par1EntityLiving, this.tickCounter));
                return;
            }

            var3 = (VillageAgressor)var2.next();
        }
        while (var3.agressor != par1EntityLiving);

        var3.agressionTime = this.tickCounter;
    }

    public EntityLiving findNearestVillageAggressor(EntityLiving par1EntityLiving)
    {
        double var2 = Double.MAX_VALUE;
        VillageAgressor var4 = null;

        for (int var5 = 0; var5 < this.villageAgressors.size(); ++var5)
        {
            VillageAgressor var6 = (VillageAgressor)this.villageAgressors.get(var5);
            double var7 = var6.agressor.getDistanceSqToEntity(par1EntityLiving);

            if (var7 <= var2)
            {
                var4 = var6;
                var2 = var7;
            }
        }

        return var4 != null ? var4.agressor : null;
    }

    private void removeDeadAndOldAgressors()
    {
        Iterator var1 = this.villageAgressors.iterator();

        while (var1.hasNext())
        {
            VillageAgressor var2 = (VillageAgressor)var1.next();

            if (!var2.agressor.isEntityAlive() || Math.abs(this.tickCounter - var2.agressionTime) > 300)
            {
                var1.remove();
            }
        }
    }

    private void removeDeadAndOutOfRangeDoors()
    {
        boolean var1 = false;
        boolean var2 = this.worldObj.rand.nextInt(50) == 0;
        Iterator var3 = this.villageDoorInfoList.iterator();

        while (var3.hasNext())
        {
            VillageDoorInfo var4 = (VillageDoorInfo)var3.next();

            if (var2)
            {
                var4.resetDoorOpeningRestrictionCounter();
            }

            if (!this.isBlockDoor(var4.posX, var4.posY, var4.posZ) || Math.abs(this.tickCounter - var4.lastActivityTimestamp) > 1200)
            {
                this.centerHelper.posX -= var4.posX;
                this.centerHelper.posY -= var4.posY;
                this.centerHelper.posZ -= var4.posZ;
                var1 = true;
                var4.isDetachedFromVillageFlag = true;
                var3.remove();
            }
        }

        if (var1)
        {
            this.updateVillageRadiusAndCenter();
        }
    }

    private boolean isBlockDoor(int par1, int par2, int par3)
    {
        int var4 = this.worldObj.getBlockId(par1, par2, par3);
        return var4 <= 0 ? false : var4 == Block.doorWood.blockID;
    }

    private void updateVillageRadiusAndCenter()
    {
        int var1 = this.villageDoorInfoList.size();

        if (var1 == 0)
        {
            this.center.setChunkCoordinates(0, 0, 0);
            this.villageRadius = 0;
        }
        else
        {
            this.center.setChunkCoordinates(this.centerHelper.posX / var1, this.centerHelper.posY / var1, this.centerHelper.posZ / var1);
            int var2 = 0;
            VillageDoorInfo var4;

            for (Iterator var3 = this.villageDoorInfoList.iterator(); var3.hasNext(); var2 = Math.max(var4.getDistanceSquared(this.center.posX, this.center.posY, this.center.posZ), var2))
            {
                var4 = (VillageDoorInfo)var3.next();
            }

            this.villageRadius = Math.max(32, (int)Math.sqrt((double)var2));
        }
    }
}
