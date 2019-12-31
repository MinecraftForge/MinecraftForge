/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.client.model;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;

import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * A model that can be rendered in multiple {@link RenderType}.
 */
public final class MultiLayerModel implements IModelGeometry<MultiLayerModel>
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final ImmutableMap<RenderType, IUnbakedModel> models;
    @Nullable
    private final IUnbakedModel base;

    public MultiLayerModel(@Nullable IUnbakedModel base, ImmutableMap<RenderType, IUnbakedModel> models)
    {
        this.base = base;
        this.models = models;
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<Material> materials = Sets.newHashSet();
        if (base != null)
            materials.addAll(base.func_225614_a_(modelGetter, missingTextureErrors));
        for (IUnbakedModel m : models.values())
            materials.addAll(m.func_225614_a_(modelGetter, missingTextureErrors));
        return Collections.emptyList();
    }

    private static ImmutableMap<RenderType, IBakedModel> buildModels(ImmutableMap<RenderType, IUnbakedModel> models, IModelTransform modelTransform, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ResourceLocation modelLocation)
    {
        ImmutableMap.Builder<RenderType, IBakedModel> builder = ImmutableMap.builder();
        for(Map.Entry<RenderType, IUnbakedModel> entry : models.entrySet())
        {
            builder.put(entry.getKey(), entry.getValue().func_225613_a_(bakery, spriteGetter, modelTransform, modelLocation));
        }
        return builder.build();
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
    {
        IUnbakedModel missing = ModelLoader.instance().getMissingModel();
        return new MultiLayerBakedModel(
            base != null ? base.func_225613_a_(bakery, spriteGetter, modelTransform, modelLocation) : null,
            buildModels(models, modelTransform, bakery, spriteGetter, modelLocation),
            missing.func_225613_a_(bakery, spriteGetter, modelTransform, modelLocation),
            PerspectiveMapWrapper.getTransforms(modelTransform)
        );
    }

    private static final class MultiLayerBakedModel implements IBakedModel
    {
        private final ImmutableMap<RenderType, IBakedModel> models;
        private final ImmutableMap<TransformType, TransformationMatrix> cameraTransforms;
        private final IBakedModel base;
        private final IBakedModel missing;

        public MultiLayerBakedModel(@Nullable IBakedModel base, ImmutableMap<RenderType, IBakedModel> models, IBakedModel missing, ImmutableMap<TransformType, TransformationMatrix> cameraTransforms)
        {
            this.models = models;
            this.cameraTransforms = cameraTransforms;
            this.missing = missing;
            this.base = base != null ? base : missing;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
        {
            return getQuads(state, side, rand, EmptyModelData.INSTANCE);
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
        {
            RenderType layer = MinecraftForgeClient.getRenderLayer();
            if (layer == null)
            {
                ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                for (IBakedModel model : models.values())
                {
                    builder.addAll(model.getQuads(state, side, rand));
                }
                return builder.build();
            }
            // assumes that child model will handle this state properly. FIXME?
            List<BakedQuad> quads = models.getOrDefault(layer, missing).getQuads(state, side, rand, extraData);
            return quads;
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return base.isAmbientOcclusion();
        }

        @Override
        public boolean isAmbientOcclusion(BlockState state)
        {
            return base.isAmbientOcclusion(state);
        }

        @Override
        public boolean isGui3d()
        {
            return base.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return base.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return base.getParticleTexture();
        }

        @Override
        public boolean doesHandlePerspectives()
        {
            return true;
        }

        @Override
        public IBakedModel handlePerspective(TransformType cameraTransformType, MatrixStack mat)
        {
            return PerspectiveMapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType, mat);
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return ItemOverrideList.EMPTY;
        }
    }

    public static final class Loader implements IModelLoader<MultiLayerModel>
    {
        public static final Loader INSTANCE = new Loader();

        private Loader() {}

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {

        }

        @Override
        public MultiLayerModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
        {
            ImmutableMap.Builder<RenderType, IUnbakedModel> builder = ImmutableMap.builder();
            JsonObject layersObject = JSONUtils.getJsonObject(modelContents, "layers");
            BlockModel base = null;
            if(layersObject.has("base"))
            {
                base = deserializationContext.deserialize(JSONUtils.getJsonObject(layersObject, "base"), BlockModel.class);
            }
            for(RenderType layer : RenderType.func_228661_n_()) // block layers
            {
                String layerName = layer.toString(); // mc overrides toString to return the ID for the layer
                if(layersObject.has(layerName))
                {
                    builder.put(layer, deserializationContext.deserialize(JSONUtils.getJsonObject(layersObject, layerName), BlockModel.class));
                }
            }
            ImmutableMap<RenderType, IUnbakedModel> models = builder.build();
            return new MultiLayerModel(base, models);
        }
    }
}