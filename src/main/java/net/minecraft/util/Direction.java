package net.minecraft.util;

public class Direction
{
    public static final int[] offsetX = new int[] {0, -1, 0, 1};
    public static final int[] offsetZ = new int[] {1, 0, -1, 0};
    public static final String[] directions = new String[] {"SOUTH", "WEST", "NORTH", "EAST"};
    // JAVADOC FIELD $$ field_71582_c
    public static final int[] directionToFacing = new int[] {3, 4, 2, 5};
    // JAVADOC FIELD $$ field_71579_d
    public static final int[] facingToDirection = new int[] { -1, -1, 2, 0, 1, 3};
    // JAVADOC FIELD $$ field_71580_e
    public static final int[] rotateOpposite = new int[] {2, 3, 0, 1};
    // JAVADOC FIELD $$ field_71577_f
    public static final int[] rotateRight = new int[] {1, 2, 3, 0};
    // JAVADOC FIELD $$ field_71578_g
    public static final int[] rotateLeft = new int[] {3, 0, 1, 2};
    public static final int[][] bedDirection = new int[][] {{1, 0, 3, 2, 5, 4}, {1, 0, 5, 4, 2, 3}, {1, 0, 2, 3, 4, 5}, {1, 0, 4, 5, 3, 2}};
    private static final String __OBFID = "CL_00001506";

    // JAVADOC METHOD $$ func_82372_a
    public static int getMovementDirection(double par0, double par2)
    {
        return MathHelper.abs((float)par0) > MathHelper.abs((float)par2) ? (par0 > 0.0D ? 1 : 3) : (par2 > 0.0D ? 2 : 0);
    }
}