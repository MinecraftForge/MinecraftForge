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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.*;
import java.util.function.Function;

public class NewMultiLayerModel implements IModelGeometry<NewMultiLayerModel>
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final ImmutableMap<BlockRenderLayer, IUnbakedModel> models;

    public NewMultiLayerModel(ImmutableMap<BlockRenderLayer, IUnbakedModel> models)
    {
        this.models = models;
    }

    @Override
    public Collection<ResourceLocation> getTextureDependencies(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
    {
        Set<ResourceLocation> materials = Sets.newHashSet();
        materials.add(new ResourceLocation(owner.resolveTexture("particle")));
        for (IUnbakedModel m : models.values())
            materials.addAll(m.getTextures(modelGetter, missingTextureErrors));
        return materials;
    }

    private static ImmutableMap<BlockRenderLayer, IBakedModel> buildModels(ImmutableMap<BlockRenderLayer, IUnbakedModel> models, ISprite sprite, ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, VertexFormat format)
    {
        ImmutableMap.Builder<BlockRenderLayer, IBakedModel> builder = ImmutableMap.builder();
        for(Map.Entry<BlockRenderLayer, IUnbakedModel> entry : models.entrySet())
        {
            builder.put(entry.getKey(), entry.getValue().bake(bakery, spriteGetter, sprite, format));
        }
        return builder.build();
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format, ItemOverrideList overrides)
    {
        @SuppressWarnings("deprecation")
        IUnbakedModel missing = ModelLoaderRegistry.getMissingModel();

        return new NewMultiLayerModel.MultiLayerBakedModel(
                owner.useSmoothLighting(), owner.isShadedInGui(),
                spriteGetter.apply(new ResourceLocation(owner.resolveTexture("particle"))), overrides,
                buildModels(models, sprite, bakery, spriteGetter, format),
                missing.bake(bakery, spriteGetter, sprite, format),
                PerspectiveMapWrapper.getTransforms(new ModelStateComposition(owner.getCombinedState(), sprite.getState())));
    }

    private static final class MultiLayerBakedModel implements IBakedModel
    {
        private final ImmutableMap<BlockRenderLayer, IBakedModel> models;
        private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> cameraTransforms;
        protected final boolean ambientOcclusion;
        protected final boolean gui3d;
        protected final TextureAtlasSprite particle;
        protected final ItemOverrideList overrides;
        private final IBakedModel missing;

        public MultiLayerBakedModel(
                boolean ambientOcclusion, boolean isGui3d,  TextureAtlasSprite particle, ItemOverrideList overrides,
                ImmutableMap<BlockRenderLayer, IBakedModel> models, IBakedModel missing, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> cameraTransforms)
        {
            this.models = models;
            this.cameraTransforms = cameraTransforms;
            this.missing = missing;
            this.ambientOcclusion = ambientOcclusion;
            this.gui3d = isGui3d;
            this.particle = particle;
            this.overrides = overrides;
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
            return models.getOrDefault(layer, missing).getQuads(state, side, rand, extraData);
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return ambientOcclusion;
        }

        @Override
        public boolean isAmbientOcclusion(BlockState state)
        {
            return ambientOcclusion;
        }

        @Override
        public boolean isGui3d()
        {
            return gui3d;
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return particle;
        }

        @Override
        public boolean doesHandlePerspectives()
        {
            return true;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
        {
            return PerspectiveMapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType);
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return ItemOverrideList.EMPTY;
        }
    }

    public static final class Loader implements IModelLoader<NewMultiLayerModel>
    {
        public static final NewMultiLayerModel.Loader INSTANCE = new NewMultiLayerModel.Loader();

        private Loader() {}

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {

        }

        @Override
        public NewMultiLayerModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
        {
            ImmutableMap.Builder<BlockRenderLayer, IUnbakedModel> builder = ImmutableMap.builder();
            JsonObject layersObject = JSONUtils.getJsonObject(modelContents, "layers");
            for(BlockRenderLayer layer : BlockRenderLayer.values()) // block layers
            {
                String layerName = layer.toString(); // mc overrides toString to return the ID for the layer
                if(layersObject.has(layerName))
                {
                    builder.put(layer, deserializationContext.deserialize(JSONUtils.getJsonObject(layersObject, layerName), BlockModel.class));
                }
            }
            ImmutableMap<BlockRenderLayer, IUnbakedModel> models = builder.build();
            return new NewMultiLayerModel(models);
        }
    }
}
