package net.minecraft.world;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ChunkPosition
{
    public final int field_151329_a;
    public final int field_151327_b;
    public final int field_151328_c;
    private static final String __OBFID = "CL_00000132";

    public ChunkPosition(int p_i45363_1_, int p_i45363_2_, int p_i45363_3_)
    {
        this.field_151329_a = p_i45363_1_;
        this.field_151327_b = p_i45363_2_;
        this.field_151328_c = p_i45363_3_;
    }

    public ChunkPosition(Vec3 p_i45364_1_)
    {
        this(MathHelper.floor_double(p_i45364_1_.xCoord), MathHelper.floor_double(p_i45364_1_.yCoord), MathHelper.floor_double(p_i45364_1_.zCoord));
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof ChunkPosition))
        {
            return false;
        }
        else
        {
            ChunkPosition chunkposition = (ChunkPosition)par1Obj;
            return chunkposition.field_151329_a == this.field_151329_a && chunkposition.field_151327_b == this.field_151327_b && chunkposition.field_151328_c == this.field_151328_c;
        }
    }

    public int hashCode()
    {
        return this.field_151329_a * 8976890 + this.field_151327_b * 981131 + this.field_151328_c;
    }
}