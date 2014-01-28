package net.minecraft.world.gen.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class MapGenStructureData extends WorldSavedData
{
    private NBTTagCompound field_143044_a = new NBTTagCompound();
    private static final String __OBFID = "CL_00000510";

    public MapGenStructureData(String par1Str)
    {
        super(par1Str);
    }

    // JAVADOC METHOD $$ func_76184_a
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.field_143044_a = par1NBTTagCompound.getCompoundTag("Features");
    }

    // JAVADOC METHOD $$ func_76187_b
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setTag("Features", this.field_143044_a);
    }

    public void func_143043_a(NBTTagCompound par1NBTTagCompound, int par2, int par3)
    {
        this.field_143044_a.setTag(func_143042_b(par2, par3), par1NBTTagCompound);
    }

    public static String func_143042_b(int par1, int par2)
    {
        return "[" + par1 + "," + par2 + "]";
    }

    public NBTTagCompound func_143041_a()
    {
        return this.field_143044_a;
    }
}