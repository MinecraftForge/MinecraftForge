package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class SaveHandler implements IPlayerFileData, ISaveHandler
{
    /** Reference to the logger. */
    private static final Logger logger = Logger.getLogger("Minecraft");

    /** The path to the current world directory */
    private final File worldDirectory;

    /** The directory in which to save world data */
    private final File worldFile;
    private final File mapDataDir;

    /**
     * The time in milliseconds when this field was initialized. Stored in the session lock file.
     */
    private final long initializationTime = System.currentTimeMillis();

    /** The directory name of the world */
    private final String saveDirectoryName;

    public SaveHandler(File par1File, String par2Str, boolean par3)
    {
        this.worldDirectory = new File(par1File, par2Str);
        this.worldDirectory.mkdirs();
        this.worldFile = new File(this.worldDirectory, "players");
        this.mapDataDir = new File(this.worldDirectory, "data");
        this.mapDataDir.mkdirs();
        this.saveDirectoryName = par2Str;

        if (par3)
        {
            this.worldFile.mkdirs();
        }

        this.setSessionLock();
    }

    /**
     * Creates a session lock file for this process
     */
    private void setSessionLock()
    {
        try
        {
            File var1 = new File(this.worldDirectory, "session.lock");
            DataOutputStream var2 = new DataOutputStream(new FileOutputStream(var1));

            try
            {
                var2.writeLong(this.initializationTime);
            }
            finally
            {
                var2.close();
            }
        }
        catch (IOException var7)
        {
            var7.printStackTrace();
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }

    /**
     * Gets the File object corresponding to the base directory of this world.
     */
    protected File getWorldDirectory()
    {
        return this.worldDirectory;
    }

    /**
     * Checks the session lock to prevent save collisions
     */
    public void checkSessionLock()
    {
        try
        {
            File var1 = new File(this.worldDirectory, "session.lock");
            DataInputStream var2 = new DataInputStream(new FileInputStream(var1));

            try
            {
                if (var2.readLong() != this.initializationTime)
                {
                    throw new MinecraftException("The save is being accessed from another location, aborting");
                }
            }
            finally
            {
                var2.close();
            }
        }
        catch (IOException var7)
        {
            throw new MinecraftException("Failed to check session lock, aborting");
        }
    }

    /**
     * initializes and returns the chunk loader for the specified world provider
     */
    public IChunkLoader getChunkLoader(WorldProvider par1WorldProvider)
    {
        throw new RuntimeException("Old Chunk Storage is no longer supported.");
    }

    /**
     * Attempts to load first level.dat, then level.dat_old from disk. Called when the server first starts.
     */
    public WorldInfo loadWorldInfo()
    {
        File var1 = new File(this.worldDirectory, "level.dat");
        NBTTagCompound var2;
        NBTTagCompound var3;

        if (var1.exists())
        {
            try
            {
                var2 = CompressedStreamTools.readCompressed(new FileInputStream(var1));
                var3 = var2.getCompoundTag("Data");
                return new WorldInfo(var3);
            }
            catch (Exception var5)
            {
                var5.printStackTrace();
            }
        }

        var1 = new File(this.worldDirectory, "level.dat_old");

        if (var1.exists())
        {
            try
            {
                var2 = CompressedStreamTools.readCompressed(new FileInputStream(var1));
                var3 = var2.getCompoundTag("Data");
                return new WorldInfo(var3);
            }
            catch (Exception var4)
            {
                var4.printStackTrace();
            }
        }

        return null;
    }

    /**
     * saves level.dat and backs up the existing one to level.dat_old
     */
    public void saveWorldInfoAndPlayer(WorldInfo par1WorldInfo, List par2List)
    {
        NBTTagCompound var3 = par1WorldInfo.getNBTTagCompoundWithPlayers(par2List);
        NBTTagCompound var4 = new NBTTagCompound();
        var4.setTag("Data", var3);

        try
        {
            File var5 = new File(this.worldDirectory, "level.dat_new");
            File var6 = new File(this.worldDirectory, "level.dat_old");
            File var7 = new File(this.worldDirectory, "level.dat");
            CompressedStreamTools.writeCompressed(var4, new FileOutputStream(var5));

            if (var6.exists())
            {
                var6.delete();
            }

            var7.renameTo(var6);

            if (var7.exists())
            {
                var7.delete();
            }

            var5.renameTo(var7);

            if (var5.exists())
            {
                var5.delete();
            }
        }
        catch (Exception var8)
        {
            var8.printStackTrace();
        }
    }

    /**
     * used to update level.dat from old format to MCRegion format
     */
    public void saveWorldInfo(WorldInfo par1WorldInfo)
    {
        NBTTagCompound var2 = par1WorldInfo.getNBTTagCompound();
        NBTTagCompound var3 = new NBTTagCompound();
        var3.setTag("Data", var2);

        try
        {
            File var4 = new File(this.worldDirectory, "level.dat_new");
            File var5 = new File(this.worldDirectory, "level.dat_old");
            File var6 = new File(this.worldDirectory, "level.dat");
            CompressedStreamTools.writeCompressed(var3, new FileOutputStream(var4));

            if (var5.exists())
            {
                var5.delete();
            }

            var6.renameTo(var5);

            if (var6.exists())
            {
                var6.delete();
            }

            var4.renameTo(var6);

            if (var4.exists())
            {
                var4.delete();
            }
        }
        catch (Exception var7)
        {
            var7.printStackTrace();
        }
    }

    /**
     * Writes the player data to disk from the specified PlayerEntityMP.
     */
    public void writePlayerData(EntityPlayer par1EntityPlayer)
    {
        try
        {
            NBTTagCompound var2 = new NBTTagCompound();
            par1EntityPlayer.writeToNBT(var2);
            File var3 = new File(this.worldFile, "_tmp_.dat");
            File var4 = new File(this.worldFile, par1EntityPlayer.username + ".dat");
            CompressedStreamTools.writeCompressed(var2, new FileOutputStream(var3));

            if (var4.exists())
            {
                var4.delete();
            }

            var3.renameTo(var4);
        }
        catch (Exception var5)
        {
            logger.warning("Failed to save player data for " + par1EntityPlayer.username);
        }
    }

    /**
     * Reads the player data from disk into the specified PlayerEntityMP.
     */
    public void readPlayerData(EntityPlayer par1EntityPlayer)
    {
        NBTTagCompound var2 = this.getPlayerData(par1EntityPlayer.username);

        if (var2 != null)
        {
            par1EntityPlayer.readFromNBT(var2);
        }
    }

    public NBTTagCompound getPlayerData(String par1Str)
    {
        try
        {
            File var2 = new File(this.worldFile, par1Str + ".dat");

            if (var2.exists())
            {
                return CompressedStreamTools.readCompressed(new FileInputStream(var2));
            }
        }
        catch (Exception var3)
        {
            logger.warning("Failed to load player data for " + par1Str);
        }

        return null;
    }

    /**
     * does exactly what it says on the tin
     */
    public IPlayerFileData getPlayerNBTManager()
    {
        return this;
    }

    public void func_22093_e() {}

    /**
     * Gets the file location of the given map
     */
    public File getMapFileFromName(String par1Str)
    {
        return new File(this.mapDataDir, par1Str + ".dat");
    }
}
