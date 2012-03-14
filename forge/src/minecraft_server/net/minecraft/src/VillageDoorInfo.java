package net.minecraft.src;

public class VillageDoorInfo
{
    public final int posX;
    public final int posY;
    public final int posZ;
    public final int insideDirectionX;
    public final int insideDirectionZ;
    public int lastActivityTimestamp;
    public boolean isDetachedFromVillageFlag = false;
    private int doorOpeningRestrictionCounter = 0;

    public VillageDoorInfo(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        this.posX = par1;
        this.posY = par2;
        this.posZ = par3;
        this.insideDirectionX = par4;
        this.insideDirectionZ = par5;
        this.lastActivityTimestamp = par6;
    }

    /**
     * Returns the squared distance between this door and the given coordinate.
     */
    public int getDistanceSquared(int par1, int par2, int par3)
    {
        int var4 = par1 - this.posX;
        int var5 = par2 - this.posY;
        int var6 = par3 - this.posZ;
        return var4 * var4 + var5 * var5 + var6 * var6;
    }

    /**
     * Get the square of the distance from a location 2 blocks away from the door considered 'inside' and the given
     * arguments
     */
    public int getInsideDistanceSquare(int par1, int par2, int par3)
    {
        int var4 = par1 - this.posX - this.insideDirectionX;
        int var5 = par2 - this.posY;
        int var6 = par3 - this.posZ - this.insideDirectionZ;
        return var4 * var4 + var5 * var5 + var6 * var6;
    }

    public int getInsidePosX()
    {
        return this.posX + this.insideDirectionX;
    }

    public int getInsidePosY()
    {
        return this.posY;
    }

    public int getInsidePosZ()
    {
        return this.posZ + this.insideDirectionZ;
    }

    public boolean isInside(int par1, int par2)
    {
        int var3 = par1 - this.posX;
        int var4 = par2 - this.posZ;
        return var3 * this.insideDirectionX + var4 * this.insideDirectionZ >= 0;
    }

    public void resetDoorOpeningRestrictionCounter()
    {
        this.doorOpeningRestrictionCounter = 0;
    }

    public void incrementDoorOpeningRestrictionCounter()
    {
        ++this.doorOpeningRestrictionCounter;
    }

    public int getDoorOpeningRestrictionCounter()
    {
        return this.doorOpeningRestrictionCounter;
    }
}
