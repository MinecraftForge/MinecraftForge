package net.minecraft.world.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;

public interface ISaveHandler
{
    // JAVADOC METHOD $$ func_75757_d
    WorldInfo loadWorldInfo();

    // JAVADOC METHOD $$ func_75762_c
    void checkSessionLock() throws MinecraftException;

    // JAVADOC METHOD $$ func_75763_a
    IChunkLoader getChunkLoader(WorldProvider var1);

    // JAVADOC METHOD $$ func_75755_a
    void saveWorldInfoWithPlayer(WorldInfo var1, NBTTagCompound var2);

    // JAVADOC METHOD $$ func_75761_a
    void saveWorldInfo(WorldInfo var1);

    // JAVADOC METHOD $$ func_75756_e
    IPlayerFileData getSaveHandler();

    // JAVADOC METHOD $$ func_75759_a
    void flush();

    // JAVADOC METHOD $$ func_75765_b
    File getWorldDirectory();

    // JAVADOC METHOD $$ func_75758_b
    File getMapFileFromName(String var1);

    // JAVADOC METHOD $$ func_75760_g
    String getWorldDirectoryName();
}