package net.minecraftforge.common;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Level;

import com.google.common.io.Files;

import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.MinecraftException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;

//Class used internally to provide the world specific data directories.

public class WorldSpecificSaveHandler implements ISaveHandler
{
    private WorldServer world;
    private ISaveHandler parent;
    private File dataDir;

    public WorldSpecificSaveHandler(WorldServer world, ISaveHandler parent)
    {
        this.world = world;
        this.parent = parent;
    }

    @Override public WorldInfo loadWorldInfo() { return parent.loadWorldInfo(); }
    @Override public void checkSessionLock() throws MinecraftException { parent.checkSessionLock(); }
    @Override public IChunkLoader getChunkLoader(WorldProvider var1) { return parent.getChunkLoader(var1); }
    @Override public void saveWorldInfoWithPlayer(WorldInfo var1, NBTTagCompound var2) { parent.saveWorldInfoWithPlayer(var1, var2); }
    @Override public void saveWorldInfo(WorldInfo var1){ parent.saveWorldInfo(var1); }
    @Override public IPlayerFileData getPlayerNBTManager() { return parent.getPlayerNBTManager(); }
    @Override public void flush() { parent.flush(); }
    @Override public String getWorldDirectoryName() { return parent.getWorldDirectoryName(); }
    @Override public File getWorldDirectory() { return parent.getWorldDirectory(); }

    @Override
    public File getMapFileFromName(String name)
    {
        if (dataDir == null) //Delayed down here do that world has time to be initalized first.
        {
            dataDir = new File(world.getChunkSaveLocation(), "data");
            dataDir.mkdirs();
        }
        File file = new File(dataDir, name + ".dat");
        if (!file.exists() && name.equalsIgnoreCase("FORTRESS") && world.provider.getDimensionId() == -1) //Only copy over the fortress.dat for the vanilla nether.
        {
            File parentFile = parent.getMapFileFromName(name);
            if (parentFile.exists())
            {
                try
                {
                    Files.copy(parentFile, file);
                }
                catch (IOException e)
                {
                    FMLLog.log(Level.ERROR, e, "A critical error occured copying fortress.dat to world specific dat folder - new file will be created.");
                }
            }
        }
        return file;
    }

}
