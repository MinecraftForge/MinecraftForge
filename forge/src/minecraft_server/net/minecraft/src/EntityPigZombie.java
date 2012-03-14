package net.minecraft.src;

import java.util.List;

public class EntityPigZombie extends EntityZombie
{
    /** Above zero if this PigZombie is Angry. */
    private int angerLevel = 0;

    /** A random delay until this PigZombie next makes a sound. */
    private int randomSoundDelay = 0;

    /** The ItemStack that any PigZombie holds (a gold sword, in fact). */
    private static final ItemStack defaultHeldItem = new ItemStack(Item.swordGold, 1);

    public EntityPigZombie(World par1World)
    {
        super(par1World);
        this.texture = "/mob/pigzombie.png";
        this.moveSpeed = 0.5F;
        this.attackStrength = 5;
        this.isImmuneToFire = true;
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    protected boolean isAIEnabled()
    {
        return false;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.moveSpeed = this.entityToAttack != null ? 0.95F : 0.5F;

        if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0)
        {
            this.worldObj.playSoundAtEntity(this, "mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
        }

        super.onUpdate();
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return this.worldObj.difficultySetting > 0 && this.worldObj.checkIfAABBIsClear(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() == 0 && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("Anger", (short)this.angerLevel);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.angerLevel = par1NBTTagCompound.getShort("Anger");
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
     * (Animals, Spiders at day, peaceful PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        return this.angerLevel == 0 ? null : super.findPlayerToAttack();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        Entity var3 = par1DamageSource.getEntity();

        if (var3 instanceof EntityPlayer)
        {
            List var4 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(32.0D, 32.0D, 32.0D));

            for (int var5 = 0; var5 < var4.size(); ++var5)
            {
                Entity var6 = (Entity)var4.get(var5);

                if (var6 instanceof EntityPigZombie)
                {
                    EntityPigZombie var7 = (EntityPigZombie)var6;
                    var7.becomeAngryAt(var3);
                }
            }

            this.becomeAngryAt(var3);
        }

        return super.attackEntityFrom(par1DamageSource, par2);
    }

    /**
     * Causes this PigZombie to become angry at the supplied Entity (which will be a player).
     */
    private void becomeAngryAt(Entity par1Entity)
    {
        this.entityToAttack = par1Entity;
        this.angerLevel = 400 + this.rand.nextInt(400);
        this.randomSoundDelay = this.rand.nextInt(40);
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.zombiepig.zpig";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.zombiepig.zpighurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.zombiepig.zpigdeath";
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int var3 = this.rand.nextInt(2 + par2);
        int var4;

        for (var4 = 0; var4 < var3; ++var4)
        {
            this.dropItem(Item.rottenFlesh.shiftedIndex, 1);
        }

        var3 = this.rand.nextInt(2 + par2);

        for (var4 = 0; var4 < var3; ++var4)
        {
            this.dropItem(Item.goldNugget.shiftedIndex, 1);
        }
    }

    protected void dropRareDrop(int par1)
    {
        if (par1 > 0)
        {
            ItemStack var2 = new ItemStack(Item.swordGold);
            EnchantmentHelper.func_48622_a(this.rand, var2, 5);
            this.entityDropItem(var2, 0.0F);
        }
        else
        {
            int var3 = this.rand.nextInt(3);

            if (var3 == 0)
            {
                this.dropItem(Item.ingotGold.shiftedIndex, 1);
            }
            else if (var3 == 1)
            {
                this.dropItem(Item.swordGold.shiftedIndex, 1);
            }
            else if (var3 == 2)
            {
                this.dropItem(Item.helmetGold.shiftedIndex, 1);
            }
        }
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return Item.rottenFlesh.shiftedIndex;
    }
}
