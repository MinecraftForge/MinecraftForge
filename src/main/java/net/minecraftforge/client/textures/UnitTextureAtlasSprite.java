/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.textures;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;

/**
 * A helper sprite with UVs spanning the entire texture.
 * <p>
 * Useful for baking quads that won't be used with an atlas.
 */
public class UnitTextureAtlasSprite extends TextureAtlasSprite {
    public static final Identifier LOCATION = Identifier.fromNamespaceAndPath("singularity", "unit");
    public static final UnitTextureAtlasSprite INSTANCE = new UnitTextureAtlasSprite();

    private UnitTextureAtlasSprite() {
        super(LOCATION, new SpriteContents(LOCATION, new FrameSize(1, 1), new NativeImage(1, 1, false)), 1, 1, 0, 0, 0);
    }

    @Override
    public float getU(float u) {
        return u;
    }

    @Override
    public float getV(float v) {
        return v;
    }
}
