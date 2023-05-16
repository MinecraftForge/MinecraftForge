/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.geometry;

import java.util.Set;
import java.util.function.Function;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

/**
 * General interface for any model that can be baked, superset of vanilla {@link UnbakedModel}.
 * <p>
 * Instances of this class ar usually created via {@link IGeometryLoader}.
 *
 * @see IGeometryLoader
 * @see IGeometryBakingContext
 */
public interface IUnbakedGeometry<T extends IUnbakedGeometry<T>>
{
    BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation);

    /**
     * Resolve parents of nested {@link BlockModel}s which are later used in
     * {@link IUnbakedGeometry#bake(IGeometryBakingContext, ModelBaker, Function, ModelState, ItemOverrides, ResourceLocation)}
     * via {@link BlockModel#resolveParents(Function)}
     */
    default void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
    {

    }

    /**
     * {@return a set of all the components whose visibility may be configured via {@link IGeometryBakingContext}}
     */
    default Set<String> getConfigurableComponentNames()
    {
        return Set.of();
    }
}
