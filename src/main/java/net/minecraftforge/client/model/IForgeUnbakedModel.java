/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.animation.IClip;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public interface IForgeUnbakedModel
{
    /**
     * @param spriteGetter Where textures will be looked up when baking
     * @param sprite Transforms to apply while baking. Usually will be an instance of {@link IModelState}.
     */
    @Nullable
    IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format);

    /**
     * Retrieves information about an animation clip in the model.
     * @param name The clip name
     * @return
     */
    default Optional<? extends IClip> getClip(String name) {
        return Optional.empty();
    }
}
