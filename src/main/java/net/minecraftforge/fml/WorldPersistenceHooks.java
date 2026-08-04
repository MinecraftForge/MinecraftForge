/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraft.nbt.CompoundNBT;
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

    public static void handleWorldDataSave(final SaveHandler handler, final WorldInfo worldInfo, final CompoundNBT tagCompound)
    {
        worldPersistenceHooks.forEach(wac->tagCompound.put(wac.getModId(), wac.getDataForWriting(handler, worldInfo)));
    }

    public static void handleWorldDataLoad(SaveHandler handler, WorldInfo worldInfo, CompoundNBT tagCompound)
    {
        if (EffectiveSide.get() == LogicalSide.SERVER)
        {
            worldPersistenceHooks.forEach(wac->wac.readData(handler, worldInfo, tagCompound.getCompound(wac.getModId())));
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
        CompoundNBT getDataForWriting(SaveHandler handler, WorldInfo info);
        void readData(SaveHandler handler, WorldInfo info, CompoundNBT tag);
    }
}
