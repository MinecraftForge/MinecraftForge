/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;
import net.minecraftforge.client.textures.UnitTextureAtlasSprite;

/**
 * A completely empty model with no quads or texture dependencies.
 * <p>
 * You can access it as a {@link BakedModel}, an {@link IUnbakedGeometry} or an {@link IGeometryLoader}.
 */
public class EmptyModel extends SimpleUnbakedGeometry<EmptyModel>
{
    public static final BakedModel BAKED = new Baked();
    public static final EmptyModel INSTANCE = new EmptyModel();
    public static final IGeometryLoader<EmptyModel> LOADER = (json, ctx) -> INSTANCE;

    private EmptyModel()
    {
    }

    @Override
    protected void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation)
    {
        // NO-OP
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        return BAKED;
    }

    private static class Baked extends SimpleBakedModel
    {
        private static final Material MISSING_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());

        public Baked()
        {
            super(List.of(), Map.of(), false, false, false, UnitTextureAtlasSprite.INSTANCE, ItemTransforms.NO_TRANSFORMS, ItemOverrides.EMPTY, RenderTypeGroup.EMPTY);
        }

        @Override
        public TextureAtlasSprite getParticleIcon()
        {
            return MISSING_TEXTURE.sprite();
        }
    }
}
