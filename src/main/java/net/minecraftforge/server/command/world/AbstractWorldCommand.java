/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.server.command.world;

import net.minecraft.command.CommandBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;

import java.io.File;

abstract class AbstractWorldCommand extends CommandBase
{

    protected CommandWorldBase base;

    AbstractWorldCommand(CommandWorldBase base)
    {
        this.base = base;
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

    boolean doesWorldExist(MinecraftServer server, String folderName)
    {
        ISaveFormat converter = server.getActiveAnvilConverter();
        if (!(converter instanceof SaveFormatOld))
        {
            return false;
        }
        File saveDirectory = new File(((SaveFormatOld) converter).savesDirectory, folderName);
        File save = new File(saveDirectory, "level.dat");
        if (exists(server, save))
        {
            return true;
        }
        save = new File(saveDirectory, "level.dat_old");
        return exists(server, save);
    }

    private boolean exists(MinecraftServer server, File save)
    {
        if (!save.exists())
        {
            return false;
        }
        WorldInfo worldInfo = SaveFormatOld.getWorldData(save, server.getDataFixer());
        return worldInfo != null;
    }
}
