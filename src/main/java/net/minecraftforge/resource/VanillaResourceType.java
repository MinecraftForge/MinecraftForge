/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.resource;

/**
 * An enum of all {@link IResourceType}s used by the Vanilla game. These should be used if handling vanilla-related
 * resources.
 */
public enum VanillaResourceType implements IResourceType
{
    /**
     * Used when block and item models are reloaded and rebaked. This also includes the texture-stitching from that
     * phase.
     */
    MODELS,

    /**
     * Used when textures from the {@link net.minecraft.client.renderer.texture.TextureManager} are reloaded. Does not
     * effect block or item textures on the texture atlas.
     */
    TEXTURES,

    /**
     * Used when all game sounds are reloaded.
     */
    SOUNDS,

    /**
     * Used when the current language is reloaded.
     */
    LANGUAGES,

    /**
     * Used when all shaders are reloaded.
     */
    SHADERS,
}
