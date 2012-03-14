package net.minecraft.src;

import java.util.List;

public abstract class EntityAnimal extends EntityAgeable implements IAnimals
{
    private int inLove;

    /**
     * This is representation of a counter for reproduction progress. (Note that this is different from the inLove which
     * represent being in Love-Mode)
     */
    private int breeding = 0;

    public EntityAnimal(World par1World)
    {
        super(par1World);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (this.inLove > 0)
        {
            --this.inLove;
            String var1 = "heart";

            if (this.inLove % 10 == 0)
            {
                double var2 = this.rand.nextGaussian() * 0.02D;
                double var4 = this.rand.nextGaussian() * 0.02D;
                double var6 = this.rand.nextGaussian() * 0.02D;
                this.worldObj.spawnParticle(var1, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var2, var4, var6);
            }
        }
        else
        {
            this.breeding = 0;
        }
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
    protected void attackEntity(Entity par1Entity, float par2)
    {
        if (par1Entity instanceof EntityPlayer)
        {
            if (par2 < 3.0F)
            {
                double var3 = par1Entity.posX - this.posX;
                double var5 = par1Entity.posZ - this.posZ;
                this.rotationYaw = (float)(Math.atan2(var5, var3) * 180.0D / Math.PI) - 90.0F;
                this.hasAttacked = true;
            }

            EntityPlayer var7 = (EntityPlayer)par1Entity;

            if (var7.getCurrentEquippedItem() == null || !this.isWheat(var7.getCurrentEquippedItem()))
            {
                this.entityToAttack = null;
            }
        }
        else if (par1Entity instanceof EntityAnimal)
        {
            EntityAnimal var8 = (EntityAnimal)par1Entity;

            if (this.getGrowingAge() > 0 && var8.getGrowingAge() < 0)
            {
                if ((double)par2 < 2.5D)
                {
                    this.hasAttacked = true;
                }
            }
            else if (this.inLove > 0 && var8.inLove > 0)
            {
                if (var8.entityToAttack == null)
                {
                    var8.entityToAttack = this;
                }

                if (var8.entityToAttack == this && (double)par2 < 3.5D)
                {
                    ++var8.inLove;
                    ++this.inLove;
                    ++this.breeding;

                    if (this.breeding % 4 == 0)
                    {
                        this.worldObj.spawnParticle("heart", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0.0D, 0.0D, 0.0D);
                    }

                    if (this.breeding == 60)
                    {
                        this.procreate((EntityAnimal)par1Entity);
                    }
                }
                else
                {
                    this.breeding = 0;
                }
            }
            else
            {
                this.breeding = 0;
                this.entityToAttack = null;
            }
        }
    }

    /**
     * Creates a baby animal according to the animal type of the target at the actual position and spawns 'love'
     * particles.
     */
    private void procreate(EntityAnimal par1EntityAnimal)
    {
        EntityAnimal var2 = this.spawnBabyAnimal(par1EntityAnimal);

        if (var2 != null)
        {
            this.setGrowingAge(6000);
            par1EntityAnimal.setGrowingAge(6000);
            this.inLove = 0;
            this.breeding = 0;
            this.entityToAttack = null;
            par1EntityAnimal.entityToAttack = null;
            par1EntityAnimal.breeding = 0;
            par1EntityAnimal.inLove = 0;
            var2.setGrowingAge(-24000);
            var2.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);

            for (int var3 = 0; var3 < 7; ++var3)
            {
                double var4 = this.rand.nextGaussian() * 0.02D;
                double var6 = this.rand.nextGaussian() * 0.02D;
                double var8 = this.rand.nextGaussian() * 0.02D;
                this.worldObj.spawnParticle("heart", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var4, var6, var8);
            }

            this.worldObj.spawnEntityInWorld(var2);
        }
    }

    /**
     * This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.
     */
    public abstract EntityAnimal spawnBabyAnimal(EntityAnimal var1);

