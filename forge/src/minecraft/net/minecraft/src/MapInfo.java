package net.minecraft.src;

public class MapInfo
{
    /** Reference for EntityPlayer object in MapInfo */
    public final EntityPlayer entityplayerObj;
    public int[] field_28119_b;
    public int[] field_28124_c;
    private int field_28122_e;
    private int field_28121_f;

    /** reference in MapInfo to MapData object */
    final MapData mapDataObj;

    public MapInfo(MapData par1MapData, EntityPlayer par2EntityPlayer)
    {
        this.mapDataObj = par1MapData;
        this.field_28119_b = new int[128];
        this.field_28124_c = new int[128];
        this.field_28122_e = 0;
        this.field_28121_f = 0;
        this.entityplayerObj = par2EntityPlayer;

        for (int var3 = 0; var3 < this.field_28119_b.length; ++var3)
        {
            this.field_28119_b[var3] = 0;
            this.field_28124_c[var3] = 127;
        }
    }
}
