package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

public class CompressedStreamTools
{
    private static final String __OBFID = "CL_00001226";

    // JAVADOC METHOD $$ func_74796_a
    public static NBTTagCompound readCompressed(InputStream par0InputStream) throws IOException
    {
        DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(par0InputStream)));
        NBTTagCompound nbttagcompound;

        try
        {
            nbttagcompound = read(datainputstream);
        }
        finally
        {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    // JAVADOC METHOD $$ func_74799_a
    public static void writeCompressed(NBTTagCompound par0NBTTagCompound, OutputStream par1OutputStream) throws IOException
    {
        DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(par1OutputStream));

        try
        {
            write(par0NBTTagCompound, dataoutputstream);
        }
        finally
        {
            dataoutputstream.close();
        }
    }

    public static NBTTagCompound decompress(byte[] par0ArrayOfByte) throws IOException
    {
        DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(par0ArrayOfByte))));
        NBTTagCompound nbttagcompound;

        try
        {
            nbttagcompound = read(datainputstream);
        }
        finally
        {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    public static byte[] compress(NBTTagCompound par0NBTTagCompound) throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream));

        try
        {
            write(par0NBTTagCompound, dataoutputstream);
        }
        finally
        {
            dataoutputstream.close();
        }

        return bytearrayoutputstream.toByteArray();
    }

    public static void safeWrite(NBTTagCompound par0NBTTagCompound, File par1File) throws IOException
    {
        File file2 = new File(par1File.getAbsolutePath() + "_tmp");

        if (file2.exists())
        {
            file2.delete();
        }

        write(par0NBTTagCompound, file2);

        if (par1File.exists())
        {
            par1File.delete();
        }

        if (par1File.exists())
        {
            throw new IOException("Failed to delete " + par1File);
        }
        else
        {
            file2.renameTo(par1File);
        }
    }

    // JAVADOC METHOD $$ func_74794_a
    public static NBTTagCompound read(DataInput par0DataInput) throws IOException
    {
        NBTBase nbtbase = func_150664_a(par0DataInput, 0);

        if (nbtbase instanceof NBTTagCompound)
        {
            return (NBTTagCompound)nbtbase;
        }
        else
        {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void write(NBTTagCompound par0NBTTagCompound, DataOutput par1DataOutput) throws IOException
    {
        func_150663_a(par0NBTTagCompound, par1DataOutput);
    }

    private static void func_150663_a(NBTBase p_150663_0_, DataOutput p_150663_1_) throws IOException
    {
        p_150663_1_.writeByte(p_150663_0_.getId());

        if (p_150663_0_.getId() != 0)
        {
            p_150663_1_.writeUTF("");
            p_150663_0_.write(p_150663_1_);
        }
    }

    private static NBTBase func_150664_a(DataInput p_150664_0_, int p_150664_1_) throws IOException
    {
        byte b0 = p_150664_0_.readByte();

        if (b0 == 0)
        {
            return new NBTTagEnd();
        }
        else
        {
            p_150664_0_.readUTF();
            NBTBase nbtbase = NBTBase.func_150284_a(b0);

            try
            {
                nbtbase.load(p_150664_0_, p_150664_1_);
                return nbtbase;
            }
            catch (IOException ioexception)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(ioexception, "Loading NBT data");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("NBT Tag");
                crashreportcategory.addCrashSection("Tag name", "[UNNAMED TAG]");
                crashreportcategory.addCrashSection("Tag type", Byte.valueOf(b0));
                throw new ReportedException(crashreport);
            }
        }
    }

    public static void write(NBTTagCompound par0NBTTagCompound, File par1File) throws IOException
    {
        DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(par1File));

        try
        {
            write(par0NBTTagCompound, dataoutputstream);
        }
        finally
        {
            dataoutputstream.close();
        }
    }

    public static NBTTagCompound read(File par0File) throws IOException
    {
        if (!par0File.exists())
        {
            return null;
        }
        else
        {
            DataInputStream datainputstream = new DataInputStream(new FileInputStream(par0File));
            NBTTagCompound nbttagcompound;

            try
            {
                nbttagcompound = read(datainputstream);
            }
            finally
            {
                datainputstream.close();
            }

            return nbttagcompound;
        }
    }
}