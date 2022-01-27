/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
