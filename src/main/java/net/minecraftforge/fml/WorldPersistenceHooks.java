/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.util.ArrayList;
import java.util.List;

public class WorldPersistenceHooks
{
    private static List<WorldPersistenceHook> worldPersistenceHooks = new ArrayList<>();

    public static void addHook(WorldPersistenceHook hook) {
        worldPersistenceHooks.add(hook);
    }

    public static void handleWorldDataSave(final SaveHandler handler, final WorldInfo worldInfo, final NBTTagCompound tagCompound)
    {
        worldPersistenceHooks.forEach(wac->tagCompound.setTag(wac.getModId(), wac.getDataForWriting(handler, worldInfo)));
    }

    public static void handleWorldDataLoad(SaveHandler handler, WorldInfo worldInfo, NBTTagCompound tagCompound)
    {
        if (EffectiveSide.get() == LogicalSide.SERVER)
        {
            worldPersistenceHooks.forEach(wac->wac.readData(handler, worldInfo, tagCompound.getCompoundTag(wac.getModId())));
        }
    }

    public static void confirmBackupLevelDatUse(SaveHandler handler)
    {
/*
        if (handlerToCheck == null || handlerToCheck.get() != handler) {
            // only run if the save has been initially loaded
            handlerToCheck = null;
            return;
        }

        String text = "Forge Mod Loader detected that the backup level.dat is being used.\n\n" +
                "This may happen due to a bug or corruption, continuing can damage\n" +
                "your world beyond repair or lose data / progress.\n\n" +
                "It's recommended to create a world backup before continuing.";

        boolean confirmed = StartupQuery.confirm(text);
        if (!confirmed) StartupQuery.abort();
*/
    }

    public interface WorldPersistenceHook
    {
        String getModId();
        NBTTagCompound getDataForWriting(SaveHandler handler, WorldInfo info);
        void readData(SaveHandler handler, WorldInfo info, NBTTagCompound tag);
    }
}
