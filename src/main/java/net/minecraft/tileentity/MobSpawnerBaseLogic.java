package net.minecraft.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public abstract class MobSpawnerBaseLogic
{
    // JAVADOC FIELD $$ field_98286_b
    public int spawnDelay = 20;
    private String mobID = "Pig";
    // JAVADOC FIELD $$ field_98285_e
    private List minecartToSpawn;
    private MobSpawnerBaseLogic.WeightedRandomMinecart randomMinecart;
    public double field_98287_c;
    public double field_98284_d;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    // JAVADOC FIELD $$ field_98294_i
    private int spawnCount = 4;
    private Entity field_98291_j;
    private int maxNearbyEntities = 6;
    // JAVADOC FIELD $$ field_98289_l
    private int activatingRangeFromPlayer = 16;
    // JAVADOC FIELD $$ field_98290_m
    private int spawnRange = 4;
    private static final String __OBFID = "CL_00000129";

    // JAVADOC METHOD $$ func_98276_e
    public String getEntityNameToSpawn()
    {
        if (this.getRandomMinecart() == null)
        {
            if (this.mobID.equals("Minecart"))
            {
                this.mobID = "MinecartRideable";
            }

            return this.mobID;
        }
        else
        {
            return this.getRandomMinecart().minecartName;
        }
    }

    public void setMobID(String par1Str)
    {
        this.mobID = par1Str;
    }

    // JAVADOC METHOD $$ func_98279_f
    public boolean canRun()
    {
        return this.getSpawnerWorld().getClosestPlayer((double)this.getSpawnerX() + 0.5D, (double)this.getSpawnerY() + 0.5D, (double)this.getSpawnerZ() + 0.5D, (double)this.activatingRangeFromPlayer) != null;
    }

    public void updateSpawner()
    {
        if (this.canRun())
        {
            double d2;

            if (this.getSpawnerWorld().isRemote)
            {
                double d0 = (double)((float)this.getSpawnerX() + this.getSpawnerWorld().rand.nextFloat());
                double d1 = (double)((float)this.getSpawnerY() + this.getSpawnerWorld().rand.nextFloat());
                d2 = (double)((float)this.getSpawnerZ() + this.getSpawnerWorld().rand.nextFloat());
                this.getSpawnerWorld().spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
                this.getSpawnerWorld().spawnParticle("flame", d0, d1, d2, 0.0D, 0.0D, 0.0D);

                if (this.spawnDelay > 0)
                {
                    --this.spawnDelay;
                }

                this.field_98284_d = this.field_98287_c;
                this.field_98287_c = (this.field_98287_c + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
            }
            else
            {
                if (this.spawnDelay == -1)
                {
                    this.func_98273_j();
                }

                if (this.spawnDelay > 0)
                {
                    --this.spawnDelay;
                    return;
                }

                boolean flag = false;

                for (int i = 0; i < this.spawnCount; ++i)
                {
                    Entity entity = EntityList.createEntityByName(this.getEntityNameToSpawn(), this.getSpawnerWorld());

                    if (entity == null)
                    {
                        return;
                    }

                    int j = this.getSpawnerWorld().getEntitiesWithinAABB(entity.getClass(), AxisAlignedBB.getAABBPool().getAABB((double)this.getSpawnerX(), (double)this.getSpawnerY(), (double)this.getSpawnerZ(), (double)(this.getSpawnerX() + 1), (double)(this.getSpawnerY() + 1), (double)(this.getSpawnerZ() + 1)).expand((double)(this.spawnRange * 2), 4.0D, (double)(this.spawnRange * 2))).size();

                    if (j >= this.maxNearbyEntities)
                    {
                        this.func_98273_j();
                        return;
                    }

                    d2 = (double)this.getSpawnerX() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * (double)this.spawnRange;
                    double d3 = (double)(this.getSpawnerY() + this.getSpawnerWorld().rand.nextInt(3) - 1);
                    double d4 = (double)this.getSpawnerZ() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * (double)this.spawnRange;
                    EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving)entity : null;
                    entity.setLocationAndAngles(d2, d3, d4, this.getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);

                    if (entityliving == null || entityliving.getCanSpawnHere())
                    {
                        this.func_98265_a(entity);
                        this.getSpawnerWorld().playAuxSFX(2004, this.getSpawnerX(), this.getSpawnerY(), this.getSpawnerZ(), 0);

                        if (entityliving != null)
                        {
                            entityliving.spawnExplosionParticle();
                        }

                        flag = true;
                    }
                }

                if (flag)
                {
                    this.func_98273_j();
                }
            }
        }
    }

    public Entity func_98265_a(Entity par1Entity)
    {
        if (this.getRandomMinecart() != null)
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            par1Entity.writeToNBTOptional(nbttagcompound);
            Iterator iterator = this.getRandomMinecart().field_98222_b.func_150296_c().iterator();

            while (iterator.hasNext())
            {
                String s = (String)iterator.next();
                NBTBase nbtbase = this.getRandomMinecart().field_98222_b.getTag(s);
                nbttagcompound.setTag(s, nbtbase.copy());
            }

            par1Entity.readFromNBT(nbttagcompound);

            if (par1Entity.worldObj != null)
            {
                par1Entity.worldObj.spawnEntityInWorld(par1Entity);
            }

            NBTTagCompound nbttagcompound2;

            for (Entity entity1 = par1Entity; nbttagcompound.func_150297_b("Riding", 10); nbttagcompound = nbttagcompound2)
            {
                nbttagcompound2 = nbttagcompound.getCompoundTag("Riding");
                Entity entity2 = EntityList.createEntityByName(nbttagcompound2.getString("id"), par1Entity.worldObj);

                if (entity2 != null)
                {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    entity2.writeToNBTOptional(nbttagcompound1);
                    Iterator iterator1 = nbttagcompound2.func_150296_c().iterator();

                    while (iterator1.hasNext())
                    {
                        String s1 = (String)iterator1.next();
                        NBTBase nbtbase1 = nbttagcompound2.getTag(s1);
                        nbttagcompound1.setTag(s1, nbtbase1.copy());
                    }

                    entity2.readFromNBT(nbttagcompound1);
                    entity2.setLocationAndAngles(entity1.posX, entity1.posY, entity1.posZ, entity1.rotationYaw, entity1.rotationPitch);

                    if (par1Entity.worldObj != null)
                    {
                        par1Entity.worldObj.spawnEntityInWorld(entity2);
                    }

                    entity1.mountEntity(entity2);
                }

                entity1 = entity2;
            }
        }
        else if (par1Entity instanceof EntityLivingBase && par1Entity.worldObj != null)
        {
            ((EntityLiving)par1Entity).onSpawnWithEgg((IEntityLivingData)null);
            this.getSpawnerWorld().spawnEntityInWorld(par1Entity);
        }

        return par1Entity;
    }

    private void func_98273_j()
    {
        if (this.maxSpawnDelay <= this.minSpawnDelay)
        {
            this.spawnDelay = this.minSpawnDelay;
        }
        else
        {
            int i = this.maxSpawnDelay - this.minSpawnDelay;
            this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(i);
        }

        if (this.minecartToSpawn != null && this.minecartToSpawn.size() > 0)
        {
            this.setRandomMinecart((MobSpawnerBaseLogic.WeightedRandomMinecart)WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.minecartToSpawn));
        }

        this.func_98267_a(1);
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.mobID = par1NBTTagCompound.getString("EntityId");
        this.spawnDelay = par1NBTTagCompound.getShort("Delay");

        if (par1NBTTagCompound.func_150297_b("SpawnPotentials", 9))
        {
            this.minecartToSpawn = new ArrayList();
            NBTTagList nbttaglist = par1NBTTagCompound.func_150295_c("SpawnPotentials", 10);

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                this.minecartToSpawn.add(new MobSpawnerBaseLogic.WeightedRandomMinecart(nbttaglist.func_150305_b(i)));
            }
        }
        else
        {
            this.minecartToSpawn = null;
        }

        if (par1NBTTagCompound.func_150297_b("SpawnData", 10))
        {
            this.setRandomMinecart(new MobSpawnerBaseLogic.WeightedRandomMinecart(par1NBTTagCompound.getCompoundTag("SpawnData"), this.mobID));
        }
        else
        {
            this.setRandomMinecart((MobSpawnerBaseLogic.WeightedRandomMinecart)null);
        }

        if (par1NBTTagCompound.func_150297_b("MinSpawnDelay", 99))
        {
            this.minSpawnDelay = par1NBTTagCompound.getShort("MinSpawnDelay");
            this.maxSpawnDelay = par1NBTTagCompound.getShort("MaxSpawnDelay");
            this.spawnCount = par1NBTTagCompound.getShort("SpawnCount");
        }

        if (par1NBTTagCompound.func_150297_b("MaxNearbyEntities", 99))
        {
            this.maxNearbyEntities = par1NBTTagCompound.getShort("MaxNearbyEntities");
            this.activatingRangeFromPlayer = par1NBTTagCompound.getShort("RequiredPlayerRange");
        }

        if (par1NBTTagCompound.func_150297_b("SpawnRange", 99))
        {
            this.spawnRange = par1NBTTagCompound.getShort("SpawnRange");
        }

        if (this.getSpawnerWorld() != null && this.getSpawnerWorld().isRemote)
        {
            this.field_98291_j = null;
        }
    }

    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setString("EntityId", this.getEntityNameToSpawn());
        par1NBTTagCompound.setShort("Delay", (short)this.spawnDelay);
        par1NBTTagCompound.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
        par1NBTTagCompound.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        par1NBTTagCompound.setShort("SpawnCount", (short)this.spawnCount);
        par1NBTTagCompound.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        par1NBTTagCompound.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
        par1NBTTagCompound.setShort("SpawnRange", (short)this.spawnRange);

        if (this.getRandomMinecart() != null)
        {
            par1NBTTagCompound.setTag("SpawnData", this.getRandomMinecart().field_98222_b.copy());
        }

        if (this.getRandomMinecart() != null || this.minecartToSpawn != null && this.minecartToSpawn.size() > 0)
        {
            NBTTagList nbttaglist = new NBTTagList();

            if (this.minecartToSpawn != null && this.minecartToSpawn.size() > 0)
            {
                Iterator iterator = this.minecartToSpawn.iterator();

                while (iterator.hasNext())
                {
                    MobSpawnerBaseLogic.WeightedRandomMinecart weightedrandomminecart = (MobSpawnerBaseLogic.WeightedRandomMinecart)iterator.next();
                    nbttaglist.appendTag(weightedrandomminecart.func_98220_a());
                }
            }
            else
            {
                nbttaglist.appendTag(this.getRandomMinecart().func_98220_a());
            }

            par1NBTTagCompound.setTag("SpawnPotentials", nbttaglist);
        }
    }

    // JAVADOC METHOD $$ func_98268_b
    public boolean setDelayToMin(int par1)
    {
        if (par1 == 1 && this.getSpawnerWorld().isRemote)
        {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        }
        else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public Entity func_98281_h()
    {
        if (this.field_98291_j == null)
        {
            Entity entity = EntityList.createEntityByName(this.getEntityNameToSpawn(), (World)null);
            entity = this.func_98265_a(entity);
            this.field_98291_j = entity;
        }

        return this.field_98291_j;
    }

    public MobSpawnerBaseLogic.WeightedRandomMinecart getRandomMinecart()
    {
        return this.randomMinecart;
    }

    public void setRandomMinecart(MobSpawnerBaseLogic.WeightedRandomMinecart par1WeightedRandomMinecart)
    {
        this.randomMinecart = par1WeightedRandomMinecart;
    }

    public abstract void func_98267_a(int var1);

    public abstract World getSpawnerWorld();

    public abstract int getSpawnerX();

    public abstract int getSpawnerY();

    public abstract int getSpawnerZ();

    public class WeightedRandomMinecart extends WeightedRandom.Item
    {
        public final NBTTagCompound field_98222_b;
        public final String minecartName;
        private static final String __OBFID = "CL_00000130";

        public WeightedRandomMinecart(NBTTagCompound par2NBTTagCompound)
        {
            super(par2NBTTagCompound.getInteger("Weight"));
            NBTTagCompound nbttagcompound1 = par2NBTTagCompound.getCompoundTag("Properties");
            String s = par2NBTTagCompound.getString("Type");

            if (s.equals("Minecart"))
            {
                if (nbttagcompound1 != null)
                {
                    switch (nbttagcompound1.getInteger("Type"))
                    {
                        case 0:
                            s = "MinecartRideable";
                            break;
                        case 1:
                            s = "MinecartChest";
                            break;
                        case 2:
                            s = "MinecartFurnace";
                    }
                }
                else
                {
                    s = "MinecartRideable";
                }
            }

            this.field_98222_b = nbttagcompound1;
            this.minecartName = s;
        }

        public WeightedRandomMinecart(NBTTagCompound par2NBTTagCompound, String par3Str)
        {
            super(1);

            if (par3Str.equals("Minecart"))
            {
                if (par2NBTTagCompound != null)
                {
                    switch (par2NBTTagCompound.getInteger("Type"))
                    {
                        case 0:
                            par3Str = "MinecartRideable";
                            break;
                        case 1:
                            par3Str = "MinecartChest";
                            break;
                        case 2:
                            par3Str = "MinecartFurnace";
                    }
                }
                else
                {
                    par3Str = "MinecartRideable";
                }
            }

            this.field_98222_b = par2NBTTagCompound;
            this.minecartName = par3Str;
        }

        public NBTTagCompound func_98220_a()
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("Properties", this.field_98222_b);
            nbttagcompound.setString("Type", this.minecartName);
            nbttagcompound.setInteger("Weight", this.itemWeight);
            return nbttagcompound;
        }
    }
}