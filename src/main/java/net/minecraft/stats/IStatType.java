package net.minecraft.stats;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IStatType
{
    // JAVADOC METHOD $$ func_75843_a
    @SideOnly(Side.CLIENT)
    String format(int var1);
}