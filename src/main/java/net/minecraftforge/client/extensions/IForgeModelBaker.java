/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import java.util.function.Function;

public interface IForgeModelBaker
{
    @Nullable
    BakedModel bake(ResourceLocation location, ModelState state, Function<Material, TextureAtlasSprite> sprites);

    Function<Material, TextureAtlasSprite> getModelTextureGetter();
}
