package net.minecraft.src;

public abstract class BlockDirectional extends Block
{
    protected BlockDirectional(int par1, int par2, Material par3Material)
    {
        super(par1, par2, par3Material);
    }

    protected BlockDirectional(int par1, Material par2Material)
    {
        super(par1, par2Material);
    }

    public static int func_48216_a(int par0)
    {
        return par0 & 3;
    }
}
