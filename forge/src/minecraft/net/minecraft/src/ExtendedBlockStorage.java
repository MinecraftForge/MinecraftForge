package net.minecraft.src;

public class ExtendedBlockStorage
{
    private int field_48722_a;

    /**
     * A total count of the number of non-air blocks in this block storage's Chunk.
     */
    private int blockRefCount;

    /**
     * Contains the number of blocks in this block storage's parent chunk that require random ticking. Used to cull the
     * Chunk from random tick updates for performance reasons.
     */
    private int tickRefCount;

    /**
     * Contains the least significant 8 bits of each block ID belonging to this block storage's parent Chunk.
     */
    private byte[] blockLSBArray;

    /**
     * Contains the most significant 4 bits of each block ID belonging to this block storage's parent Chunk.
     */
    private NibbleArray blockMSBArray;
    private NibbleArray field_48716_f;

    /** The NibbleArray containing a block of Block-light data. */
    private NibbleArray blocklightArray;

    /** The NibbleArray containing a block of Sky-light data. */
    private NibbleArray skylightArray;

    public ExtendedBlockStorage(int par1)
    {
        this.field_48722_a = par1;
        this.blockLSBArray = new byte[4096];
        this.field_48716_f = new NibbleArray(this.blockLSBArray.length, 4);
        this.skylightArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.blocklightArray = new NibbleArray(this.blockLSBArray.length, 4);
    }

    /**
     * Returns the extended block ID for a location in a chunk, merged from a byte array and a NibbleArray to form a
     * full 12-bit block ID.
     */
    public int getExtBlockID(int par1, int par2, int par3)
    {
        int var4 = this.blockLSBArray[par2 << 8 | par3 << 4 | par1] & 255;
        return this.blockMSBArray != null ? this.blockMSBArray.get(par1, par2, par3) << 8 | var4 : var4;
    }

