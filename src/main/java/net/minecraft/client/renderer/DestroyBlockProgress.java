package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DestroyBlockProgress
{
    // JAVADOC FIELD $$ field_73115_a
    private final int miningPlayerEntId;
    private final int partialBlockX;
    private final int partialBlockY;
    private final int partialBlockZ;
    // JAVADOC FIELD $$ field_73112_e
    private int partialBlockProgress;
    // JAVADOC FIELD $$ field_82745_f
    private int createdAtCloudUpdateTick;
    private static final String __OBFID = "CL_00001427";

    public DestroyBlockProgress(int par1, int par2, int par3, int par4)
    {
        this.miningPlayerEntId = par1;
        this.partialBlockX = par2;
        this.partialBlockY = par3;
        this.partialBlockZ = par4;
    }

    public int getPartialBlockX()
    {
        return this.partialBlockX;
    }

    public int getPartialBlockY()
    {
        return this.partialBlockY;
    }

    public int getPartialBlockZ()
    {
        return this.partialBlockZ;
    }

    // JAVADOC METHOD $$ func_73107_a
    public void setPartialBlockDamage(int par1)
    {
        if (par1 > 10)
        {
            par1 = 10;
        }

        this.partialBlockProgress = par1;
    }

    public int getPartialBlockDamage()
    {
        return this.partialBlockProgress;
    }

    // JAVADOC METHOD $$ func_82744_b
    public void setCloudUpdateTick(int par1)
    {
        this.createdAtCloudUpdateTick = par1;
    }

    // JAVADOC METHOD $$ func_82743_f
    public int getCreationCloudUpdateTick()
    {
        return this.createdAtCloudUpdateTick;
    }
}