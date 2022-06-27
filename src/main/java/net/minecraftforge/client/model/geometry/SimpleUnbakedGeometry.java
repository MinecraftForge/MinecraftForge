/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.geometry;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.IModelBuilder;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/**
 * Base class for implementations of {@link IUnbakedGeometry} which do not wish to handle model creation themselves,
 * instead supplying {@linkplain BakedQuad baked quads} through a builder.
 */
public abstract class SimpleUnbakedGeometry<T extends SimpleUnbakedGeometry<T>> implements IUnbakedGeometry<T>
{
    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));

        var renderTypeHint = context.getRenderTypeHint();
        var renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;
        IModelBuilder<?> builder = IModelBuilder.of(context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(),
                context.getTransforms(), overrides, particle, renderTypes);

        addQuads(context, builder, bakery, spriteGetter, modelState, modelLocation);

        return builder.build();
    }

    protected abstract void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation);

    @Override
    public abstract Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors);
}
