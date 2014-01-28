package net.minecraft.util;

public enum EnumFacing
{
    DOWN(0, 1, 0, -1, 0),
    UP(1, 0, 0, 1, 0),
    NORTH(2, 3, 0, 0, -1),
    SOUTH(3, 2, 0, 0, 1),
    EAST(4, 5, -1, 0, 0),
    WEST(5, 4, 1, 0, 0);
    // JAVADOC FIELD $$ field_82603_g
    private final int order_a;
    // JAVADOC FIELD $$ field_82613_h
    private final int order_b;
    private final int frontOffsetX;
    private final int frontOffsetY;
    private final int frontOffsetZ;
    // JAVADOC FIELD $$ field_82609_l
    private static final EnumFacing[] faceList = new EnumFacing[6];

    private static final String __OBFID = "CL_00001201";

    private EnumFacing(int par3, int par4, int par5, int par6, int par7)
    {
        this.order_a = par3;
        this.order_b = par4;
        this.frontOffsetX = par5;
        this.frontOffsetY = par6;
        this.frontOffsetZ = par7;
    }

    // JAVADOC METHOD $$ func_82601_c
    public int getFrontOffsetX()
    {
        return this.frontOffsetX;
    }

    public int getFrontOffsetY()
    {
        return this.frontOffsetY;
    }

    // JAVADOC METHOD $$ func_82599_e
    public int getFrontOffsetZ()
    {
        return this.frontOffsetZ;
    }

    // JAVADOC METHOD $$ func_82600_a
    public static EnumFacing getFront(int par0)
    {
        return faceList[par0 % faceList.length];
    }

    static
    {
        EnumFacing[] var0 = values();
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2)
        {
            EnumFacing var3 = var0[var2];
            faceList[var3.order_a] = var3;
        }
    }
}