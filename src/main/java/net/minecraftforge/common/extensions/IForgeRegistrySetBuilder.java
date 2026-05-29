/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.extensions;

import com.mojang.serialization.Lifecycle;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraftsingularity.registries.DeferredRegisterData;

public interface IForgeRegistrySetBuilder {
    private RegistrySetBuilder self() {
        return (RegistrySetBuilder)this;
    }

    default <T> RegistrySetBuilder add(DeferredRegisterData<T> dr) {
        return self().add(dr.getRegistryKey(), dr);
    }

    default <T> RegistrySetBuilder add(DeferredRegisterData<T> dr, Lifecycle lifecycle) {
        return self().add(dr.getRegistryKey(), lifecycle, dr);
    }
}
