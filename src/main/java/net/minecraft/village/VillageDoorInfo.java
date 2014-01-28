package net.minecraft.village;

public class VillageDoorInfo
{
    public final int posX;
    public final int posY;
    public final int posZ;
    public final int insideDirectionX;
    public final int insideDirectionZ;
    public int lastActivityTimestamp;
    public boolean isDetachedFromVillageFlag;
    private int doorOpeningRestrictionCounter;
    private static final String __OBFID = "CL_00001630";

    public VillageDoorInfo(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        this.posX = par1;
        this.posY = par2;
        this.posZ = par3;
        this.insideDirectionX = par4;
        this.insideDirectionZ = par5;
        this.lastActivityTimestamp = par6;
    }

    // JAVADOC METHOD $$ func_75474_b
    public int getDistanceSquared(int par1, int par2, int par3)
    {
        int l = par1 - this.posX;
        int i1 = par2 - this.posY;
        int j1 = par3 - this.posZ;
        return l * l + i1 * i1 + j1 * j1;
    }

    // JAVADOC METHOD $$ func_75469_c
    public int getInsideDistanceSquare(int par1, int par2, int par3)
    {
        int l = par1 - this.posX - this.insideDirectionX;
        int i1 = par2 - this.posY;
        int j1 = par3 - this.posZ - this.insideDirectionZ;
        return l * l + i1 * i1 + j1 * j1;
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
        int k = par1 - this.posX;
        int l = par2 - this.posZ;
        return k * this.insideDirectionX + l * this.insideDirectionZ >= 0;
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