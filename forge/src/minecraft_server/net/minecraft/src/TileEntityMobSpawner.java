package net.minecraft.src;

public class TileEntityMobSpawner extends TileEntity
{
    /** The stored delay before a new spawn. */
    public int delay = -1;

    /**
     * The string ID of the mobs being spawned from this spawner. Defaults to pig, apparently.
     */
    private String mobID = "Pig";
    public double yaw;
    public double yaw2 = 0.0D;

    public TileEntityMobSpawner()
    {
        this.delay = 20;
    }

    public void setMobID(String par1Str)
    {
        this.mobID = par1Str;
    }

    /**
     * Returns true if there is a player in range (using World.getClosestPlayer)
     */
    public boolean anyPlayerInRange()
    {
        return this.worldObj.getClosestPlayer((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D, 16.0D) != null;
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity()
    {
        this.yaw2 = this.yaw;

        if (this.anyPlayerInRange())
        {
            double var1 = (double)((float)this.xCoord + this.worldObj.rand.nextFloat());
            double var3 = (double)((float)this.yCoord + this.worldObj.rand.nextFloat());
            double var5 = (double)((float)this.zCoord + this.worldObj.rand.nextFloat());
            this.worldObj.spawnParticle("smoke", var1, var3, var5, 0.0D, 0.0D, 0.0D);
            this.worldObj.spawnParticle("flame", var1, var3, var5, 0.0D, 0.0D, 0.0D);

            for (this.yaw += (double)(1000.0F / ((float)this.delay + 200.0F)); this.yaw > 360.0D; this.yaw2 -= 360.0D)
            {
                this.yaw -= 360.0D;
            }

            if (!this.worldObj.isRemote)
            {
                if (this.delay == -1)
                {
                    this.updateDelay();
                }

                if (this.delay > 0)
                {
                    --this.delay;
                    return;
                }

                byte var7 = 4;

                for (int var8 = 0; var8 < var7; ++var8)
                {
                    EntityLiving var9 = (EntityLiving)((EntityLiving)EntityList.createEntityInWorld(this.mobID, this.worldObj));

                    if (var9 == null)
                    {
                        return;
                    }

                    int var10 = this.worldObj.getEntitiesWithinAABB(var9.getClass(), AxisAlignedBB.getBoundingBoxFromPool((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 1), (double)(this.zCoord + 1)).expand(8.0D, 4.0D, 8.0D)).size();

                    if (var10 >= 6)
                    {
                        this.updateDelay();
                        return;
                    }

                    if (var9 != null)
                    {
                        double var11 = (double)this.xCoord + (this.worldObj.rand.nextDouble() - this.worldObj.rand.nextDouble()) * 4.0D;
                        double var13 = (double)(this.yCoord + this.worldObj.rand.nextInt(3) - 1);
                        double var15 = (double)this.zCoord + (this.worldObj.rand.nextDouble() - this.worldObj.rand.nextDouble()) * 4.0D;
                        var9.setLocationAndAngles(var11, var13, var15, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);

                        if (var9.getCanSpawnHere())
                        {
                            this.worldObj.spawnEntityInWorld(var9);
                            this.worldObj.playAuxSFX(2004, this.xCoord, this.yCoord, this.zCoord, 0);
                            var9.spawnExplosionParticle();
                            this.updateDelay();
                        }
                    }
                }
            }

            super.updateEntity();
        }
    }

    /**
     * Sets the delay before a new spawn (base delay of 200 + random number up to 600).
     */
    private void updateDelay()
    {
        this.delay = 200 + this.worldObj.rand.nextInt(600);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.mobID = par1NBTTagCompound.getString("EntityId");
        this.delay = par1NBTTagCompound.getShort("Delay");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setString("EntityId", this.mobID);
        par1NBTTagCompound.setShort("Delay", (short)this.delay);
    }

    /**
     * Overriden in a sign to provide the text
     */
    public Packet getDescriptionPacket()
    {
        int var1 = EntityList.func_48582_a(this.mobID);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, var1);
    }
}
