/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;
import java.util.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public final class MultiLayerModel implements IModel
{
    public static final MultiLayerModel INSTANCE = new MultiLayerModel(ImmutableMap.of());

    private final ImmutableMap<Optional<BlockRenderLayer>, ModelResourceLocation> models;

    public MultiLayerModel(ImmutableMap<Optional<BlockRenderLayer>, ModelResourceLocation> models)
    {
        this.models = models;
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.copyOf(models.values());
    }

    private static ImmutableMap<Optional<BlockRenderLayer>, IBakedModel> buildModels(ImmutableMap<Optional<BlockRenderLayer>, ModelResourceLocation> models, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        ImmutableMap.Builder<Optional<BlockRenderLayer>, IBakedModel> builder = ImmutableMap.builder();
        for(Optional<BlockRenderLayer> key : models.keySet())
        {
            IModel model = ModelLoaderRegistry.getModelOrLogError(models.get(key), "Couldn't load MultiLayerModel dependency: " + models.get(key));
            builder.put(key, model.bake(new ModelStateComposition(state, model.getDefaultState()), format, bakedTextureGetter));
        }
        return builder.build();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        IModel missing = ModelLoaderRegistry.getMissingModel();
        return new MultiLayerBakedModel(
            buildModels(models, state, format, bakedTextureGetter),
            missing.bake(missing.getDefaultState(), format, bakedTextureGetter),
            PerspectiveMapWrapper.getTransforms(state)
        );
    }

    @Override
    public MultiLayerModel process(ImmutableMap<String, String> customData)
    {
        ImmutableMap.Builder<Optional<BlockRenderLayer>, ModelResourceLocation> builder = ImmutableMap.builder();
        for(String key : customData.keySet())
        {
            if("base".equals(key))
            {
                builder.put(Optional.empty(), getLocation(customData.get(key)));
            }
            for(BlockRenderLayer layer : BlockRenderLayer.values())
            {
                if(layer.toString().equals(key))
                {
                    builder.put(Optional.of(layer), getLocation(customData.get(key)));
                }
            }
        }
        ImmutableMap<Optional<BlockRenderLayer>, ModelResourceLocation> models = builder.build();
        if(models.isEmpty()) return INSTANCE;
        return new MultiLayerModel(models);
    }

    private ModelResourceLocation getLocation(String json)
    {
        JsonElement e = new JsonParser().parse(json);
        if(e.isJsonPrimitive() && e.getAsJsonPrimitive().isString())
        {
            return new ModelResourceLocation(e.getAsString());
        }
        FMLLog.log.fatal("Expect ModelResourceLocation, got: {}", json);
        return new ModelResourceLocation("builtin/missing", "missing");
    }

    private static final class MultiLayerBakedModel implements IBakedModel
    {
        private final ImmutableMap<Optional<BlockRenderLayer>, IBakedModel> models;
        private final ImmutableMap<TransformType, TRSRTransformation> cameraTransforms;
        private final IBakedModel base;
        private final IBakedModel missing;

        public MultiLayerBakedModel(ImmutableMap<Optional<BlockRenderLayer>, IBakedModel> models, IBakedModel missing, ImmutableMap<TransformType, TRSRTransformation> cameraTransforms)
        {
            this.models = models;
            this.cameraTransforms = cameraTransforms;
            this.missing = missing;
            this.base = models.getOrDefault(Optional.empty(), missing);
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
        {
            BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
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
            return models.getOrDefault(Optional.of(layer), missing).getQuads(state, side, rand);
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return base.isAmbientOcclusion();
        }

        @Override
        public boolean isAmbientOcclusion(IBlockState state)
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
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            return PerspectiveMapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType);
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return ItemOverrideList.NONE;
        }
    }

    public static enum Loader implements ICustomModelLoader
    {
        INSTANCE;

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {}

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getResourceDomain().equals(ForgeVersion.MOD_ID) && (
                modelLocation.getResourcePath().equals("multi-layer") ||
                modelLocation.getResourcePath().equals("models/block/multi-layer") ||
                modelLocation.getResourcePath().equals("models/item/multi-layer"));
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation)
        {
            return MultiLayerModel.INSTANCE;
        }
    }
}
