package net.minecraft.src;

public class PlayerCapabilities
{
    /** Disables player damage. */
    public boolean disableDamage = false;

    /** Sets/indicates whether the player is flying. */
    public boolean isFlying = false;

    /** whether or not to allow the player to fly when they double jump. */
    public boolean allowFlying = false;

    /** Used by ItemBucket to say if it should empty when used or not. */
    public boolean depleteBuckets = false;

    public void writeCapabilitiesToNBT(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagCompound var2 = new NBTTagCompound();
        var2.setBoolean("invulnerable", this.disableDamage);
        var2.setBoolean("flying", this.disableDamage);
        var2.setBoolean("mayfly", this.allowFlying);
        var2.setBoolean("instabuild", this.depleteBuckets);
        par1NBTTagCompound.setTag("abilities", var2);
    }

    public void readCapabilitiesFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.hasKey("abilities"))
        {
            NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("abilities");
            this.disableDamage = var2.getBoolean("invulnerable");
            this.isFlying = var2.getBoolean("flying");
            this.allowFlying = var2.getBoolean("mayfly");
            this.depleteBuckets = var2.getBoolean("instabuild");
        }
    }
}
