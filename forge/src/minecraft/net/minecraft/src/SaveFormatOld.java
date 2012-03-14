package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SaveFormatOld implements ISaveFormat
{
    /**
     * Reference to the File object representing the directory for the world saves
     */
    protected final File savesDirectory;

    public SaveFormatOld(File par1File)
    {
        if (!par1File.exists())
        {
            par1File.mkdirs();
        }

        this.savesDirectory = par1File;
    }

    public String getFormatName()
    {
        return "Old Format";
    }

    public List getSaveList()
    {
        ArrayList var1 = new ArrayList();

        for (int var2 = 0; var2 < 5; ++var2)
        {
            String var3 = "World" + (var2 + 1);
            WorldInfo var4 = this.getWorldInfo(var3);

            if (var4 != null)
            {
                var1.add(new SaveFormatComparator(var3, "", var4.getLastTimePlayed(), var4.getSizeOnDisk(), var4.getGameType(), false, var4.isHardcoreModeEnabled()));
            }
        }

        return var1;
    }

    public void flushCache() {}

    public WorldInfo getWorldInfo(String par1Str)
    {
        File var2 = new File(this.savesDirectory, par1Str);

        if (!var2.exists())
        {
            return null;
        }
        else
        {
            File var3 = new File(var2, "level.dat");
            NBTTagCompound var4;
            NBTTagCompound var5;

            if (var3.exists())
            {
                try
                {
                    var4 = CompressedStreamTools.readCompressed(new FileInputStream(var3));
                    var5 = var4.getCompoundTag("Data");
                    return new WorldInfo(var5);
                }
                catch (Exception var7)
                {
                    var7.printStackTrace();
                }
            }

            var3 = new File(var2, "level.dat_old");

            if (var3.exists())
            {
                try
                {
                    var4 = CompressedStreamTools.readCompressed(new FileInputStream(var3));
                    var5 = var4.getCompoundTag("Data");
                    return new WorldInfo(var5);
                }
                catch (Exception var6)
                {
                    var6.printStackTrace();
                }
            }

            return null;
        }
    }

    /**
     * @args: Takes two arguments - first the name of the directory containing the world and second the new name for
     * that world. @desc: Renames the world by storing the new name in level.dat. It does *not* rename the directory
     * containing the world data.
     */
    public void renameWorld(String par1Str, String par2Str)
    {
        File var3 = new File(this.savesDirectory, par1Str);

        if (var3.exists())
        {
            File var4 = new File(var3, "level.dat");

            if (var4.exists())
            {
                try
                {
                    NBTTagCompound var5 = CompressedStreamTools.readCompressed(new FileInputStream(var4));
                    NBTTagCompound var6 = var5.getCompoundTag("Data");
                    var6.setString("LevelName", par2Str);
                    CompressedStreamTools.writeCompressed(var5, new FileOutputStream(var4));
                }
                catch (Exception var7)
                {
                    var7.printStackTrace();
                }
            }
        }
    }

    /**
     * @args: Takes one argument - the name of the directory of the world to delete. @desc: Delete the world by deleting
     * the associated directory recursively.
     */
    public void deleteWorldDirectory(String par1Str)
    {
        File var2 = new File(this.savesDirectory, par1Str);

        if (var2.exists())
        {
            deleteFiles(var2.listFiles());
            var2.delete();
        }
    }

    /**
     * @args: Takes one argument - the list of files and directories to delete. @desc: Deletes the files and directory
     * listed in the list recursively.
     */
    protected static void deleteFiles(File[] par0ArrayOfFile)
    {
        for (int var1 = 0; var1 < par0ArrayOfFile.length; ++var1)
        {
            if (par0ArrayOfFile[var1].isDirectory())
            {
                System.out.println("Deleting " + par0ArrayOfFile[var1]);
                deleteFiles(par0ArrayOfFile[var1].listFiles());
            }

            par0ArrayOfFile[var1].delete();
        }
    }

    /**
     * Returns back a loader for the specified save directory
     */
    public ISaveHandler getSaveLoader(String par1Str, boolean par2)
    {
        return new SaveHandler(this.savesDirectory, par1Str, par2);
    }

    /**
     * Checks if the save directory uses the old map format
     */
    public boolean isOldMapFormat(String par1Str)
    {
        return false;
    }

    /**
     * Converts the specified map to the new map format. Args: worldName, loadingScreen
     */
    public boolean convertMapFormat(String par1Str, IProgressUpdate par2IProgressUpdate)
    {
        return false;
    }
}
