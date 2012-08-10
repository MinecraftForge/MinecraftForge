package net.minecraftforge.common;

public enum Orientation
{
    /** -Y */
    DOWN, 
    
    /** +Y */
    UP,
    
    /** -Z */
    NORTH,
    
    /** +Z */
    SOUTH,
    
    /** -X */
    WEST,
    
    /** +X */
    EAST,
    
    /** 
     * Used only by getOrientation, for invalid inputs
     */
    UNKNOWN;
    
    public static Orientation getOrientation(int id)
    {
        if (id < Orientation.values().length)
        {
            return Orientation.values()[id];
        }
        return UNKNOWN;
    }
}
