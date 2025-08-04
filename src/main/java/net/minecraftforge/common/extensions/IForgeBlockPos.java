/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;

public interface IForgeBlockPos {
    private BlockPos self() { return (BlockPos) this; }

    default ListTag toListTag() {
        var tag = new ListTag();
        tag.add(IntTag.valueOf(self().getX()));
        tag.add(IntTag.valueOf(self().getY()));
        tag.add(IntTag.valueOf(self().getZ()));
        return tag;
    }

    default CompoundTag toCompoundTag() {
        return CompoundTag.builder()
            .putInt("x", self().getX())
            .putInt("y", self().getY())
            .putInt("z", self().getZ())
            .build();
    }
}
