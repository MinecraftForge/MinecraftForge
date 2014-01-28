package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.Stitcher;

@SideOnly(Side.CLIENT)
public class StitcherException extends RuntimeException
{
    private final Stitcher.Holder field_98149_a;
    private static final String __OBFID = "CL_00001057";

    public StitcherException(Stitcher.Holder par1StitchHolder, String par2Str)
    {
        super(par2Str);
        this.field_98149_a = par1StitchHolder;
    }
}