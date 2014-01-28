package net.minecraft.entity.player;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerCapabilities
{
    // JAVADOC FIELD $$ field_75102_a
    public boolean disableDamage;
    // JAVADOC FIELD $$ field_75100_b
    public boolean isFlying;
    // JAVADOC FIELD $$ field_75101_c
    public boolean allowFlying;
    // JAVADOC FIELD $$ field_75098_d
    public boolean isCreativeMode;
    // JAVADOC FIELD $$ field_75099_e
    public boolean allowEdit = true;
    private float flySpeed = 0.05F;
    private float walkSpeed = 0.1F;
    private static final String __OBFID = "CL_00001708";

    public void writeCapabilitiesToNBT(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        nbttagcompound1.setBoolean("invulnerable", this.disableDamage);
        nbttagcompound1.setBoolean("flying", this.isFlying);
        nbttagcompound1.setBoolean("mayfly", this.allowFlying);
        nbttagcompound1.setBoolean("instabuild", this.isCreativeMode);
        nbttagcompound1.setBoolean("mayBuild", this.allowEdit);
        nbttagcompound1.setFloat("flySpeed", this.flySpeed);
        nbttagcompound1.setFloat("walkSpeed", this.walkSpeed);
        par1NBTTagCompound.setTag("abilities", nbttagcompound1);
    }

    public void readCapabilitiesFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.func_150297_b("abilities", 10))
        {
            NBTTagCompound nbttagcompound1 = par1NBTTagCompound.getCompoundTag("abilities");
            this.disableDamage = nbttagcompound1.getBoolean("invulnerable");
            this.isFlying = nbttagcompound1.getBoolean("flying");
            this.allowFlying = nbttagcompound1.getBoolean("mayfly");
            this.isCreativeMode = nbttagcompound1.getBoolean("instabuild");

            if (nbttagcompound1.func_150297_b("flySpeed", 99))
            {
                this.flySpeed = nbttagcompound1.getFloat("flySpeed");
                this.walkSpeed = nbttagcompound1.getFloat("walkSpeed");
            }

            if (nbttagcompound1.func_150297_b("mayBuild", 1))
            {
                this.allowEdit = nbttagcompound1.getBoolean("mayBuild");
            }
        }
    }

    public float getFlySpeed()
    {
        return this.flySpeed;
    }

    @SideOnly(Side.CLIENT)
    public void setFlySpeed(float par1)
    {
        this.flySpeed = par1;
    }

    public float getWalkSpeed()
    {
        return this.walkSpeed;
    }

    @SideOnly(Side.CLIENT)
    public void setPlayerWalkSpeed(float par1)
    {
        this.walkSpeed = par1;
    }
}