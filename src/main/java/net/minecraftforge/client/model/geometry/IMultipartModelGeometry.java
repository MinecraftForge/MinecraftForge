/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.geometry;

import com.google.common.collect.Sets;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public interface IMultipartModelGeometry<T extends IMultipartModelGeometry<T>> extends ISimpleModelGeometry<T>
{
    @Override
    Collection<? extends IModelGeometryPart> getParts();

    Optional<? extends IModelGeometryPart> getPart(String name);

    @Override
    default void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format)
    {
        getParts().stream().filter(part -> owner.getPartVisibility(part))
                .forEach(part -> part.addQuads(owner, modelBuilder, bakery, spriteGetter, sprite, format));
    }

    @Override
    default Collection<ResourceLocation> getTextureDependencies(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
    {
        Set<ResourceLocation> combined = Sets.newHashSet();
        for (IModelGeometryPart part : getParts())
            combined.addAll(part.getTextureDependencies(owner, modelGetter, missingTextureErrors));
        return combined;
    }
}
