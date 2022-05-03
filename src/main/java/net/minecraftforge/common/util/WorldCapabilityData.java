/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

public class WorldCapabilityData extends WorldSavedData
{
    public static final String ID = "capabilities";

    private INBTSerializable<CompoundNBT> serializable;
    private CompoundNBT capNBT = null;

    public WorldCapabilityData(String name)
    {
        super(name);
    }

    public WorldCapabilityData(@Nullable INBTSerializable<CompoundNBT> serializable)
    {
        super(ID);
        this.serializable = serializable;
    }

    @Override
    public void load(CompoundNBT nbt)
    {
        this.capNBT = nbt;
        if (serializable != null)
        {
            serializable.deserializeNBT(this.capNBT);
            this.capNBT = null;
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
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

    public void setCapabilities(INBTSerializable<CompoundNBT> capabilities)
    {
        this.serializable = capabilities;
        if (this.capNBT != null && serializable != null)
        {
            serializable.deserializeNBT(this.capNBT);
            this.capNBT = null;
        }
    }
}
