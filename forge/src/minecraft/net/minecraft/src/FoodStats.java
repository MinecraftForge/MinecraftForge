package net.minecraft.src;

public class FoodStats
{
    private int foodLevel = 20;
    private float foodSaturationLevel = 5.0F;
    private float foodExhaustionLevel;
    private int foodTimer = 0;
    private int prevFoodLevel = 20;

    /**
     * Args: int foodLevel, float foodSaturationModifier
     */
    public void addStats(int par1, float par2)
    {
        this.foodLevel = Math.min(par1 + this.foodLevel, 20);
        this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float)par1 * par2 * 2.0F, (float)this.foodLevel);
    }

    public void addStats(ItemFood par1ItemFood)
    {
        this.addStats(par1ItemFood.getHealAmount(), par1ItemFood.getSaturationModifier());
    }

    public void onUpdate(EntityPlayer par1EntityPlayer)
    {
        int var2 = par1EntityPlayer.worldObj.difficultySetting;
        this.prevFoodLevel = this.foodLevel;

        if (this.foodExhaustionLevel > 6.0F)
        {
            this.foodExhaustionLevel -= 6.0F;

            if (this.foodSaturationLevel > 0.0F)
            {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
            }
            else if (var2 > 0)
            {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        if (this.foodLevel >= 14 && par1EntityPlayer.shouldHeal())
        {
            ++this.foodTimer;

            if (this.foodTimer >= 80)
            {
                par1EntityPlayer.heal(1);
                this.foodTimer = 0;
            }
        }
        /*else if (this.foodLevel <= 0)
        {
            ++this.foodTimer;

            if (this.foodTimer >= 80)
            {
                if (par1EntityPlayer.getEntityHealth() > 10 || var2 >= 3 || par1EntityPlayer.getEntityHealth() > 1 && var2 >= 2)
                {
                    par1EntityPlayer.attackEntityFrom(DamageSource.starve, 1);
                }

                this.foodTimer = 0;
            }
        }*/
        else
        {
            this.foodTimer = 0;
        }
    }

    /**
     * Reads food stats from an NBT object.
     */
    public void readNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.hasKey("foodLevel"))
        {
            this.foodLevel = par1NBTTagCompound.getInteger("foodLevel");
            this.foodTimer = par1NBTTagCompound.getInteger("foodTickTimer");
            this.foodSaturationLevel = par1NBTTagCompound.getFloat("foodSaturationLevel");
            this.foodExhaustionLevel = par1NBTTagCompound.getFloat("foodExhaustionLevel");
        }
    }

    /**
     * Writes food stats to an NBT object.
     */
    public void writeNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("foodLevel", this.foodLevel);
        par1NBTTagCompound.setInteger("foodTickTimer", this.foodTimer);
        par1NBTTagCompound.setFloat("foodSaturationLevel", this.foodSaturationLevel);
        par1NBTTagCompound.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
    }

    public int getFoodLevel()
    {
        return this.foodLevel;
    }

    public int getPrevFoodLevel()
    {
        return this.prevFoodLevel;
    }

    /**
     * If foodLevel is not max.
     */
    public boolean needFood()
    {
        return this.foodLevel < 20;
    }

    /**
     * adds input to foodExhaustionLevel to a max of 40
     */
    public void addExhaustion(float par1)
    {
        this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + par1, 40.0F);
    }

    public float getSaturationLevel()
    {
        return this.foodSaturationLevel;
    }

    public void setFoodLevel(int par1)
    {
        this.foodLevel = par1;
    }

    public void setFoodSaturationLevel(float par1)
    {
        this.foodSaturationLevel = par1;
    }
}
