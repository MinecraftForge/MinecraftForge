/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import org.jetbrains.annotations.Nullable;

public class LevelCapabilityData extends SavedData {
    public static final String ID = "capabilities";

    private INBTSerializable<CompoundTag> serializable;
    private CompoundTag capNBT = null;

    public LevelCapabilityData(@Nullable INBTSerializable<CompoundTag> serializable) {
        this.serializable = serializable;
    }

    public void read(CompoundTag nbt) {
        this.capNBT = nbt;
        if (serializable != null) {
            serializable.deserializeNBT(this.capNBT);
            this.capNBT = null;
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
        if (serializable != null)
            nbt = serializable.serializeNBT();
        return nbt;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public void setCapabilities(INBTSerializable<CompoundTag> capabilities) {
        this.serializable = capabilities;
        if (this.capNBT != null && serializable != null) {
            serializable.deserializeNBT(this.capNBT);
            this.capNBT = null;
        }
    }

    public static LevelCapabilityData compute(DimensionDataStorage data, @Nullable INBTSerializable<CompoundTag> caps) {
        var factory = new Factory<>(
            () -> new LevelCapabilityData(caps),
            (tag, lookup) -> {
                var ret = new LevelCapabilityData(caps);
                ret.read(tag);
                return ret;
            },
            null
        );

        var ret = data.computeIfAbsent(factory, ID);
        ret.setCapabilities(caps);
        return ret;
    }
}
