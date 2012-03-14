package net.minecraft.src;

public interface ICamera
{
    /**
     * Returns true if the bounding box is inside all 6 clipping planes, otherwise returns false.
     */
    boolean isBoundingBoxInFrustum(AxisAlignedBB var1);

    void setPosition(double var1, double var3, double var5);
}
