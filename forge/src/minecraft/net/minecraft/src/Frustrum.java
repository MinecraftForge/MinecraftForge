package net.minecraft.src;

public class Frustrum implements ICamera
{
    private ClippingHelper clippingHelper = ClippingHelperImpl.getInstance();
    private double xPosition;
    private double yPosition;
    private double zPosition;

    public void setPosition(double par1, double par3, double par5)
    {
        this.xPosition = par1;
        this.yPosition = par3;
        this.zPosition = par5;
    }

    /**
     * Calls the clipping helper. Returns true if the box is inside all 6 clipping planes, otherwise returns false.
     */
    public boolean isBoxInFrustum(double par1, double par3, double par5, double par7, double par9, double par11)
    {
        return this.clippingHelper.isBoxInFrustum(par1 - this.xPosition, par3 - this.yPosition, par5 - this.zPosition, par7 - this.xPosition, par9 - this.yPosition, par11 - this.zPosition);
    }

    /**
     * Returns true if the bounding box is inside all 6 clipping planes, otherwise returns false.
     */
    public boolean isBoundingBoxInFrustum(AxisAlignedBB par1AxisAlignedBB)
    {
        return this.isBoxInFrustum(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ, par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
    }
}
