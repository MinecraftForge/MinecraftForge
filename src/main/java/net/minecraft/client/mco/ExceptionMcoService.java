package net.minecraft.client.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExceptionMcoService extends Exception
{
    public final int field_148831_a;
    public final String field_148829_b;
    public final int field_148830_c;
    private static final String __OBFID = "CL_00001177";

    public ExceptionMcoService(int par1, String par2Str, int par3)
    {
        super(par2Str);
        this.field_148831_a = par1;
        this.field_148829_b = par2Str;
        this.field_148830_c = par3;
    }

    public String toString()
    {
        return this.field_148830_c != -1 ? "Realms ( ErrorCode: " + this.field_148830_c + " )" : "Realms: " + this.field_148829_b;
    }
}