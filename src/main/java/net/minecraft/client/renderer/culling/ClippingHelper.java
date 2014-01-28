package net.minecraft.client.renderer.culling;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClippingHelper
{
    public float[][] frustum = new float[16][16];
    public float[] projectionMatrix = new float[16];
    public float[] modelviewMatrix = new float[16];
    public float[] clippingMatrix = new float[16];
    private static final String __OBFID = "CL_00000977";

    // JAVADOC METHOD $$ func_78553_b
    public boolean isBoxInFrustum(double par1, double par3, double par5, double par7, double par9, double par11)
    {
        for (int i = 0; i < 6; ++i)
        {
            if ((double)this.frustum[i][0] * par1 + (double)this.frustum[i][1] * par3 + (double)this.frustum[i][2] * par5 + (double)this.frustum[i][3] <= 0.0D && (double)this.frustum[i][0] * par7 + (double)this.frustum[i][1] * par3 + (double)this.frustum[i][2] * par5 + (double)this.frustum[i][3] <= 0.0D && (double)this.frustum[i][0] * par1 + (double)this.frustum[i][1] * par9 + (double)this.frustum[i][2] * par5 + (double)this.frustum[i][3] <= 0.0D && (double)this.frustum[i][0] * par7 + (double)this.frustum[i][1] * par9 + (double)this.frustum[i][2] * par5 + (double)this.frustum[i][3] <= 0.0D && (double)this.frustum[i][0] * par1 + (double)this.frustum[i][1] * par3 + (double)this.frustum[i][2] * par11 + (double)this.frustum[i][3] <= 0.0D && (double)this.frustum[i][0] * par7 + (double)this.frustum[i][1] * par3 + (double)this.frustum[i][2] * par11 + (double)this.frustum[i][3] <= 0.0D && (double)this.frustum[i][0] * par1 + (double)this.frustum[i][1] * par9 + (double)this.frustum[i][2] * par11 + (double)this.frustum[i][3] <= 0.0D && (double)this.frustum[i][0] * par7 + (double)this.frustum[i][1] * par9 + (double)this.frustum[i][2] * par11 + (double)this.frustum[i][3] <= 0.0D)
            {
                return false;
            }
        }

        return true;
    }
}