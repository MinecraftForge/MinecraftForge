package net.minecraft.src;

public class Direction
{
    public static final int[] offsetX = new int[] {0, -1, 0, 1};
    public static final int[] offsetZ = new int[] {1, 0, -1, 0};
    public static final int[] headInvisibleFace = new int[] {3, 4, 2, 5};
    public static final int[] vineGrowth = new int[] { -1, -1, 2, 0, 1, 3};
    public static final int[] footInvisibleFaceRemap = new int[] {2, 3, 0, 1};
    public static final int[] enderEyeMetaToDirection = new int[] {1, 2, 3, 0};
    public static final int[] field_35868_g = new int[] {3, 0, 1, 2};
    public static final int[][] bedDirection = new int[][] {{1, 0, 3, 2, 5, 4}, {1, 0, 5, 4, 2, 3}, {1, 0, 2, 3, 4, 5}, {1, 0, 4, 5, 3, 2}};
}
