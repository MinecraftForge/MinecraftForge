package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

public class ComponentStrongholdStairs2 extends ComponentStrongholdStairs
{
    public StructureStrongholdPieceWeight field_35329_a;
    public ComponentStrongholdPortalRoom field_40317_b;
    public ArrayList field_35328_b = new ArrayList();

    public ComponentStrongholdStairs2(int par1, Random par2Random, int par3, int par4)
    {
        super(0, par2Random, par3, par4);
    }

    public ChunkPosition getCenter()
    {
        return this.field_40317_b != null ? this.field_40317_b.getCenter() : super.getCenter();
    }
}
