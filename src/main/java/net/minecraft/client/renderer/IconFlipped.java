package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public class IconFlipped implements IIcon
{
    private final IIcon baseIcon;
    private final boolean flipU;
    private final boolean flipV;
    private static final String __OBFID = "CL_00001511";

    public IconFlipped(IIcon par1Icon, boolean par2, boolean par3)
    {
        this.baseIcon = par1Icon;
        this.flipU = par2;
        this.flipV = par3;
    }

    // JAVADOC METHOD $$ func_94211_a
    public int getIconWidth()
    {
        return this.baseIcon.getIconWidth();
    }

    // JAVADOC METHOD $$ func_94216_b
    public int getIconHeight()
    {
        return this.baseIcon.getIconHeight();
    }

    // JAVADOC METHOD $$ func_94209_e
    public float getMinU()
    {
        return this.flipU ? this.baseIcon.getMaxU() : this.baseIcon.getMinU();
    }

    // JAVADOC METHOD $$ func_94212_f
    public float getMaxU()
    {
        return this.flipU ? this.baseIcon.getMinU() : this.baseIcon.getMaxU();
    }

    // JAVADOC METHOD $$ func_94214_a
    public float getInterpolatedU(double par1)
    {
        float f = this.getMaxU() - this.getMinU();
        return this.getMinU() + f * ((float)par1 / 16.0F);
    }

    // JAVADOC METHOD $$ func_94206_g
    public float getMinV()
    {
        return this.flipV ? this.baseIcon.getMinV() : this.baseIcon.getMinV();
    }

    // JAVADOC METHOD $$ func_94210_h
    public float getMaxV()
    {
        return this.flipV ? this.baseIcon.getMinV() : this.baseIcon.getMaxV();
    }

    // JAVADOC METHOD $$ func_94207_b
    public float getInterpolatedV(double par1)
    {
        float f = this.getMaxV() - this.getMinV();
        return this.getMinV() + f * ((float)par1 / 16.0F);
    }

    public String getIconName()
    {
        return this.baseIcon.getIconName();
    }
}