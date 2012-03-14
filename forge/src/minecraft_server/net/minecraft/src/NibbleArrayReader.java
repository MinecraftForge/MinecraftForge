package net.minecraft.src;

public class NibbleArrayReader
{
    public final byte[] data;
    private final int depthBits;
    private final int depthBitsPlusFour;

    public NibbleArrayReader(byte[] par1ArrayOfByte, int par2)
    {
        this.data = par1ArrayOfByte;
        this.depthBits = par2;
        this.depthBitsPlusFour = par2 + 4;
    }

    public int get(int par1, int par2, int par3)
    {
        int var4 = par1 << this.depthBitsPlusFour | par3 << this.depthBits | par2;
        int var5 = var4 >> 1;
        int var6 = var4 & 1;
        return var6 == 0 ? this.data[var5] & 15 : this.data[var5] >> 4 & 15;
    }
}