    /**
     * Sets the extended block ID for a location in a chunk, splitting bits 11..8 into a NibbleArray and bits 7..0 into
     * a byte array. Also performs reference counting to determine whether or not to broadly cull this Chunk from the
     * random-update tick list.
     */
    public void setExtBlockID(int par1, int par2, int par3, int par4)
    {
        int var5 = this.blockLSBArray[par2 << 8 | par3 << 4 | par1] & 255;

        if (this.blockMSBArray != null)
        {
            var5 |= this.blockMSBArray.get(par1, par2, par3) << 8;
        }

        if (var5 == 0 && par4 != 0)
        {
            ++this.blockRefCount;

            if (Block.blocksList[par4] != null && Block.blocksList[par4].getTickRandomly())
            {
                ++this.tickRefCount;
            }
        }
        else if (var5 != 0 && par4 == 0)
        {
            --this.blockRefCount;

            if (Block.blocksList[var5] != null && Block.blocksList[var5].getTickRandomly())
            {
                --this.tickRefCount;
            }
        }
        else if (Block.blocksList[var5] != null && Block.blocksList[var5].getTickRandomly() && (Block.blocksList[par4] == null || !Block.blocksList[par4].getTickRandomly()))
        {
            --this.tickRefCount;
        }
        else if ((Block.blocksList[var5] == null || !Block.blocksList[var5].getTickRandomly()) && Block.blocksList[par4] != null && Block.blocksList[par4].getTickRandomly())
        {
            ++this.tickRefCount;
        }

        this.blockLSBArray[par2 << 8 | par3 << 4 | par1] = (byte)(par4 & 255);

        if (par4 > 255)
        {
            if (this.blockMSBArray == null)
            {
                this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);
            }

            this.blockMSBArray.set(par1, par2, par3, (par4 & 3840) >> 8);
        }
        else if (this.blockMSBArray != null)
        {
            this.blockMSBArray.set(par1, par2, par3, 0);
        }
    }

    public int func_48694_b(int par1, int par2, int par3)
    {
        return this.field_48716_f.get(par1, par2, par3);
    }

    public void func_48690_b(int par1, int par2, int par3, int par4)
    {
        this.field_48716_f.set(par1, par2, par3, par4);
    }

    /**
     * Returns whether or not this block storage's Chunk is fully empty, based on its internal reference count.
     */
    public boolean getIsEmpty()
    {
        return this.blockRefCount == 0;
    }

    /**
     * Returns whether or not this block storage's Chunk will require random ticking, used to avoid looping through
     * random block ticks when there are no blocks that would randomly tick.
     */
    public boolean getNeedsRandomTick()
    {
        return this.tickRefCount > 0;
    }

    public int func_48707_c()
    {
        return this.field_48722_a;
    }

    /**
     * Sets the saved Sky-light value in the extended block storage structure.
     */
    public void setExtSkylightValue(int par1, int par2, int par3, int par4)
    {
        this.skylightArray.set(par1, par2, par3, par4);
    }

    /**
     * Gets the saved Sky-light value in the extended block storage structure.
     */
    public int getExtSkylightValue(int par1, int par2, int par3)
    {
        return this.skylightArray.get(par1, par2, par3);
    }

    /**
     * Sets the saved Block-light value in the extended block storage structure.
     */
    public void setExtBlocklightValue(int par1, int par2, int par3, int par4)
    {
        this.blocklightArray.set(par1, par2, par3, par4);
    }

    /**
     * Gets the saved Block-light value in the extended block storage structure.
     */
    public int getExtBlocklightValue(int par1, int par2, int par3)
    {
        return this.blocklightArray.get(par1, par2, par3);
    }

    public void func_48708_d()
    {
        this.blockRefCount = 0;
        this.tickRefCount = 0;

        for (int var1 = 0; var1 < 16; ++var1)
        {
            for (int var2 = 0; var2 < 16; ++var2)
            {
                for (int var3 = 0; var3 < 16; ++var3)
                {
                    int var4 = this.getExtBlockID(var1, var2, var3);

                    if (var4 > 0)
                    {
                        if (Block.blocksList[var4] == null)
                        {
                            this.blockLSBArray[var2 << 8 | var3 << 4 | var1] = 0;

                            if (this.blockMSBArray != null)
                            {
                                this.blockMSBArray.set(var1, var2, var3, 0);
                            }
                        }
                        else
                        {
                            ++this.blockRefCount;

                            if (Block.blocksList[var4].getTickRandomly())
                            {
                                ++this.tickRefCount;
                            }
                        }
                    }
                }
            }
        }
    }

    public void func_48711_e() {}

    public int func_48700_f()
    {
        return this.blockRefCount;
    }

    public byte[] func_48692_g()
    {
        return this.blockLSBArray;
    }

    public void func_48715_h()
    {
        this.blockMSBArray = null;
    }

    /**
     * Returns the block ID MSB (bits 11..8) array for this storage array's Chunk.
     */
    public NibbleArray getBlockMSBArray()
    {
        return this.blockMSBArray;
    }

    public NibbleArray func_48697_j()
    {
        return this.field_48716_f;
    }

    /**
     * Returns the NibbleArray instance containing Block-light data.
     */
    public NibbleArray getBlocklightArray()
    {
        return this.blocklightArray;
    }

    /**
     * Returns the NibbleArray instance containing Sky-light data.
     */
    public NibbleArray getSkylightArray()
    {
        return this.skylightArray;
    }

    public void func_48706_a(byte[] par1ArrayOfByte)
    {
        this.blockLSBArray = par1ArrayOfByte;
    }

    public void func_48710_a(NibbleArray par1NibbleArray)
    {
        this.blockMSBArray = par1NibbleArray;
    }

    public void func_48701_b(NibbleArray par1NibbleArray)
    {
        this.field_48716_f = par1NibbleArray;
    }

    /**
     * Sets the NibbleArray instance used for Block-light values in this particular storage block.
     */
    public void setBlocklightArray(NibbleArray par1NibbleArray)
    {
        this.blocklightArray = par1NibbleArray;
    }

    /**
     * Sets the NibbleArray instance used for Sky-light values in this particular storage block.
     */
    public void setSkylightArray(NibbleArray par1NibbleArray)
    {
        this.skylightArray = par1NibbleArray;
    }

    /**
     * Called by a Chunk to initialize the MSB array if getBlockMSBArray returns null. Returns the newly-created
     * NibbleArray instance.
     */
    public NibbleArray createBlockMSBArray()
    {
        this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);
        return this.blockMSBArray;
    }
}
