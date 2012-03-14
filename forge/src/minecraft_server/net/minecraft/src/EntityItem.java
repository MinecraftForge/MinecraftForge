package net.minecraft.src;

import net.minecraft.src.forge.ForgeHooks;

public class EntityItem extends Entity
{
    /** The item stack of this EntityItem. */
    public ItemStack item;

    /**
     * The age of this EntityItem (used to animate it up and down as well as expire it)
     */
    public int age = 0;
    public int delayBeforeCanPickup;

    /** The health of this EntityItem. (For example, damage for tools) */
    private int health = 5;
    public float field_432_ae = (float)(Math.random() * Math.PI * 2.0D);

    public EntityItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack)
    {
        super(par1World);
        this.setSize(0.25F, 0.25F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(par2, par4, par6);
        this.item = par8ItemStack;
        this.rotationYaw = (float)(Math.random() * 360.0D);
        this.motionX = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
        this.motionY = 0.20000000298023224D;
        this.motionZ = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
    }

    protected boolean canTriggerWalking()
    {
        return false;
    }

    public EntityItem(World par1World)
    {
        super(par1World);
        this.setSize(0.25F, 0.25F);
        this.yOffset = this.height / 2.0F;
    }

    protected void entityInit() {}

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (this.delayBeforeCanPickup > 0)
        {
            --this.delayBeforeCanPickup;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03999999910593033D;

        if (this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) == Material.lava)
        {
            this.motionY = 0.20000000298023224D;
            this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.worldObj.playSoundAtEntity(this, "random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
        }

        this.pushOutOfBlocks(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        float var1 = 0.98F;

        if (this.onGround)
        {
            var1 = 0.58800006F;
            int var2 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));

            if (var2 > 0)
            {
                var1 = Block.blocksList[var2].slipperiness * 0.98F;
            }
        }

        this.motionX *= (double)var1;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= (double)var1;

        if (this.onGround)
        {
            this.motionY *= -0.5D;
        }

        ++this.age;

        if (this.age >= 6000)
        {
            this.setEntityDead();
        }
    }

    public void func_48316_k()
    {
        this.age = 4800;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    public boolean handleWaterMovement()
    {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    protected void dealFireDamage(int par1)
    {
        this.attackEntityFrom(DamageSource.inFire, par1);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        this.setBeenAttacked();
        this.health -= par2;

        if (this.health <= 0)
        {
            this.setEntityDead();
        }

        return false;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("Health", (short)((byte)this.health));
        par1NBTTagCompound.setShort("Age", (short)this.age);
        par1NBTTagCompound.setCompoundTag("Item", this.item.writeToNBT(new NBTTagCompound()));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.health = par1NBTTagCompound.getShort("Health") & 255;
        this.age = par1NBTTagCompound.getShort("Age");
        NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("Item");
        this.item = ItemStack.loadItemStackFromNBT(var2);

        if (this.item == null)
        {
            this.setEntityDead();
        }
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer)
    {
        if (!this.worldObj.isRemote)
        {
            int var2 = this.item.stackSize;
            
            if (delayBeforeCanPickup == 0 && !ForgeHooks.onItemPickup(par1EntityPlayer, this))
            {
                ModLoader.onItemPickup(par1EntityPlayer, item);
                this.worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(this, var2);
                if (item.stackSize <= 0)
                {
                    setEntityDead();
                }
                return;
            }
            
            var2 = item.stackSize;

            if (this.delayBeforeCanPickup == 0 && par1EntityPlayer.inventory.addItemStackToInventory(this.item))
            {
                if (this.item.itemID == Block.wood.blockID)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.mineWood);
                }

                if (this.item.itemID == Item.leather.shiftedIndex)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.killCow);
                }

                if (this.item.itemID == Item.diamond.shiftedIndex)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.diamonds);
                }

                if (this.item.itemID == Item.blazeRod.shiftedIndex)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.blazeRod);
                }

                ModLoader.onItemPickup(par1EntityPlayer, item);
                this.worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(this, var2);

                if (this.item.stackSize <= 0)
                {
                    this.setEntityDead();
                }
            }
        }
    }

    public String getUsername()
    {
        return StatCollector.translateToLocal("item." + this.item.getItemName());
    }

    public boolean func_48313_k_()
    {
        return false;
    }
}
