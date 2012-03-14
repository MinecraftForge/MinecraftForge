package net.minecraft.src;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

class MusInputStream extends InputStream
{
    private int hash;
    private InputStream inputStream;
    byte[] buffer;

    final CodecMus codec;

    public MusInputStream(CodecMus par1CodecMus, URL par2URL, InputStream par3InputStream)
    {
        this.codec = par1CodecMus;
        this.buffer = new byte[1];
        this.inputStream = par3InputStream;
        String var4 = par2URL.getPath();
        var4 = var4.substring(var4.lastIndexOf("/") + 1);
        this.hash = var4.hashCode();
    }

    public int read()
    {
        int var1 = this.read(this.buffer, 0, 1);
        return var1 < 0 ? var1 : this.buffer[0];
    }

    public int read(byte[] par1ArrayOfByte, int par2, int par3)
    {
        try
        {
            par3 = this.inputStream.read(par1ArrayOfByte, par2, par3);
        }
        catch (IOException e)
        {
            return 0;
        }

        for (int var4 = 0; var4 < par3; ++var4)
        {
            byte var5 = par1ArrayOfByte[par2 + var4] = (byte)(par1ArrayOfByte[par2 + var4] ^ this.hash >> 8);
            this.hash = this.hash * 498729871 + 85731 * var5;
        }

        return par3;
    }
}
