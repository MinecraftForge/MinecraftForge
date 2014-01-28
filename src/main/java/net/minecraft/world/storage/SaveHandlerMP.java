package net.minecraft.world.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;

@SideOnly(Side.CLIENT)
public class SaveHandlerMP implements ISaveHandler
{
    private static final String __OBFID = "CL_00000602";

    // JAVADOC METHOD $$ func_75757_d
    public WorldInfo loadWorldInfo()
    {
        return null;
    }

    // JAVADOC METHOD $$ func_75762_c
    public void checkSessionLock() throws MinecraftException {}

    // JAVADOC METHOD $$ func_75763_a
    public IChunkLoader getChunkLoader(WorldProvider par1WorldProvider)
    {
        return null;
    }

    // JAVADOC METHOD $$ func_75755_a
    public void saveWorldInfoWithPlayer(WorldInfo par1WorldInfo, NBTTagCompound par2NBTTagCompound) {}

    // JAVADOC METHOD $$ func_75761_a
    public void saveWorldInfo(WorldInfo par1WorldInfo) {}

    // JAVADOC METHOD $$ func_75756_e
    public IPlayerFileData getSaveHandler()
    {
        return null;
    }

    // JAVADOC METHOD $$ func_75759_a
    public void flush() {}

    // JAVADOC METHOD $$ func_75758_b
    public File getMapFileFromName(String par1Str)
    {
        return null;
    }

    // JAVADOC METHOD $$ func_75760_g
    public String getWorldDirectoryName()
    {
        return "none";
    }

    // JAVADOC METHOD $$ func_75765_b
    public File getWorldDirectory()
    {
        return null;
    }
}