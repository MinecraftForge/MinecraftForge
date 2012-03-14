package net.minecraft.src;

public class Facing
{
    /** Converts a face to a side. */
    public static final int[] faceToSide = new int[] {1, 0, 3, 2, 5, 4};

    /**
     * gives the offset required for this axis to get the block at that side.
     */
    public static final int[] offsetsXForSide = new int[] {0, 0, 0, 0, -1, 1};

    /**
     * gives the offset required for this axis to get the block at that side.
     */
    public static final int[] offsetsYForSide = new int[] { -1, 1, 0, 0, 0, 0};

    /**
     * gives the offset required for this axis to get the block at that side.
     */
    public static final int[] offsetsZForSide = new int[] {0, 0, -1, 1, 0, 0};
}
