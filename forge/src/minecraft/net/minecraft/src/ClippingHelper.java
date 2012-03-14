package net.minecraft.src;

public class ClippingHelper
{
    public float[][] frustum = new float[16][16];
    public float[] projectionMatrix = new float[16];
    public float[] modelviewMatrix = new float[16];
    public float[] clippingMatrix = new float[16];

    /**
     * Returns true if the box is inside all 6 clipping planes, otherwise returns false.
     */
    public boolean isBoxInFrustum(double par1, double par3, double par5, double par7, double par9, double par11)
    {
        for (int var13 = 0; var13 < 6; ++var13)
        {
            if ((double)this.frustum[var13][0] * par1 + (double)this.frustum[var13][1] * par3 + (double)this.frustum[var13][2] * par5 + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * par7 + (double)this.frustum[var13][1] * par3 + (double)this.frustum[var13][2] * par5 + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * par1 + (double)this.frustum[var13][1] * par9 + (double)this.frustum[var13][2] * par5 + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * par7 + (double)this.frustum[var13][1] * par9 + (double)this.frustum[var13][2] * par5 + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * par1 + (double)this.frustum[var13][1] * par3 + (double)this.frustum[var13][2] * par11 + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * par7 + (double)this.frustum[var13][1] * par3 + (double)this.frustum[var13][2] * par11 + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * par1 + (double)this.frustum[var13][1] * par9 + (double)this.frustum[var13][2] * par11 + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * par7 + (double)this.frustum[var13][1] * par9 + (double)this.frustum[var13][2] * par11 + (double)this.frustum[var13][3] <= 0.0D)
            {
                return false;
            }
        }

        return true;
    }
}
