/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Level;

import com.google.common.io.Files;
import com.mojang.datafixers.DataFixer;

import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldInfo;
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
    }

    @Override public WorldInfo loadWorldInfo() { return parent.loadWorldInfo(); }
    @Override public void checkSessionLock() throws MinecraftException { parent.checkSessionLock(); }
    @Override public IChunkLoader getChunkLoader(Dimension var1) { return parent.getChunkLoader(var1); }
    @Override public void saveWorldInfoWithPlayer(WorldInfo var1, NBTTagCompound var2) { parent.saveWorldInfoWithPlayer(var1, var2); }
    @Override public void saveWorldInfo(WorldInfo var1){ parent.saveWorldInfo(var1); }
    @Override public IPlayerFileData getPlayerNBTManager() { return parent.getPlayerNBTManager(); }
    @Override public void flush() { parent.flush(); }
    @Override public File getWorldDirectory() { return parent.getWorldDirectory(); }
    @Override public DataFixer func_197718_i() { return parent.func_197718_i(); }

    @Override
    public File getMapFileFromName(String name)
    {
        if (dataDir == null) //Delayed down here do that world has time to be initialized first.
        {
            dataDir = new File(world.getChunkSaveLocation(), "data");
            dataDir.mkdirs();
        }
        File file = new File(dataDir, name + ".dat");
        if (!file.exists())
        {
            switch (world.provider.getDimension())
            {
                case -1:
                    if (name.equalsIgnoreCase("FORTRESS")) copyFile(name, file);
                    break;
                case 1:
                    if (name.equalsIgnoreCase("VILLAGES_END")) copyFile(name, file);
                    break;
            }
        }
        return file;
    }

    private void copyFile(String name, File to)
    {
        File parentFile = parent.getMapFileFromName(name);
        if (parentFile.exists())
        {
            try
            {
                Files.copy(parentFile, to);
            }
            catch (IOException e)
            {
                FMLLog.log.error("A critical error occurred copying {} to world specific dat folder - new file will be created.", parentFile.getName(), e);
            }
        }
    }

    @Override
    public TemplateManager getStructureTemplateManager()
    {
        return parent.getStructureTemplateManager();
    }
}
