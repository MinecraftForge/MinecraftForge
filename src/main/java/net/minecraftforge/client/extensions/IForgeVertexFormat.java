/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

/**
 * Extension interface for {@link VertexFormat}.
 */
public interface IForgeVertexFormat {
    ImmutableList<VertexFormatElement> getElements();

    default boolean hasPosition() {
        return getElements().stream().anyMatch(e -> e.getUsage() == VertexFormatElement.Usage.POSITION);
    }

    default boolean hasNormal() {
        return getElements().stream().anyMatch(e -> e.getUsage() == VertexFormatElement.Usage.NORMAL);
    }

    default boolean hasColor() {
        return getElements().stream().anyMatch(e -> e.getUsage() == VertexFormatElement.Usage.COLOR);
    }

    default boolean hasUV(int which) {
        for (var e : getElements()) {
            if (e.getUsage() == VertexFormatElement.Usage.UV && e.getIndex() == which) {
                return true;
            }
        }
        return false;
    }
}
