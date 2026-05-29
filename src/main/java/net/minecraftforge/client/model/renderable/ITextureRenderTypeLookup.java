/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.model.renderable;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

/**
 * A generic lookup for {@link RenderType} implementations that use the specified texture.
 */
@FunctionalInterface
public interface ITextureRenderTypeLookup {
    RenderType get(Identifier name);
}
