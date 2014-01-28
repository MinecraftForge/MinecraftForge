package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IIcon
{
    // JAVADOC METHOD $$ func_94211_a
    @SideOnly(Side.CLIENT)
    int getIconWidth();

    // JAVADOC METHOD $$ func_94216_b
    @SideOnly(Side.CLIENT)
    int getIconHeight();

    // JAVADOC METHOD $$ func_94209_e
    @SideOnly(Side.CLIENT)
    float getMinU();

    // JAVADOC METHOD $$ func_94212_f
    @SideOnly(Side.CLIENT)
    float getMaxU();

    // JAVADOC METHOD $$ func_94214_a
    @SideOnly(Side.CLIENT)
    float getInterpolatedU(double var1);

    // JAVADOC METHOD $$ func_94206_g
    @SideOnly(Side.CLIENT)
    float getMinV();

    // JAVADOC METHOD $$ func_94210_h
    @SideOnly(Side.CLIENT)
    float getMaxV();

    // JAVADOC METHOD $$ func_94207_b
    @SideOnly(Side.CLIENT)
    float getInterpolatedV(double var1);

    @SideOnly(Side.CLIENT)
    String getIconName();
}