package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MouseFilter
{
    private float field_76336_a;
    private float field_76334_b;
    private float field_76335_c;
    private static final String __OBFID = "CL_00001500";

    // JAVADOC METHOD $$ func_76333_a
    public float smooth(float par1, float par2)
    {
        this.field_76336_a += par1;
        par1 = (this.field_76336_a - this.field_76334_b) * par2;
        this.field_76335_c += (par1 - this.field_76335_c) * 0.5F;

        if (par1 > 0.0F && par1 > this.field_76335_c || par1 < 0.0F && par1 < this.field_76335_c)
        {
            par1 = this.field_76335_c;
        }

        this.field_76334_b += par1;
        return par1;
    }
}