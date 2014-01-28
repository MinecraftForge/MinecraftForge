package net.minecraft.world.chunk;

public class NibbleArray
{
    // JAVADOC FIELD $$ field_76585_a
    public final byte[] data;
    // JAVADOC FIELD $$ field_76583_b
    private final int depthBits;
    // JAVADOC FIELD $$ field_76584_c
    private final int depthBitsPlusFour;
    private static final String __OBFID = "CL_00000371";

    public NibbleArray(int par1, int par2)
    {
        this.data = new byte[par1 >> 1];
        this.depthBits = par2;
        this.depthBitsPlusFour = par2 + 4;
    }

    public NibbleArray(byte[] par1ArrayOfByte, int par2)
    {
        this.data = par1ArrayOfByte;
        this.depthBits = par2;
        this.depthBitsPlusFour = par2 + 4;
    }

    // JAVADOC METHOD $$ func_76582_a
    public int get(int par1, int par2, int par3)
    {
        int l = par2 << this.depthBitsPlusFour | par3 << this.depthBits | par1;
        int i1 = l >> 1;
        int j1 = l & 1;
        return j1 == 0 ? this.data[i1] & 15 : this.data[i1] >> 4 & 15;
    }

    // JAVADOC METHOD $$ func_76581_a
    public void set(int par1, int par2, int par3, int par4)
    {
        int i1 = par2 << this.depthBitsPlusFour | par3 << this.depthBits | par1;
        int j1 = i1 >> 1;
        int k1 = i1 & 1;

        if (k1 == 0)
        {
            this.data[j1] = (byte)(this.data[j1] & 240 | par4 & 15);
        }
        else
        {
            this.data[j1] = (byte)(this.data[j1] & 15 | (par4 & 15) << 4);
        }
    }
}