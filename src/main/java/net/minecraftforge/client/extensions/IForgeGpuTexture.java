/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.extensions;

import com.mojang.blaze3d.textures.GpuTexture;

public interface IForgeGpuTexture {
    @SuppressWarnings("unused")
    private GpuTexture self() {
        return (GpuTexture) this;
    }

    default boolean isStencilEnabled() {
        return false;
    }
}
