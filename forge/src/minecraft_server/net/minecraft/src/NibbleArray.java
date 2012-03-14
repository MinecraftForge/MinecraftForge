package net.minecraft.src;

public class NibbleArray
{
    /**
     * Byte array of data stored in this holder. Possibly a light map or some chunk data. Data is accessed in 4-bit
     * pieces.
     */
    public final byte[] data;

    /**
     * Log base 2 of the chunk height (128); applied as a shift on Z coordinate
     */
    private final int depthBits;

    /**
     * Log base 2 of the chunk height (128) * width (16); applied as a shift on X coordinate
     */
    private final int depthBitsPlusFour;

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

    /**
     * Returns the nibble of data corresponding to the passed in x, y, z. y is at most 6 bits, z is at most 4.
     */
    public int get(int par1, int par2, int par3)
    {
        int var4 = par2 << this.depthBitsPlusFour | par3 << this.depthBits | par1;
        int var5 = var4 >> 1;
        int var6 = var4 & 1;
        return var6 == 0 ? this.data[var5] & 15 : this.data[var5] >> 4 & 15;
    }

    /**
     * Arguments are x, y, z, val. Sets the nibble of data at x << 11 | z << 7 | y to val.
     */
    public void set(int par1, int par2, int par3, int par4)
    {
        int var5 = par2 << this.depthBitsPlusFour | par3 << this.depthBits | par1;
        int var6 = var5 >> 1;
        int var7 = var5 & 1;

        if (var7 == 0)
        {
            this.data[var6] = (byte)(this.data[var6] & 240 | par4 & 15);
        }
        else
        {
            this.data[var6] = (byte)(this.data[var6] & 15 | (par4 & 15) << 4);
        }
    }
}