    protected void attackBlockedEntity(Entity par1Entity, float par2) {}

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        this.fleeingTick = 60;
        this.entityToAttack = null;
        this.inLove = 0;
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    /**
     * Takes a coordinate in and returns a weight to determine how likely this creature will try to path to the block.
     * Args: x, y, z
     */
    public float getBlockPathWeight(int par1, int par2, int par3)
    {
        return this.worldObj.getBlockId(par1, par2 - 1, par3) == Block.grass.blockID ? 10.0F : this.worldObj.getLightBrightness(par1, par2, par3) - 0.5F;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("InLove", this.inLove);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.inLove = par1NBTTagCompound.getInteger("InLove");
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
     * (Animals, Spiders at day, peaceful PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        if (this.fleeingTick > 0)
        {
            return null;
        }
        else
        {
            float var1 = 8.0F;
            List var2;
            int var3;
            EntityAnimal var4;

            if (this.inLove > 0)
            {
                var2 = this.worldObj.getEntitiesWithinAABB(this.getClass(), this.boundingBox.expand((double)var1, (double)var1, (double)var1));

                for (var3 = 0; var3 < var2.size(); ++var3)
                {
                    var4 = (EntityAnimal)var2.get(var3);

                    if (var4 != this && var4.inLove > 0)
                    {
                        return var4;
                    }
                }
            }
            else if (this.getGrowingAge() == 0)
            {
                var2 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.boundingBox.expand((double)var1, (double)var1, (double)var1));

                for (var3 = 0; var3 < var2.size(); ++var3)
                {
                    EntityPlayer var5 = (EntityPlayer)var2.get(var3);

                    if (var5.getCurrentEquippedItem() != null && this.isWheat(var5.getCurrentEquippedItem()))
                    {
                        return var5;
                    }
                }
            }
            else if (this.getGrowingAge() > 0)
            {
                var2 = this.worldObj.getEntitiesWithinAABB(this.getClass(), this.boundingBox.expand((double)var1, (double)var1, (double)var1));

                for (var3 = 0; var3 < var2.size(); ++var3)
                {
                    var4 = (EntityAnimal)var2.get(var3);

                    if (var4 != this && var4.getGrowingAge() < 0)
                    {
                        return var4;
                    }
                }
            }

            return null;
        }
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        int var1 = MathHelper.floor_double(this.posX);
        int var2 = MathHelper.floor_double(this.boundingBox.minY);
        int var3 = MathHelper.floor_double(this.posZ);
        return this.worldObj.getBlockId(var1, var2 - 1, var3) == Block.grass.blockID && this.worldObj.getFullBlockLightValue(var1, var2, var3) > 8 && super.getCanSpawnHere();
    }

    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    public int getTalkInterval()
    {
        return 120;
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
        return false;
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(EntityPlayer par1EntityPlayer)
    {
        return 1 + this.worldObj.rand.nextInt(3);
    }

    /**
     * Checks if the parameter is an wheat item.
     */
    public boolean isWheat(ItemStack par1ItemStack)
    {
        return par1ItemStack.itemID == Item.wheat.shiftedIndex;
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

        if (var2 != null && this.isWheat(var2) && this.getGrowingAge() == 0)
        {
            if (this.worldObj.getWorldInfo().getGameType() != 1)
            {
                --var2.stackSize;

                if (var2.stackSize <= 0)
                {
                    par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                }
            }

            this.inLove = 600;
            this.entityToAttack = null;

            for (int var3 = 0; var3 < 7; ++var3)
            {
                double var4 = this.rand.nextGaussian() * 0.02D;
                double var6 = this.rand.nextGaussian() * 0.02D;
                double var8 = this.rand.nextGaussian() * 0.02D;
                this.worldObj.spawnParticle("heart", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var4, var6, var8);
            }

            return true;
        }
        else
        {
            return super.interact(par1EntityPlayer);
        }
    }

    public boolean func_48363_r_()
    {
        return this.inLove > 0;
    }

    public void func_48364_s_()
    {
        this.inLove = 0;
    }

    public boolean func_48362_b(EntityAnimal par1EntityAnimal)
    {
        return par1EntityAnimal == this ? false : (par1EntityAnimal.getClass() != this.getClass() ? false : this.func_48363_r_() && par1EntityAnimal.func_48363_r_());
    }
}
