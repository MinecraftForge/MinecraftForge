package net.minecraft.client.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExceptionMcoHttp extends RuntimeException
{
    private static final String __OBFID = "CL_00001176";

    public ExceptionMcoHttp(String par1Str, Exception par2Exception)
    {
        super(par1Str, par2Exception);
    }
}