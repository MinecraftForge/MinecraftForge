package net.minecraftforge.common;

import java.io.File;

import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.MinecraftException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;

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
        dataDir = new File(world.getChunkSaveLocation(), "data");
        dataDir.mkdirs();
    }

    @Override public WorldInfo loadWorldInfo() { return parent.loadWorldInfo(); }
    @Override public void checkSessionLock() throws MinecraftException { parent.checkSessionLock(); }
    @Override public IChunkLoader getChunkLoader(WorldProvider var1) { return parent.getChunkLoader(var1); }
    @Override public void saveWorldInfoWithPlayer(WorldInfo var1, NBTTagCompound var2) { parent.saveWorldInfoWithPlayer(var1, var2); }
    @Override public void saveWorldInfo(WorldInfo var1){ parent.saveWorldInfo(var1); }
    @Override public IPlayerFileData getSaveHandler() { return parent.getSaveHandler(); }
    @Override public void flush() { parent.flush(); }
    @Override public String getSaveDirectoryName() { return parent.getSaveDirectoryName(); }

    @Override
    public File getMapFileFromName(String name)
    {
        return new File(dataDir, name + ".dat");
    }
}
