/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.textures;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * A helper that lets you bake quads that won't be used with an atlas.
 */
public class UnitSprite extends TextureAtlasSprite
{
    public static final UnitSprite INSTANCE = new UnitSprite();
    public static final ResourceLocation LOCATION = new ResourceLocation("forge", "unit");
    public static final Function<Material, TextureAtlasSprite> GETTER = (x) -> INSTANCE;

    private UnitSprite()
    {
        super(new TextureAtlas(LOCATION),
                new Info(LOCATION, 1, 1, AnimationMetadataSection.EMPTY),
                0, 1, 1,
                0, 0, new NativeImage(1, 1, false));
    }

    @Override
    public float getU(double u)
    {
        return (float) u / 16;
    }

    @Override
    public float getV(double v)
    {
        return (float) v / 16;
    }
}
