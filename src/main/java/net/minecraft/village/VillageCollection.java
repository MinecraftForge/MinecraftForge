package net.minecraft.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockDoor;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class VillageCollection extends WorldSavedData
{
    private World worldObj;
    // JAVADOC FIELD $$ field_75554_b
    private final List villagerPositionsList = new ArrayList();
    private final List newDoors = new ArrayList();
    private final List villageList = new ArrayList();
    private int tickCounter;
    private static final String __OBFID = "CL_00001635";

    public VillageCollection(String par1Str)
    {
        super(par1Str);
    }

    public VillageCollection(World par1World)
    {
        super("villages");
        this.worldObj = par1World;
        this.markDirty();
    }

    public void func_82566_a(World par1World)
    {
        this.worldObj = par1World;
        Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            Village village = (Village)iterator.next();
            village.func_82691_a(par1World);
        }
    }

    // JAVADOC METHOD $$ func_75551_a
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

    // JAVADOC METHOD $$ func_75544_a
    public void tick()
    {
        ++this.tickCounter;
        Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            Village village = (Village)iterator.next();
            village.tick(this.tickCounter);
        }

        this.removeAnnihilatedVillages();
        this.dropOldestVillagerPosition();
        this.addNewDoorsToVillageOrCreateVillage();

        if (this.tickCounter % 400 == 0)
        {
            this.markDirty();
        }
    }

    private void removeAnnihilatedVillages()
    {
        Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            Village village = (Village)iterator.next();

            if (village.isAnnihilated())
            {
                iterator.remove();
                this.markDirty();
            }
        }
    }

    // JAVADOC METHOD $$ func_75540_b
    public List getVillageList()
    {
        return this.villageList;
    }

    // JAVADOC METHOD $$ func_75550_a
    public Village findNearestVillage(int par1, int par2, int par3, int par4)
    {
        Village village = null;
        float f = Float.MAX_VALUE;
        Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            Village village1 = (Village)iterator.next();
            float f1 = village1.getCenter().getDistanceSquared(par1, par2, par3);

            if (f1 < f)
            {
                float f2 = (float)(par4 + village1.getVillageRadius());

                if (f1 <= f2 * f2)
                {
                    village = village1;
                    f = f1;
                }
            }
        }

        return village;
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
        int i = 0;

        while (i < this.newDoors.size())
        {
            VillageDoorInfo villagedoorinfo = (VillageDoorInfo)this.newDoors.get(i);
            boolean flag = false;
            Iterator iterator = this.villageList.iterator();

            while (true)
            {
                if (iterator.hasNext())
                {
                    Village village = (Village)iterator.next();
                    int j = (int)village.getCenter().getDistanceSquared(villagedoorinfo.posX, villagedoorinfo.posY, villagedoorinfo.posZ);
                    float k = 32f + village.getVillageRadius(); //BugFix: Avoid int wrapping

                    if (j > k * k)
                    {
                        continue;
                    }

                    village.addVillageDoorInfo(villagedoorinfo);
                    flag = true;
                }

                if (!flag)
                {
                    Village village1 = new Village(this.worldObj);
                    village1.addVillageDoorInfo(villagedoorinfo);
                    this.villageList.add(village1);
                    this.markDirty();
                }

                ++i;
                break;
            }
        }

        this.newDoors.clear();
    }

    private void addUnassignedWoodenDoorsAroundToNewDoorsList(ChunkCoordinates par1ChunkCoordinates)
    {
        byte b0 = 16;
        byte b1 = 4;
        byte b2 = 16;

        for (int i = par1ChunkCoordinates.posX - b0; i < par1ChunkCoordinates.posX + b0; ++i)
        {
            for (int j = par1ChunkCoordinates.posY - b1; j < par1ChunkCoordinates.posY + b1; ++j)
            {
                for (int k = par1ChunkCoordinates.posZ - b2; k < par1ChunkCoordinates.posZ + b2; ++k)
                {
                    if (this.isWoodenDoorAt(i, j, k))
                    {
                        VillageDoorInfo villagedoorinfo = this.getVillageDoorAt(i, j, k);

                        if (villagedoorinfo == null)
                        {
                            this.addDoorToNewListIfAppropriate(i, j, k);
                        }
                        else
                        {
                            villagedoorinfo.lastActivityTimestamp = this.tickCounter;
                        }
                    }
                }
            }
        }
    }

    private VillageDoorInfo getVillageDoorAt(int par1, int par2, int par3)
    {
        Iterator iterator = this.newDoors.iterator();
        VillageDoorInfo villagedoorinfo;

        do
        {
            if (!iterator.hasNext())
            {
                iterator = this.villageList.iterator();
                VillageDoorInfo villagedoorinfo1;

                do
                {
                    if (!iterator.hasNext())
                    {
                        return null;
                    }

                    Village village = (Village)iterator.next();
                    villagedoorinfo1 = village.getVillageDoorAt(par1, par2, par3);
                }
                while (villagedoorinfo1 == null);

                return villagedoorinfo1;
            }

            villagedoorinfo = (VillageDoorInfo)iterator.next();
        }
        while (villagedoorinfo.posX != par1 || villagedoorinfo.posZ != par3 || Math.abs(villagedoorinfo.posY - par2) > 1);

        return villagedoorinfo;
    }

    private void addDoorToNewListIfAppropriate(int par1, int par2, int par3)
    {
        int l = ((BlockDoor)Blocks.wooden_door).func_150013_e(this.worldObj, par1, par2, par3);
        int i1;
        int j1;

        if (l != 0 && l != 2)
        {
            i1 = 0;

            for (j1 = -5; j1 < 0; ++j1)
            {
                if (this.worldObj.canBlockSeeTheSky(par1, par2, par3 + j1))
                {
                    --i1;
                }
            }

            for (j1 = 1; j1 <= 5; ++j1)
            {
                if (this.worldObj.canBlockSeeTheSky(par1, par2, par3 + j1))
                {
                    ++i1;
                }
            }

            if (i1 != 0)
            {
                this.newDoors.add(new VillageDoorInfo(par1, par2, par3, 0, i1 > 0 ? -2 : 2, this.tickCounter));
            }
        }
        else
        {
            i1 = 0;

            for (j1 = -5; j1 < 0; ++j1)
            {
                if (this.worldObj.canBlockSeeTheSky(par1 + j1, par2, par3))
                {
                    --i1;
                }
            }

            for (j1 = 1; j1 <= 5; ++j1)
            {
                if (this.worldObj.canBlockSeeTheSky(par1 + j1, par2, par3))
                {
                    ++i1;
                }
            }

            if (i1 != 0)
            {
                this.newDoors.add(new VillageDoorInfo(par1, par2, par3, i1 > 0 ? -2 : 2, 0, this.tickCounter));
            }
        }
    }

    private boolean isVillagerPositionPresent(int par1, int par2, int par3)
    {
        Iterator iterator = this.villagerPositionsList.iterator();
        ChunkCoordinates chunkcoordinates;

        do
        {
            if (!iterator.hasNext())
            {
                return false;
            }

            chunkcoordinates = (ChunkCoordinates)iterator.next();
        }
        while (chunkcoordinates.posX != par1 || chunkcoordinates.posY != par2 || chunkcoordinates.posZ != par3);

        return true;
    }

    private boolean isWoodenDoorAt(int par1, int par2, int par3)
    {
        return this.worldObj.func_147439_a(par1, par2, par3) == Blocks.wooden_door;
    }

    // JAVADOC METHOD $$ func_76184_a
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.tickCounter = par1NBTTagCompound.getInteger("Tick");
        NBTTagList nbttaglist = par1NBTTagCompound.func_150295_c("Villages", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
            Village village = new Village();
            village.readVillageDataFromNBT(nbttagcompound1);
            this.villageList.add(village);
        }
    }

    // JAVADOC METHOD $$ func_76187_b
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("Tick", this.tickCounter);
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            Village village = (Village)iterator.next();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            village.writeVillageDataToNBT(nbttagcompound1);
            nbttaglist.appendTag(nbttagcompound1);
        }

        par1NBTTagCompound.setTag("Villages", nbttaglist);
    }
}