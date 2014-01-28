package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPlayerInfo
{
    // JAVADOC FIELD $$ field_78831_a
    public final String name;
    // JAVADOC FIELD $$ field_78830_c
    private final String nameinLowerCase;
    // JAVADOC FIELD $$ field_78829_b
    public int responseTime;
    private static final String __OBFID = "CL_00000888";

    public GuiPlayerInfo(String par1Str)
    {
        this.name = par1Str;
        this.nameinLowerCase = par1Str.toLowerCase();
    }
}