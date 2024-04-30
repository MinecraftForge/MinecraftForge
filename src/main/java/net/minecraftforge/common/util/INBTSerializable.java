/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

/**
 * An interface designed to unify various things in the Minecraft
 * code base that can be serialized to and from a NBT tag.
 *
 * @deprecated // Mojang has switched most things to Codecs and registry context, probably worth deleting this.
 */
public interface INBTSerializable<T extends Tag> {
    default T serializeNBT() { return null; }
    default T serializeNBT(HolderLookup.Provider registryAccess) {
        return serializeNBT();
    }
    default void deserializeNBT(T nbt) { }
    default void deserializeNBT(HolderLookup.Provider registryAccess, T nbt) {
        deserializeNBT(nbt);
    }
}
