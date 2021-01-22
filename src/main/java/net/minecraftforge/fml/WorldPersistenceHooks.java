/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.util.ArrayList;
import java.util.List;

public class WorldPersistenceHooks
{
    private static List<WorldPersistenceHook> worldPersistenceHooks = new ArrayList<>();

    public static void addHook(WorldPersistenceHook hook) {
        worldPersistenceHooks.add(hook);
    }

    public static void handleWorldDataSave(final SaveFormat.LevelSave levelSave, final IServerConfiguration serverInfo, final CompoundNBT tagCompound)
    {
        worldPersistenceHooks.forEach(wac->tagCompound.put(wac.getModId(), wac.getDataForWriting(levelSave, serverInfo)));
    }

    public static void handleWorldDataLoad(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo, CompoundNBT tagCompound)
    {
        worldPersistenceHooks.forEach(wac->wac.readData(levelSave, serverInfo, tagCompound.getCompound(wac.getModId())));
    }

    public interface WorldPersistenceHook
    {
        String getModId();
        CompoundNBT getDataForWriting(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo);
        void readData(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo, CompoundNBT tag);
    }
}
