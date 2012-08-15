package net.minecraftforge.common;

public enum ForgeDirection
{
    /** -Y */
    DOWN(0, -1, 0), 
    
    /** +Y */
    UP(0, 1, 0),
    
    /** -Z */
    NORTH(0, 0, -1),
    
    /** +Z */
    SOUTH(0, 0, 1),
    
    /** -X */
    WEST(-1, 0, 0),
    
    /** +X */
    EAST(1, 0, 0),
    
    /** 
     * Used only by getOrientation, for invalid inputs
     */
    UNKNOWN(0, 0, 0);

    public final int offsetX;
    public final int offsetY;
    public final int offsetZ;
    public final int flag;
    
    private ForgeDirection(int x, int y, int z)
    {
        offsetX = x;
        offsetY = y;
        offsetZ = z;
        flag = 1 << ordinal();
    }
    
    public static ForgeDirection getOrientation(int id)
    {
        if (id >= 0 && id < ForgeDirection.values().length)
        {
            return ForgeDirection.values()[id];
        }
        return UNKNOWN;
    }

    public static final int[] opposite = new int[] { 1, 0,  3, 2,  5, 4, 6};

    public ForgeDirection getOpposite()
    {
        return getOrientation(opposite[ordinal()]);
    }
}
