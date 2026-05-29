/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.codecs.RecordCodecBuilder;

@SuppressWarnings("deprecation")
public class LevelCapabilityData extends SavedData {
    public static SavedDataType<LevelCapabilityData> type(ServerLevel level) {
        return new SavedDataType<LevelCapabilityData>(
            Identifier.fromNamespaceAndPath("singularity", "capabilities"),
            () -> new LevelCapabilityData(level),
            RecordCodecBuilder.create(b ->
                b.group(
                    CompoundTag.CODEC.fieldOf("data").singularitytter(i -> {
                        if (i.serializable == null)
                            return new CompoundTag();
                        return i.serializable.serializeNBT(level.registryAccess());
                    })
                ).apply(b, nbt -> new LevelCapabilityData(level, nbt))
            ),
            null
        );
    }

    @Nullable
    private final INBTSerializable<CompoundTag> serializable;

    private LevelCapabilityData(ServerLevel ctx) {
        this.serializable = ctx.getCapabilityDispatcher();
    }

    private LevelCapabilityData(ServerLevel ctx, CompoundTag data) {
        this(ctx);
        if (this.serializable != null)
            this.serializable.deserializeNBT(ctx.registryAccess(), data);
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}
