package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;

public class AABBPool
{
    // JAVADOC FIELD $$ field_72306_a
    private final int maxNumCleans;
    // JAVADOC FIELD $$ field_72304_b
    private final int numEntriesToRemove;
    // JAVADOC FIELD $$ field_72305_c
    private final List listAABB = new ArrayList();
    // JAVADOC FIELD $$ field_72302_d
    private int nextPoolIndex;
    // JAVADOC FIELD $$ field_72303_e
    private int maxPoolIndex;
    // JAVADOC FIELD $$ field_72301_f
    private int numCleans;
    private static final String __OBFID = "CL_00000609";

    public AABBPool(int par1, int par2)
    {
        this.maxNumCleans = par1;
        this.numEntriesToRemove = par2;
    }

    // JAVADOC METHOD $$ func_72299_a
    public AxisAlignedBB getAABB(double par1, double par3, double par5, double par7, double par9, double par11)
    {
        AxisAlignedBB axisalignedbb;

        if (this.nextPoolIndex >= this.listAABB.size())
        {
            axisalignedbb = new AxisAlignedBB(par1, par3, par5, par7, par9, par11);
            this.listAABB.add(axisalignedbb);
        }
        else
        {
            axisalignedbb = (AxisAlignedBB)this.listAABB.get(this.nextPoolIndex);
            axisalignedbb.setBounds(par1, par3, par5, par7, par9, par11);
        }

        ++this.nextPoolIndex;
        return axisalignedbb;
    }

    // JAVADOC METHOD $$ func_72298_a
    public void cleanPool()
    {
        if (this.nextPoolIndex > this.maxPoolIndex)
        {
            this.maxPoolIndex = this.nextPoolIndex;
        }

        if (this.numCleans++ == this.maxNumCleans)
        {
            int i = Math.max(this.maxPoolIndex, this.listAABB.size() - this.numEntriesToRemove);

            while (this.listAABB.size() > i)
            {
                this.listAABB.remove(i);
            }

            this.maxPoolIndex = 0;
            this.numCleans = 0;
        }

        this.nextPoolIndex = 0;
    }

    // JAVADOC METHOD $$ func_72300_b
    @SideOnly(Side.CLIENT)
    public void clearPool()
    {
        this.nextPoolIndex = 0;
        this.listAABB.clear();
    }

    public int getlistAABBsize()
    {
        return this.listAABB.size();
    }

    public int getnextPoolIndex()
    {
        return this.nextPoolIndex;
    }
}