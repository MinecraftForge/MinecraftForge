/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import java.util.List;

/**
 * Extension interface for {@link VertexFormat}.
 */
public interface IForgeVertexFormat {
    List<VertexFormatElement> getElements();

    default boolean hasPosition() {
        return getElements().stream().anyMatch(e -> e.usage() == VertexFormatElement.Usage.POSITION);
    }

    default boolean hasNormal() {
        return getElements().stream().anyMatch(e -> e.usage() == VertexFormatElement.Usage.NORMAL);
    }

    default boolean hasColor() {
        return getElements().stream().anyMatch(e -> e.usage() == VertexFormatElement.Usage.COLOR);
    }

    default boolean hasUV(int which) {
        for (var e : getElements()) {
            if (e.usage() == VertexFormatElement.Usage.UV && e.index() == which) {
                return true;
            }
        }
        return false;
    }
}
