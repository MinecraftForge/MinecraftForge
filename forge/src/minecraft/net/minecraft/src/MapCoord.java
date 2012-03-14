package net.minecraft.src;

public class MapCoord
{
    public byte field_28217_a;
    public byte centerX;
    public byte centerZ;
    public byte iconRotation;

    final MapData data;

    public MapCoord(MapData par1MapData, byte par2, byte par3, byte par4, byte par5)
    {
        this.data = par1MapData;
        this.field_28217_a = par2;
        this.centerX = par3;
        this.centerZ = par4;
        this.iconRotation = par5;
    }
}
