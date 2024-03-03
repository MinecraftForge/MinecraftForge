/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class DummySavedData extends SavedData {
    public static final DummySavedData DUMMY = new DummySavedData();
    private DummySavedData() {
        super();
    }

    @Override
    public CompoundTag save(final CompoundTag compound) {
        // NOOP
        return null;
    }
}
