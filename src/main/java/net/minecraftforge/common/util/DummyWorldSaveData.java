/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

public class DummyWorldSaveData extends WorldSavedData {
    public static final DummyWorldSaveData DUMMY = new DummyWorldSaveData();
    private DummyWorldSaveData() {
        super("DUMMYDUMMY \uD83D\uDC4C\uD83D\uDC4C\uD83D\uDC4C");
    }

    @Override
    public void read(final CompoundNBT nbt) {
        // NOOP
    }

    @Override
    public CompoundNBT write(final CompoundNBT compound) {
        // NOOP
        return null;
    }
}
