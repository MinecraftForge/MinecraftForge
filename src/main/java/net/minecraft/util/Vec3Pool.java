package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;

public class Vec3Pool
{
    private final int truncateArrayResetThreshold;
    private final int minimumSize;
    // JAVADOC FIELD $$ field_72350_c
    private final List vec3Cache = new ArrayList();
    private int nextFreeSpace;
    private int maximumSizeSinceLastTruncation;
    private int resetCount;
    private static final String __OBFID = "CL_00000613";

    public Vec3Pool(int par1, int par2)
    {
        this.truncateArrayResetThreshold = par1;
        this.minimumSize = par2;
    }

    // JAVADOC METHOD $$ func_72345_a
    public Vec3 getVecFromPool(double par1, double par3, double par5)
    {
        if (this.func_82589_e())
        {
            return new Vec3(this, par1, par3, par5);
        }
        else
        {
            Vec3 vec3;

            if (this.nextFreeSpace >= this.vec3Cache.size())
            {
                vec3 = new Vec3(this, par1, par3, par5);
                this.vec3Cache.add(vec3);
            }
            else
            {
                vec3 = (Vec3)this.vec3Cache.get(this.nextFreeSpace);
                vec3.setComponents(par1, par3, par5);
            }

            ++this.nextFreeSpace;
            return vec3;
        }
    }

    // JAVADOC METHOD $$ func_72343_a
    public void clear()
    {
        if (!this.func_82589_e())
        {
            if (this.nextFreeSpace > this.maximumSizeSinceLastTruncation)
            {
                this.maximumSizeSinceLastTruncation = this.nextFreeSpace;
            }

            if (this.resetCount++ == this.truncateArrayResetThreshold)
            {
                int i = Math.max(this.maximumSizeSinceLastTruncation, this.vec3Cache.size() - this.minimumSize);

                while (this.vec3Cache.size() > i)
                {
                    this.vec3Cache.remove(i);
                }

                this.maximumSizeSinceLastTruncation = 0;
                this.resetCount = 0;
            }

            this.nextFreeSpace = 0;
        }
    }

    @SideOnly(Side.CLIENT)
    public void clearAndFreeCache()
    {
        if (!this.func_82589_e())
        {
            this.nextFreeSpace = 0;
            this.vec3Cache.clear();
        }
    }

    public int getPoolSize()
    {
        return this.vec3Cache.size();
    }

    public int func_82590_d()
    {
        return this.nextFreeSpace;
    }

    private boolean func_82589_e()
    {
        return this.minimumSize < 0 || this.truncateArrayResetThreshold < 0;
    }
}