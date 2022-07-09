/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A model composed of multiple sub-models which are picked based on the {@link ItemTransforms.TransformType} being used.
 */
public class SeparateTransformsModel implements IUnbakedGeometry<SeparateTransformsModel>
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final BlockModel baseModel;
    private final ImmutableMap<ItemTransforms.TransformType, BlockModel> perspectives;
    private final boolean deprecatedLoader;

    public SeparateTransformsModel(BlockModel baseModel, ImmutableMap<ItemTransforms.TransformType, BlockModel> perspectives)
    {
        this(baseModel, perspectives, false);
    }

    private SeparateTransformsModel(BlockModel baseModel, ImmutableMap<ItemTransforms.TransformType, BlockModel> perspectives, boolean deprecatedLoader)
    {
        this.baseModel = baseModel;
        this.perspectives = perspectives;
        this.deprecatedLoader = deprecatedLoader;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        if (deprecatedLoader)
            LOGGER.warn("Model \"" + modelLocation + "\" is using the deprecated loader \"forge:separate-perspective\" instead of \"forge:separate_transforms\". This loader will be removed in 1.20.");

        return new Baked(
                context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(),
                spriteGetter.apply(context.getMaterial("particle")), overrides,
                baseModel.bake(bakery, baseModel, spriteGetter, modelState, modelLocation, context.useBlockLight()),
                ImmutableMap.copyOf(Maps.transformValues(perspectives, value -> {
                    return value.bake(bakery, value, spriteGetter, modelState, modelLocation, context.useBlockLight());
                }))
        );
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<Material> textures = Sets.newHashSet();
        if (context.hasMaterial("particle"))
            textures.add(context.getMaterial("particle"));
        textures.addAll(baseModel.getMaterials(modelGetter, missingTextureErrors));
        for (BlockModel model : perspectives.values())
            textures.addAll(model.getMaterials(modelGetter, missingTextureErrors));
        return textures;
    }

    public static class Baked implements IDynamicBakedModel
    {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final boolean isSideLit;
        private final TextureAtlasSprite particle;
        private final ItemOverrides overrides;
        private final BakedModel baseModel;
        private final ImmutableMap<ItemTransforms.TransformType, BakedModel> perspectives;

        public Baked(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, BakedModel baseModel, ImmutableMap<ItemTransforms.TransformType, BakedModel> perspectives)
        {
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.isSideLit = isSideLit;
            this.particle = particle;
            this.overrides = overrides;
            this.baseModel = baseModel;
            this.perspectives = perspectives;
        }

        @NotNull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
        {
            return baseModel.getQuads(state, side, rand, data, renderType);
        }

        @Override
        public boolean useAmbientOcclusion()
        {
            return isAmbientOcclusion;
        }

        @Override
        public boolean isGui3d()
        {
            return isGui3d;
        }

        @Override
        public boolean usesBlockLight()
        {
            return isSideLit;
        }

        @Override
        public boolean isCustomRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon()
        {
            return particle;
        }

        @Override
        public ItemOverrides getOverrides()
        {
            return overrides;
        }

        @Override
        public ItemTransforms getTransforms()
        {
            return ItemTransforms.NO_TRANSFORMS;
        }

        @Override
        public BakedModel applyTransform(ItemTransforms.TransformType cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform)
        {
            if (perspectives.containsKey(cameraTransformType))
            {
                BakedModel p = perspectives.get(cameraTransformType);
                return p.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
            }
            return baseModel.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
        {
            return baseModel.getRenderTypes(state, rand, data);
        }
    }

    public static final class Loader implements IGeometryLoader<SeparateTransformsModel>
    {
        public static final Loader INSTANCE = new Loader(false);
        @Deprecated(forRemoval = true, since = "1.19")
        public static final Loader INSTANCE_DEPRECATED = new Loader(true);

        private final boolean deprecated;

        private Loader(boolean deprecated)
        {
            this.deprecated = deprecated;
        }

        @Override
        public SeparateTransformsModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext)
        {
            BlockModel baseModel = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "base"), BlockModel.class);

            JsonObject perspectiveData = GsonHelper.getAsJsonObject(jsonObject, "perspectives");

            Map<ItemTransforms.TransformType, BlockModel> perspectives = new HashMap<>();
            for (ItemTransforms.TransformType transform : ItemTransforms.TransformType.values())
            {
                if (perspectiveData.has(transform.getSerializeName()))
                {
                    BlockModel perspectiveModel = deserializationContext.deserialize(GsonHelper.getAsJsonObject(perspectiveData, transform.getSerializeName()), BlockModel.class);
                    perspectives.put(transform, perspectiveModel);
                }
            }

            return new SeparateTransformsModel(baseModel, ImmutableMap.copyOf(perspectives), deprecated);
        }
    }
}
