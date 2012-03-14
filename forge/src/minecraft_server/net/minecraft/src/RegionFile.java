package net.minecraft.src;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionFile
{
    private static final byte[] emptySector = new byte[4096];
    private final File fileName;
    private RandomAccessFile dataFile;
    private final int[] offsets = new int[1024];
    private final int[] chunkTimestamps = new int[1024];
    private ArrayList sectorFree;

    /** McRegion sizeDelta */
    private int sizeDelta;
    private long lastModified = 0L;

    public RegionFile(File par1File)
    {
        this.fileName = par1File;
        this.debugln("REGION LOAD " + this.fileName);
        this.sizeDelta = 0;

        try
        {
            if (par1File.exists())
            {
                this.lastModified = par1File.lastModified();
            }

            this.dataFile = new RandomAccessFile(par1File, "rw");
            int var2;

            if (this.dataFile.length() < 4096L)
            {
                for (var2 = 0; var2 < 1024; ++var2)
                {
                    this.dataFile.writeInt(0);
                }

                for (var2 = 0; var2 < 1024; ++var2)
                {
                    this.dataFile.writeInt(0);
                }

                this.sizeDelta += 8192;
            }

            if ((this.dataFile.length() & 4095L) != 0L)
            {
                for (var2 = 0; (long)var2 < (this.dataFile.length() & 4095L); ++var2)
                {
                    this.dataFile.write(0);
                }
            }

            var2 = (int)this.dataFile.length() / 4096;
            this.sectorFree = new ArrayList(var2);
            int var3;

            for (var3 = 0; var3 < var2; ++var3)
            {
                this.sectorFree.add(Boolean.valueOf(true));
            }

            this.sectorFree.set(0, Boolean.valueOf(false));
            this.sectorFree.set(1, Boolean.valueOf(false));
            this.dataFile.seek(0L);
            int var4;

            for (var3 = 0; var3 < 1024; ++var3)
            {
                var4 = this.dataFile.readInt();
                this.offsets[var3] = var4;

                if (var4 != 0 && (var4 >> 8) + (var4 & 255) <= this.sectorFree.size())
                {
                    for (int var5 = 0; var5 < (var4 & 255); ++var5)
                    {
                        this.sectorFree.set((var4 >> 8) + var5, Boolean.valueOf(false));
                    }
                }
            }

            for (var3 = 0; var3 < 1024; ++var3)
            {
                var4 = this.dataFile.readInt();
                this.chunkTimestamps[var3] = var4;
            }
        }
        catch (IOException var6)
        {
            var6.printStackTrace();
        }
    }

    private void debug(String par1Str) {}

    private void debugln(String par1Str)
    {
        this.debug(par1Str + "\n");
    }

    private void debug(String par1Str, int par2, int par3, String par4Str)
    {
        this.debug("REGION " + par1Str + " " + this.fileName.getName() + "[" + par2 + "," + par3 + "] = " + par4Str);
    }

    private void debug(String par1Str, int par2, int par3, int par4, String par5Str)
    {
        this.debug("REGION " + par1Str + " " + this.fileName.getName() + "[" + par2 + "," + par3 + "] " + par4 + "B = " + par5Str);
    }

    private void debugln(String par1Str, int par2, int par3, String par4Str)
    {
        this.debug(par1Str, par2, par3, par4Str + "\n");
    }

    /**
     * args: x, y - get uncompressed chunk stream from the region file
     */
    public synchronized DataInputStream getChunkDataInputStream(int par1, int par2)
    {
        if (this.outOfBounds(par1, par2))
        {
            this.debugln("READ", par1, par2, "out of bounds");
            return null;
        }
        else
        {
            try
            {
                int var3 = this.getOffset(par1, par2);

                if (var3 == 0)
                {
                    return null;
                }
                else
                {
                    int var4 = var3 >> 8;
                    int var5 = var3 & 255;

                    if (var4 + var5 > this.sectorFree.size())
                    {
                        this.debugln("READ", par1, par2, "invalid sector");
                        return null;
                    }
                    else
                    {
                        this.dataFile.seek((long)(var4 * 4096));
                        int var6 = this.dataFile.readInt();

                        if (var6 > 4096 * var5)
                        {
                            this.debugln("READ", par1, par2, "invalid length: " + var6 + " > 4096 * " + var5);
                            return null;
                        }
                        else if (var6 <= 0)
                        {
                            this.debugln("READ", par1, par2, "invalid length: " + var6 + " < 1");
                            return null;
                        }
                        else
                        {
                            byte var7 = this.dataFile.readByte();
                            byte[] var8;
                            DataInputStream var9;

                            if (var7 == 1)
                            {
                                var8 = new byte[var6 - 1];
                                this.dataFile.read(var8);
                                var9 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(var8))));
                                return var9;
                            }
                            else if (var7 == 2)
                            {
                                var8 = new byte[var6 - 1];
                                this.dataFile.read(var8);
                                var9 = new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(var8))));
                                return var9;
                            }
                            else
                            {
                                this.debugln("READ", par1, par2, "unknown version " + var7);
                                return null;
                            }
                        }
                    }
                }
            }
            catch (IOException var10)
            {
                this.debugln("READ", par1, par2, "exception");
                return null;
            }
        }
    }

    /**
     * args: x, z - get an output stream used to write chunk data, data is on disk when the returned stream is closed
     */
    public DataOutputStream getChunkDataOutputStream(int par1, int par2)
    {
        return this.outOfBounds(par1, par2) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFileChunkBuffer(this, par1, par2)));
    }

    /**
     * args: x, z, data, length - write chunk data at (x, z) to disk
     */
    protected synchronized void write(int par1, int par2, byte[] par3ArrayOfByte, int par4)
    {
        try
        {
            int var5 = this.getOffset(par1, par2);
            int var6 = var5 >> 8;
            int var7 = var5 & 255;
            int var8 = (par4 + 5) / 4096 + 1;

            if (var8 >= 256)
            {
                return;
            }

            if (var6 != 0 && var7 == var8)
            {
                this.debug("SAVE", par1, par2, par4, "rewrite");
                this.write(var6, par3ArrayOfByte, par4);
            }
            else
            {
                int var9;

                for (var9 = 0; var9 < var7; ++var9)
                {
                    this.sectorFree.set(var6 + var9, Boolean.valueOf(true));
                }

                var9 = this.sectorFree.indexOf(Boolean.valueOf(true));
                int var10 = 0;
                int var11;

                if (var9 != -1)
                {
                    for (var11 = var9; var11 < this.sectorFree.size(); ++var11)
                    {
                        if (var10 != 0)
                        {
                            if (((Boolean)this.sectorFree.get(var11)).booleanValue())
                            {
                                ++var10;
                            }
                            else
                            {
                                var10 = 0;
                            }
                        }
                        else if (((Boolean)this.sectorFree.get(var11)).booleanValue())
                        {
                            var9 = var11;
                            var10 = 1;
                        }

                        if (var10 >= var8)
                        {
                            break;
                        }
                    }
                }

                if (var10 >= var8)
                {
                    this.debug("SAVE", par1, par2, par4, "reuse");
                    var6 = var9;
                    this.setOffset(par1, par2, var9 << 8 | var8);

                    for (var11 = 0; var11 < var8; ++var11)
                    {
                        this.sectorFree.set(var6 + var11, Boolean.valueOf(false));
                    }

                    this.write(var6, par3ArrayOfByte, par4);
                }
                else
                {
                    this.debug("SAVE", par1, par2, par4, "grow");
                    this.dataFile.seek(this.dataFile.length());
                    var6 = this.sectorFree.size();

                    for (var11 = 0; var11 < var8; ++var11)
                    {
                        this.dataFile.write(emptySector);
                        this.sectorFree.add(Boolean.valueOf(false));
                    }

                    this.sizeDelta += 4096 * var8;
                    this.write(var6, par3ArrayOfByte, par4);
                    this.setOffset(par1, par2, var6 << 8 | var8);
                }
            }

            this.setChunkTimestamp(par1, par2, (int)(System.currentTimeMillis() / 1000L));
        }
        catch (IOException var12)
        {
            var12.printStackTrace();
        }
    }

    /**
     * args: sectorNumber, data, length - write the chunk data to this RegionFile
     */
    private void write(int par1, byte[] par2ArrayOfByte, int par3) throws IOException
    {
        this.debugln(" " + par1);
        this.dataFile.seek((long)(par1 * 4096));
        this.dataFile.writeInt(par3 + 1);
        this.dataFile.writeByte(2);
        this.dataFile.write(par2ArrayOfByte, 0, par3);
    }

    /**
     * args: x, z - check region bounds
     */
    private boolean outOfBounds(int par1, int par2)
    {
        return par1 < 0 || par1 >= 32 || par2 < 0 || par2 >= 32;
    }

    /**
     * args: x, y - get chunk's offset in region file
     */
    private int getOffset(int par1, int par2)
    {
        return this.offsets[par1 + par2 * 32];
    }

    /**
     * args: x, z, - true if chunk has been saved / converted
     */
    public boolean isChunkSaved(int par1, int par2)
    {
        return this.getOffset(par1, par2) != 0;
    }

    /**
     * args: x, z, offset - sets the chunk's offset in the region file
     */
    private void setOffset(int par1, int par2, int par3) throws IOException
    {
        this.offsets[par1 + par2 * 32] = par3;
        this.dataFile.seek((long)((par1 + par2 * 32) * 4));
        this.dataFile.writeInt(par3);
    }

    /**
     * args: x, z, timestamp - sets the chunk's write timestamp
     */
    private void setChunkTimestamp(int par1, int par2, int par3) throws IOException
    {
        this.chunkTimestamps[par1 + par2 * 32] = par3;
        this.dataFile.seek((long)(4096 + (par1 + par2 * 32) * 4));
        this.dataFile.writeInt(par3);
    }

    /**
     * close this RegionFile and prevent further writes
     */
    public void close() throws IOException
    {
        this.dataFile.close();
    }
}
