package net.minecraftforge.client;

import java.util.BitSet;

public class StencilBitPool {
    private BitSet pool = new BitSet(0);
    private int count;

    void reset(int bitCount) {
        count = bitCount;
        pool = new BitSet(bitCount);
        pool.set(0, bitCount);
    }

    public int getBitPoolSize() {
        return count;
    }

    /**
     * Reserve a stencil bit for use in rendering
     *
     * @return A bit or -1 if no further stencil bits are available
     */
    public int reserveStencilBit()
    {
        int bit = pool.nextSetBit(0);
        if (bit >= 0)
        {
            pool.clear(bit);
        }
        return bit;
    }

    /**
     * Release the stencil bit for other use
     *
     * @param bit The bit from {@link #reserveStencilBit()}
     */
    public void releaseStencilBit(int bit)
    {
        if (bit >= 0 && bit < count)
        {
            pool.set(bit);
        }
    }
}
