package net.minecraft.src;

class StructureNetherBridgePieceWeight
{
    public Class field_40655_a;
    public final int field_40653_b;
    public int field_40654_c;
    public int field_40651_d;
    public boolean field_40652_e;

    public StructureNetherBridgePieceWeight(Class par1Class, int par2, int par3, boolean par4)
    {
        this.field_40655_a = par1Class;
        this.field_40653_b = par2;
        this.field_40651_d = par3;
        this.field_40652_e = par4;
    }

    public StructureNetherBridgePieceWeight(Class par1Class, int par2, int par3)
    {
        this(par1Class, par2, par3, false);
    }

    public boolean func_40649_a(int par1)
    {
        return this.field_40651_d == 0 || this.field_40654_c < this.field_40651_d;
    }

    public boolean func_40650_a()
    {
        return this.field_40651_d == 0 || this.field_40654_c < this.field_40651_d;
    }
}
