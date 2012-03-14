package net.minecraft.src;

class StructureNetherBridgePieceWeight
{
    public Class field_40699_a;
    public final int field_40697_b;
    public int field_40698_c;
    public int field_40695_d;
    public boolean field_40696_e;

    public StructureNetherBridgePieceWeight(Class par1Class, int par2, int par3, boolean par4)
    {
        this.field_40699_a = par1Class;
        this.field_40697_b = par2;
        this.field_40695_d = par3;
        this.field_40696_e = par4;
    }

    public StructureNetherBridgePieceWeight(Class par1Class, int par2, int par3)
    {
        this(par1Class, par2, par3, false);
    }

    public boolean func_40693_a(int par1)
    {
        return this.field_40695_d == 0 || this.field_40698_c < this.field_40695_d;
    }

    public boolean func_40694_a()
    {
        return this.field_40695_d == 0 || this.field_40698_c < this.field_40695_d;
    }
}
