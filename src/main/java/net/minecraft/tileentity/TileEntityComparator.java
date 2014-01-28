package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityComparator extends TileEntity
{
    private int field_145997_a;
    private static final String __OBFID = "CL_00000349";

    public void func_145841_b(NBTTagCompound p_145841_1_)
    {
        super.func_145841_b(p_145841_1_);
        p_145841_1_.setInteger("OutputSignal", this.field_145997_a);
    }

    public void func_145839_a(NBTTagCompound p_145839_1_)
    {
        super.func_145839_a(p_145839_1_);
        this.field_145997_a = p_145839_1_.getInteger("OutputSignal");
    }

    public int func_145996_a()
    {
        return this.field_145997_a;
    }

    public void func_145995_a(int p_145995_1_)
    {
        this.field_145997_a = p_145995_1_;
    }
}