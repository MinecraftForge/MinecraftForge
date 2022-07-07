/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.renderable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * A generic lookup for {@link RenderType} implementations that use the specified texture.
 */
@FunctionalInterface
public interface ITextureRenderTypeLookup
{
    RenderType get(ResourceLocation name);
}
