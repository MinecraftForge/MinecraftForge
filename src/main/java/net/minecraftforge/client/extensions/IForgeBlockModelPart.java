/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public interface IForgeBlockModelPart {
    /*
    private BlockModelPart self() {
        return (BlockModelPart)this;
    }
    */

    default ChunkSectionLayer layer() {
        return null;
    }

    default ChunkSectionLayer layerFast() {
        return null;
    }
}
