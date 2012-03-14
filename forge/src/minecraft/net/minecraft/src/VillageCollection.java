package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VillageCollection
{
    private World worldObj;

    /**
     * This is a black hole. You can add data to this list through a public interface, but you can't query that
     * information in any way and it's not used internally either.
     */
    private final List villagerPositionsList = new ArrayList();
    private final List newDoors = new ArrayList();
    private final List villageList = new ArrayList();
    private int tickCounter = 0;

    public VillageCollection(World par1World)
    {
        this.worldObj = par1World;
    }

    /**
     * This is a black hole. You can add data to this list through a public interface, but you can't query that
     * information in any way and it's not used internally either.
     */
    public void addVillagerPosition(int par1, int par2, int par3)
    {
        if (this.villagerPositionsList.size() <= 64)
        {
            if (!this.isVillagerPositionPresent(par1, par2, par3))
            {
                this.villagerPositionsList.add(new ChunkCoordinates(par1, par2, par3));
            }
        }
    }

    /**
     * Runs a single tick for the village collection
     */
    public void tick()
    {
        ++this.tickCounter;
        Iterator var1 = this.villageList.iterator();

        while (var1.hasNext())
        {
            Village var2 = (Village)var1.next();
            var2.tick(this.tickCounter);
        }

        this.removeAnnihilatedVillages();
        this.dropOldestVillagerPosition();
        this.addNewDoorsToVillageOrCreateVillage();
    }

    private void removeAnnihilatedVillages()
    {
        Iterator var1 = this.villageList.iterator();

        while (var1.hasNext())
        {
            Village var2 = (Village)var1.next();

            if (var2.isAnnihilated())
            {
                var1.remove();
            }
        }
    }

    public List func_48554_b()
    {
        return this.villageList;
    }

    /**
     * Finds the nearest village, but only the given coordinates are withing it's bounding box plus the given the
     * distance.
     */
    public Village findNearestVillage(int par1, int par2, int par3, int par4)
    {
        Village var5 = null;
        float var6 = Float.MAX_VALUE;
        Iterator var7 = this.villageList.iterator();

        while (var7.hasNext())
        {
            Village var8 = (Village)var7.next();
            float var9 = var8.getCenter().getDistanceSquared(par1, par2, par3);

            if (var9 < var6)
            {
                int var10 = par4 + var8.getVillageRadius();

                if (var9 <= (float)(var10 * var10))
                {
                    var5 = var8;
                    var6 = var9;
                }
            }
        }

        return var5;
    }

    private void dropOldestVillagerPosition()
    {
        if (!this.villagerPositionsList.isEmpty())
        {
            this.addUnassignedWoodenDoorsAroundToNewDoorsList((ChunkCoordinates)this.villagerPositionsList.remove(0));
        }
    }

    private void addNewDoorsToVillageOrCreateVillage()
    {
        int var1 = 0;

        while (var1 < this.newDoors.size())
        {
            VillageDoorInfo var2 = (VillageDoorInfo)this.newDoors.get(var1);
            boolean var3 = false;
            Iterator var4 = this.villageList.iterator();

            while (true)
            {
                if (var4.hasNext())
                {
                    Village var5 = (Village)var4.next();
                    int var6 = (int)var5.getCenter().getEuclideanDistanceTo(var2.posX, var2.posY, var2.posZ);

                    if (var6 > 32 + var5.getVillageRadius())
                    {
                        continue;
                    }

                    var5.addVillageDoorInfo(var2);
                    var3 = true;
                }

                if (!var3)
                {
                    Village var7 = new Village(this.worldObj);
                    var7.addVillageDoorInfo(var2);
                    this.villageList.add(var7);
                }

                ++var1;
                break;
            }
        }

        this.newDoors.clear();
    }

    private void addUnassignedWoodenDoorsAroundToNewDoorsList(ChunkCoordinates par1ChunkCoordinates)
    {
        byte var2 = 16;
        byte var3 = 4;
        byte var4 = 16;

        for (int var5 = par1ChunkCoordinates.posX - var2; var5 < par1ChunkCoordinates.posX + var2; ++var5)
        {
            for (int var6 = par1ChunkCoordinates.posY - var3; var6 < par1ChunkCoordinates.posY + var3; ++var6)
            {
                for (int var7 = par1ChunkCoordinates.posZ - var4; var7 < par1ChunkCoordinates.posZ + var4; ++var7)
                {
                    if (this.isWoodenDoorAt(var5, var6, var7))
                    {
                        VillageDoorInfo var8 = this.getVillageDoorAt(var5, var6, var7);

                        if (var8 == null)
                        {
                            this.addDoorToNewListIfAppropriate(var5, var6, var7);
                        }
                        else
                        {
                            var8.lastActivityTimestamp = this.tickCounter;
                        }
                    }
                }
            }
        }
    }

    private VillageDoorInfo getVillageDoorAt(int par1, int par2, int par3)
    {
        Iterator var4 = this.newDoors.iterator();
        VillageDoorInfo var5;

        do
        {
            if (!var4.hasNext())
            {
                var4 = this.villageList.iterator();
                VillageDoorInfo var6;

                do
                {
                    if (!var4.hasNext())
                    {
                        return null;
                    }

                    Village var7 = (Village)var4.next();
                    var6 = var7.getVillageDoorAt(par1, par2, par3);
                }
                while (var6 == null);

                return var6;
            }

            var5 = (VillageDoorInfo)var4.next();
        }
        while (var5.posX != par1 || var5.posZ != par3 || Math.abs(var5.posY - par2) > 1);

        return var5;
    }

    private void addDoorToNewListIfAppropriate(int par1, int par2, int par3)
    {
        int var4 = ((BlockDoor)Block.doorWood).getDoorOrientation(this.worldObj, par1, par2, par3);
        int var5;
        int var6;

        if (var4 != 0 && var4 != 2)
        {
            var5 = 0;

            for (var6 = -5; var6 < 0; ++var6)
            {
                if (this.worldObj.canBlockSeeTheSky(par1, par2, par3 + var6))
                {
                    --var5;
                }
            }

            for (var6 = 1; var6 <= 5; ++var6)
            {
                if (this.worldObj.canBlockSeeTheSky(par1, par2, par3 + var6))
                {
                    ++var5;
                }
            }

            if (var5 != 0)
            {
                this.newDoors.add(new VillageDoorInfo(par1, par2, par3, 0, var5 > 0 ? -2 : 2, this.tickCounter));
            }
        }
        else
        {
            var5 = 0;

            for (var6 = -5; var6 < 0; ++var6)
            {
                if (this.worldObj.canBlockSeeTheSky(par1 + var6, par2, par3))
                {
                    --var5;
                }
            }

            for (var6 = 1; var6 <= 5; ++var6)
            {
                if (this.worldObj.canBlockSeeTheSky(par1 + var6, par2, par3))
                {
                    ++var5;
                }
            }

            if (var5 != 0)
            {
                this.newDoors.add(new VillageDoorInfo(par1, par2, par3, var5 > 0 ? -2 : 2, 0, this.tickCounter));
            }
        }
    }

    private boolean isVillagerPositionPresent(int par1, int par2, int par3)
    {
        Iterator var4 = this.villagerPositionsList.iterator();
        ChunkCoordinates var5;

        do
        {
            if (!var4.hasNext())
            {
                return false;
            }

            var5 = (ChunkCoordinates)var4.next();
        }
        while (var5.posX != par1 || var5.posY != par2 || var5.posZ != par3);

        return true;
    }

    private boolean isWoodenDoorAt(int par1, int par2, int par3)
    {
        int var4 = this.worldObj.getBlockId(par1, par2, par3);
        return var4 == Block.doorWood.blockID;
    }
}
