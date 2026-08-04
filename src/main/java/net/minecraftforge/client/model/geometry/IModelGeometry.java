/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.geometry;

import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * General interface for any model that can be baked, superset of vanilla {@link net.minecraft.client.renderer.model.IUnbakedModel}.
 * Models can be baked to different vertex formats and with different state.
 */
public interface IModelGeometry<T extends IModelGeometry<T>>
{
    default Collection<? extends IModelGeometryPart> getParts() {
        return Collections.emptyList();
    }

    default Optional<? extends IModelGeometryPart> getPart(String name) {
        return Optional.empty();
    }

    IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format, ItemOverrideList overrides);

    Collection<ResourceLocation> getTextureDependencies(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors);
}
