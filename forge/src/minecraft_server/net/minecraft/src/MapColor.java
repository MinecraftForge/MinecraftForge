package net.minecraft.src;

public class MapColor
{
    public static final MapColor[] mapColorArray = new MapColor[16];
    public static final MapColor airColor = new MapColor(0, 0);
    public static final MapColor grassColor = new MapColor(1, 8368696);
    public static final MapColor sandColor = new MapColor(2, 16247203);
    public static final MapColor clothColor = new MapColor(3, 10987431);
    public static final MapColor tntColor = new MapColor(4, 16711680);
    public static final MapColor iceColor = new MapColor(5, 10526975);
    public static final MapColor ironColor = new MapColor(6, 10987431);
    public static final MapColor foliageColor = new MapColor(7, 31744);
    public static final MapColor snowColor = new MapColor(8, 16777215);
    public static final MapColor clayColor = new MapColor(9, 10791096);
    public static final MapColor dirtColor = new MapColor(10, 12020271);
    public static final MapColor stoneColor = new MapColor(11, 7368816);
    public static final MapColor waterColor = new MapColor(12, 4210943);
    public static final MapColor woodColor = new MapColor(13, 6837042);
    public final int colorValue;
    public final int colorIndex;

    private MapColor(int par1, int par2)
    {
        this.colorIndex = par1;
        this.colorValue = par2;
        mapColorArray[par1] = this;
    }
}
