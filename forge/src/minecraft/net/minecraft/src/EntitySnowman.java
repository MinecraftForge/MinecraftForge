package net.minecraft.src;

public class EntitySnowman extends EntityGolem
{
    public EntitySnowman(World par1World)
    {
        super(par1World);
        this.texture = "/mob/snowman.png";
        this.setSize(0.4F, 1.8F);
        this.getNavigator().func_48664_a(true);
        this.tasks.addTask(1, new EntityAIArrowAttack(this, 0.25F, 2, 20));
        this.tasks.addTask(2, new EntityAIWander(this, 0.2F));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityMob.class, 16.0F, 0, true));
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    public int getMaxHealth()
    {
        return 4;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (this.isWet())
        {
            this.attackEntityFrom(DamageSource.drown, 1);
        }

        int var1 = MathHelper.floor_double(this.posX);
        int var2 = MathHelper.floor_double(this.posZ);

        if (this.worldObj.func_48454_a(var1, var2).getFloatTemperature() > 1.0F)
        {
            this.attackEntityFrom(DamageSource.onFire, 1);
        }

        for (var1 = 0; var1 < 4; ++var1)
        {
            var2 = MathHelper.floor_double(this.posX + (double)((float)(var1 % 2 * 2 - 1) * 0.25F));
            int var3 = MathHelper.floor_double(this.posY);
            int var4 = MathHelper.floor_double(this.posZ + (double)((float)(var1 / 2 % 2 * 2 - 1) * 0.25F));

            if (this.worldObj.getBlockId(var2, var3, var4) == 0 && this.worldObj.func_48454_a(var2, var4).getFloatTemperature() < 0.8F && Block.snow.canPlaceBlockAt(this.worldObj, var2, var3, var4))
            {
                this.worldObj.setBlockWithNotify(var2, var3, var4, Block.snow.blockID);
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return Item.snowball.shiftedIndex;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int var3 = this.rand.nextInt(16);

        for (int var4 = 0; var4 < var3; ++var4)
        {
            this.dropItem(Item.snowball.shiftedIndex, 1);
        }
    }
}
