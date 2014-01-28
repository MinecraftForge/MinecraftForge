package net.minecraft.world.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld implements ISaveFormat
{
    private static final Logger field_151479_b = LogManager.getLogger();
    // JAVADOC FIELD $$ field_75808_a
    protected final File savesDirectory;
    private static final String __OBFID = "CL_00000586";

    public SaveFormatOld(File par1File)
    {
        if (!par1File.exists())
        {
            par1File.mkdirs();
        }

        this.savesDirectory = par1File;
    }

    @SideOnly(Side.CLIENT)
    public List getSaveList() throws AnvilConverterException
    {
        ArrayList arraylist = new ArrayList();

        for (int i = 0; i < 5; ++i)
        {
            String s = "World" + (i + 1);
            WorldInfo worldinfo = this.getWorldInfo(s);

            if (worldinfo != null)
            {
                arraylist.add(new SaveFormatComparator(s, "", worldinfo.getLastTimePlayed(), worldinfo.getSizeOnDisk(), worldinfo.getGameType(), false, worldinfo.isHardcoreModeEnabled(), worldinfo.areCommandsAllowed()));
            }
        }

        return arraylist;
    }

    public void flushCache() {}

    // JAVADOC METHOD $$ func_75803_c
    public WorldInfo getWorldInfo(String par1Str)
    {
        File file1 = new File(this.savesDirectory, par1Str);

        if (!file1.exists())
        {
            return null;
        }
        else
        {
            File file2 = new File(file1, "level.dat");
            NBTTagCompound nbttagcompound;
            NBTTagCompound nbttagcompound1;

            if (file2.exists())
            {
                try
                {
                    nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file2));
                    nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                    return new WorldInfo(nbttagcompound1);
                }
                catch (Exception exception1)
                {
                    field_151479_b.error("Exception reading " + file2, exception1);
                }
            }

            file2 = new File(file1, "level.dat_old");

            if (file2.exists())
            {
                try
                {
                    nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file2));
                    nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                    return new WorldInfo(nbttagcompound1);
                }
                catch (Exception exception)
                {
                    field_151479_b.error("Exception reading " + file2, exception);
                }
            }

            return null;
        }
    }

    // JAVADOC METHOD $$ func_75806_a
    @SideOnly(Side.CLIENT)
    public void renameWorld(String par1Str, String par2Str)
    {
        File file1 = new File(this.savesDirectory, par1Str);

        if (file1.exists())
        {
            File file2 = new File(file1, "level.dat");

            if (file2.exists())
            {
                try
                {
                    NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file2));
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                    nbttagcompound1.setString("LevelName", par2Str);
                    CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file2));
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_75802_e
    public boolean deleteWorldDirectory(String par1Str)
    {
        File file1 = new File(this.savesDirectory, par1Str);

        if (!file1.exists())
        {
            return true;
        }
        else
        {
            field_151479_b.info("Deleting level " + par1Str);

            for (int i = 1; i <= 5; ++i)
            {
                field_151479_b.info("Attempt " + i + "...");

                if (deleteFiles(file1.listFiles()))
                {
                    break;
                }

                field_151479_b.warn("Unsuccessful in deleting contents.");

                if (i < 5)
                {
                    try
                    {
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException interruptedexception)
                    {
                        ;
                    }
                }
            }

            return file1.delete();
        }
    }

    // JAVADOC METHOD $$ func_75807_a
    protected static boolean deleteFiles(File[] par0ArrayOfFile)
    {
        for (int i = 0; i < par0ArrayOfFile.length; ++i)
        {
            File file1 = par0ArrayOfFile[i];
            field_151479_b.debug("Deleting " + file1);

            if (file1.isDirectory() && !deleteFiles(file1.listFiles()))
            {
                field_151479_b.warn("Couldn\'t delete directory " + file1);
                return false;
            }

            if (!file1.delete())
            {
                field_151479_b.warn("Couldn\'t delete file " + file1);
                return false;
            }
        }

        return true;
    }

    // JAVADOC METHOD $$ func_75804_a
    public ISaveHandler getSaveLoader(String par1Str, boolean par2)
    {
        return new SaveHandler(this.savesDirectory, par1Str, par2);
    }

    // JAVADOC METHOD $$ func_75801_b
    public boolean isOldMapFormat(String par1Str)
    {
        return false;
    }

    // JAVADOC METHOD $$ func_75805_a
    public boolean convertMapFormat(String par1Str, IProgressUpdate par2IProgressUpdate)
    {
        return false;
    }

    // JAVADOC METHOD $$ func_90033_f
    @SideOnly(Side.CLIENT)
    public boolean canLoadWorld(String par1Str)
    {
        File file1 = new File(this.savesDirectory, par1Str);
        return file1.isDirectory();
    }
}