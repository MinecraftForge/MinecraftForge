/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.capabilities.CapabilityProvider;

public class WorldCapabilityData extends SavedData
{
    public static final String ID = "capabilities";

    private INBTSerializable<CompoundTag> serializable;
    private CompoundTag capNBT = null;

    public WorldCapabilityData(@Nullable INBTSerializable<CompoundTag> serializable)
    {
        this.serializable = serializable;
    }

    public static WorldCapabilityData load(CompoundTag tag, @Nullable INBTSerializable<CompoundTag> serializable) {
        WorldCapabilityData data = new WorldCapabilityData(serializable);
        data.read(tag);
        return data;
    }

    public void read(CompoundTag nbt)
    {
        this.capNBT = nbt;
        if (serializable != null)
        {
            serializable.deserializeNBT(this.capNBT);
            this.capNBT = null;
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        if (serializable != null)
            nbt = serializable.serializeNBT();
        return nbt;
    }

    @Override
    public boolean isDirty()
    {
        return true;
    }

    public void setCapabilities(INBTSerializable<CompoundTag> capabilities)
    {
        this.serializable = capabilities;
        if (this.capNBT != null && serializable != null)
        {
            serializable.deserializeNBT(this.capNBT);
            this.capNBT = null;
        }
    }
}
