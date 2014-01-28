package net.minecraft.world.chunk.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.NibbleArray;

public class ExtendedBlockStorage
{
    // JAVADOC FIELD $$ field_76684_a
    private int yBase;
    // JAVADOC FIELD $$ field_76682_b
    private int blockRefCount;
    // JAVADOC FIELD $$ field_76683_c
    private int tickRefCount;
    // JAVADOC FIELD $$ field_76680_d
    private byte[] blockLSBArray;
    // JAVADOC FIELD $$ field_76681_e
    private NibbleArray blockMSBArray;
    // JAVADOC FIELD $$ field_76678_f
    private NibbleArray blockMetadataArray;
    // JAVADOC FIELD $$ field_76679_g
    private NibbleArray blocklightArray;
    // JAVADOC FIELD $$ field_76685_h
    private NibbleArray skylightArray;
    private static final String __OBFID = "CL_00000375";

    public ExtendedBlockStorage(int par1, boolean par2)
    {
        this.yBase = par1;
        this.blockLSBArray = new byte[4096];
        this.blockMetadataArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.blocklightArray = new NibbleArray(this.blockLSBArray.length, 4);

        if (par2)
        {
            this.skylightArray = new NibbleArray(this.blockLSBArray.length, 4);
        }
    }

    public Block func_150819_a(int p_150819_1_, int p_150819_2_, int p_150819_3_)
    {
        int l = this.blockLSBArray[p_150819_2_ << 8 | p_150819_3_ << 4 | p_150819_1_] & 255;

        if (this.blockMSBArray != null)
        {
            l |= this.blockMSBArray.get(p_150819_1_, p_150819_2_, p_150819_3_) << 8;
        }

        return Block.func_149729_e(l);
    }

    public void func_150818_a(int p_150818_1_, int p_150818_2_, int p_150818_3_, Block p_150818_4_)
    {
        int l = this.blockLSBArray[p_150818_2_ << 8 | p_150818_3_ << 4 | p_150818_1_] & 255;

        if (this.blockMSBArray != null)
        {
            l |= this.blockMSBArray.get(p_150818_1_, p_150818_2_, p_150818_3_) << 8;
        }

        Block block1 = Block.func_149729_e(l);

        if (block1 != Blocks.air)
        {
            --this.blockRefCount;

            if (block1.func_149653_t())
            {
                --this.tickRefCount;
            }
        }

        if (p_150818_4_ != Blocks.air)
        {
            ++this.blockRefCount;

            if (p_150818_4_.func_149653_t())
            {
                ++this.tickRefCount;
            }
        }

        int i1 = Block.func_149682_b(p_150818_4_);
        this.blockLSBArray[p_150818_2_ << 8 | p_150818_3_ << 4 | p_150818_1_] = (byte)(i1 & 255);

        if (i1 > 255)
        {
            if (this.blockMSBArray == null)
            {
                this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);
            }

            this.blockMSBArray.set(p_150818_1_, p_150818_2_, p_150818_3_, (i1 & 3840) >> 8);
        }
        else if (this.blockMSBArray != null)
        {
            this.blockMSBArray.set(p_150818_1_, p_150818_2_, p_150818_3_, 0);
        }
    }

    // JAVADOC METHOD $$ func_76665_b
    public int getExtBlockMetadata(int par1, int par2, int par3)
    {
        return this.blockMetadataArray.get(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_76654_b
    public void setExtBlockMetadata(int par1, int par2, int par3, int par4)
    {
        this.blockMetadataArray.set(par1, par2, par3, par4);
    }

    // JAVADOC METHOD $$ func_76663_a
    public boolean isEmpty()
    {
        return this.blockRefCount == 0;
    }

    // JAVADOC METHOD $$ func_76675_b
    public boolean getNeedsRandomTick()
    {
        return this.tickRefCount > 0;
    }

    // JAVADOC METHOD $$ func_76662_d
    public int getYLocation()
    {
        return this.yBase;
    }

    // JAVADOC METHOD $$ func_76657_c
    public void setExtSkylightValue(int par1, int par2, int par3, int par4)
    {
        this.skylightArray.set(par1, par2, par3, par4);
    }

    // JAVADOC METHOD $$ func_76670_c
    public int getExtSkylightValue(int par1, int par2, int par3)
    {
        return this.skylightArray.get(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_76677_d
    public void setExtBlocklightValue(int par1, int par2, int par3, int par4)
    {
        this.blocklightArray.set(par1, par2, par3, par4);
    }

    // JAVADOC METHOD $$ func_76674_d
    public int getExtBlocklightValue(int par1, int par2, int par3)
    {
        return this.blocklightArray.get(par1, par2, par3);
    }

    public void removeInvalidBlocks()
    {
        this.blockRefCount = 0;
        this.tickRefCount = 0;

        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
                for (int k = 0; k < 16; ++k)
                {
                    Block block = this.func_150819_a(i, j, k);

                    if (block != Blocks.air)
                    {
                        ++this.blockRefCount;

                        if (block.func_149653_t())
                        {
                            ++this.tickRefCount;
                        }
                    }
                }
            }
        }
    }

    public byte[] getBlockLSBArray()
    {
        return this.blockLSBArray;
    }

    @SideOnly(Side.CLIENT)
    public void clearMSBArray()
    {
        this.blockMSBArray = null;
    }

    // JAVADOC METHOD $$ func_76660_i
    public NibbleArray getBlockMSBArray()
    {
        return this.blockMSBArray;
    }

    public NibbleArray getMetadataArray()
    {
        return this.blockMetadataArray;
    }

    // JAVADOC METHOD $$ func_76661_k
    public NibbleArray getBlocklightArray()
    {
        return this.blocklightArray;
    }

    // JAVADOC METHOD $$ func_76671_l
    public NibbleArray getSkylightArray()
    {
        return this.skylightArray;
    }

    // JAVADOC METHOD $$ func_76664_a
    public void setBlockLSBArray(byte[] par1ArrayOfByte)
    {
        this.blockLSBArray = par1ArrayOfByte;
    }

    // JAVADOC METHOD $$ func_76673_a
    public void setBlockMSBArray(NibbleArray par1NibbleArray)
    {
        this.blockMSBArray = par1NibbleArray;
    }

    // JAVADOC METHOD $$ func_76668_b
    public void setBlockMetadataArray(NibbleArray par1NibbleArray)
    {
        this.blockMetadataArray = par1NibbleArray;
    }

    // JAVADOC METHOD $$ func_76659_c
    public void setBlocklightArray(NibbleArray par1NibbleArray)
    {
        this.blocklightArray = par1NibbleArray;
    }

    // JAVADOC METHOD $$ func_76666_d
    public void setSkylightArray(NibbleArray par1NibbleArray)
    {
        this.skylightArray = par1NibbleArray;
    }

    // JAVADOC METHOD $$ func_76667_m
    @SideOnly(Side.CLIENT)
    public NibbleArray createBlockMSBArray()
    {
        this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);
        return this.blockMSBArray;
    }
}