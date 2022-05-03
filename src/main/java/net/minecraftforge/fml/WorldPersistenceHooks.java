/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
