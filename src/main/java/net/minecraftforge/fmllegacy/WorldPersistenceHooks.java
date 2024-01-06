/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fmllegacy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.util.ArrayList;
import java.util.List;

public class WorldPersistenceHooks
{
    private static List<WorldPersistenceHook> worldPersistenceHooks = new ArrayList<>();

    public static void addHook(WorldPersistenceHook hook) {
        worldPersistenceHooks.add(hook);
    }

    public static void handleWorldDataSave(final LevelStorageSource.LevelStorageAccess levelSave, final WorldData serverInfo, final CompoundTag tagCompound)
    {
        worldPersistenceHooks.forEach(wac->tagCompound.put(wac.getModId(), wac.getDataForWriting(levelSave, serverInfo)));
    }

    public static void handleWorldDataLoad(LevelStorageSource.LevelStorageAccess levelSave, WorldData serverInfo, CompoundTag tagCompound)
    {
        worldPersistenceHooks.forEach(wac->wac.readData(levelSave, serverInfo, tagCompound.getCompound(wac.getModId())));
    }

    public interface WorldPersistenceHook
    {
        String getModId();
        CompoundTag getDataForWriting(LevelStorageSource.LevelStorageAccess levelSave, WorldData serverInfo);
        void readData(LevelStorageSource.LevelStorageAccess levelSave, WorldData serverInfo, CompoundTag tag);
    }
}
