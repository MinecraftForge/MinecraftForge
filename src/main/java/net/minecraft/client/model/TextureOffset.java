package net.minecraft.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureOffset
{
    // JAVADOC FIELD $$ field_78783_a
    public final int textureOffsetX;
    // JAVADOC FIELD $$ field_78782_b
    public final int textureOffsetY;
    private static final String __OBFID = "CL_00000875";

    public TextureOffset(int par1, int par2)
    {
        this.textureOffsetX = par1;
        this.textureOffsetY = par2;
    }
}