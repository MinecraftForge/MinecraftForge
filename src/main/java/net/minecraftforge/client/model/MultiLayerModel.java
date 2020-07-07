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

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.*;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.MinecraftForgeClient;

import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A model that can be rendered in multiple {@link RenderType}.
 */
public final class MultiLayerModel implements IModelGeometry<MultiLayerModel>
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final ImmutableList<Pair<RenderType, IUnbakedModel>> models;
    private final boolean convertRenderTypes;

    public MultiLayerModel(Map<RenderType, IUnbakedModel> models)
    {
        this(models.entrySet().stream().map(kv -> Pair.of(kv.getKey(), kv.getValue())).collect(ImmutableList.toImmutableList()), true);
    }

    public MultiLayerModel(ImmutableList<Pair<RenderType, IUnbakedModel>> models, boolean convertRenderTypes)
    {
        this.models = models;
        this.convertRenderTypes = convertRenderTypes;
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<RenderMaterial> materials = Sets.newHashSet();
        materials.add(owner.resolveTexture("particle"));
        for (Pair<RenderType, IUnbakedModel> m : models)
            materials.addAll(m.getSecond().getTextures(modelGetter, missingTextureErrors));
        return materials;
    }

    private static ImmutableList<Pair<RenderType, IBakedModel>> buildModels(List<Pair<RenderType, IUnbakedModel>> models, IModelTransform modelTransform,
                                                                            ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, ResourceLocation modelLocation)
    {
        ImmutableList.Builder<Pair<RenderType, IBakedModel>> builder = ImmutableList.builder();
        for(Pair<RenderType, IUnbakedModel> entry : models)
        {
            builder.add(Pair.of(entry.getFirst(), entry.getSecond().bakeModel(bakery, spriteGetter, modelTransform, modelLocation)));
        }
        return builder.build();
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
    {
        IUnbakedModel missing = ModelLoader.instance().getMissingModel();

        return new MultiLayerBakedModel(
                owner.useSmoothLighting(), owner.isShadedInGui(), owner.isSideLit(),
                spriteGetter.apply(owner.resolveTexture("particle")), overrides, convertRenderTypes,
                missing.bakeModel(bakery, spriteGetter, modelTransform, modelLocation),
                buildModels(models, modelTransform, bakery, spriteGetter, modelLocation),
                PerspectiveMapWrapper.getTransforms(new ModelTransformComposition(owner.getCombinedTransform(), modelTransform))
        );
    }

    private static final class MultiLayerBakedModel implements IBakedModel
    {
        private final ImmutableMap<RenderType, IBakedModel> models;
        private final ImmutableMap<TransformType, TransformationMatrix> cameraTransforms;
        protected final boolean ambientOcclusion;
        protected final boolean gui3d;
        protected final boolean isSideLit;
        protected final TextureAtlasSprite particle;
        protected final ItemOverrideList overrides;
        private final IBakedModel missing;
        private final boolean convertRenderTypes;
        private final List<Pair<IBakedModel, RenderType>> itemLayers;

        public MultiLayerBakedModel(
                boolean ambientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrideList overrides,
                boolean convertRenderTypes, IBakedModel missing, List<Pair<RenderType, IBakedModel>> models,
                ImmutableMap<TransformType, TransformationMatrix> cameraTransforms)
        {
            this.isSideLit = isSideLit;
            this.cameraTransforms = cameraTransforms;
            this.missing = missing;
            this.ambientOcclusion = ambientOcclusion;
            this.gui3d = isGui3d;
            this.particle = particle;
            this.overrides = overrides;
            this.convertRenderTypes = convertRenderTypes;
            this.models = ImmutableMap.copyOf(models.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            this.itemLayers = models.stream().map(kv -> {
                RenderType rt = kv.getFirst();
                if (convertRenderTypes) rt = ITEM_RENDER_TYPE_MAPPING.getOrDefault(rt, rt);
                return Pair.of(kv.getSecond(), rt);
            }).collect(Collectors.toList());
        }

        @Deprecated
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
                    builder.addAll(model.getQuads(state, side, rand, extraData));
                }
                return builder.build();
            }
            // support for item layer rendering
            if (state == null && convertRenderTypes)
                layer = ITEM_RENDER_TYPE_MAPPING.inverse().getOrDefault(layer, layer);
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
        public boolean func_230044_c_()
        {
            return isSideLit;
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
        public IBakedModel handlePerspective(TransformType cameraTransformType, MatrixStack mat)
        {
            return PerspectiveMapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType, mat);
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return ItemOverrideList.EMPTY;
        }

        @Override
        public boolean isLayered()
        {
            return true;
        }

        @Override
        public List<Pair<IBakedModel, RenderType>> getLayerModels(ItemStack itemStack, boolean fabulous)
        {
            return itemLayers;
        }

        public static BiMap<RenderType, RenderType> ITEM_RENDER_TYPE_MAPPING = HashBiMap.create();
        static {
            ITEM_RENDER_TYPE_MAPPING.put(RenderType.getSolid(), ForgeRenderTypes.ITEM_LAYERED_SOLID.get());
            ITEM_RENDER_TYPE_MAPPING.put(RenderType.getCutout(), ForgeRenderTypes.ITEM_LAYERED_CUTOUT.get());
            ITEM_RENDER_TYPE_MAPPING.put(RenderType.getCutoutMipped(), ForgeRenderTypes.ITEM_LAYERED_CUTOUT_MIPPED.get());
            ITEM_RENDER_TYPE_MAPPING.put(RenderType.getTranslucent(), ForgeRenderTypes.ITEM_LAYERED_TRANSLUCENT.get());
        }
    }

    public static final class Loader implements IModelLoader<MultiLayerModel>
    {
        public static final ImmutableBiMap<String, RenderType> BLOCK_LAYERS = ImmutableBiMap.<String, RenderType>builder()
                .put("solid", RenderType.getSolid())
                .put("cutout", RenderType.getCutout())
                .put("cutout_mipped", RenderType.getCutoutMipped())
                .put("translucent", RenderType.getTranslucent())
                .put("tripwire", RenderType.func_241715_r_())
                .build();

        public static final Loader INSTANCE = new Loader();

        private Loader() {}

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {

        }

        @Override
        public MultiLayerModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
        {
            ImmutableList.Builder<Pair<RenderType, IUnbakedModel>> builder = ImmutableList.builder();
            JsonObject layersObject = JSONUtils.getJsonObject(modelContents, "layers");
            for(Map.Entry<String, RenderType> layer : BLOCK_LAYERS.entrySet()) // block layers
            {
                String layerName = layer.getKey(); // mc overrides toString to return the ID for the layer
                if(layersObject.has(layerName))
                {
                    builder.add(Pair.of(layer.getValue(), deserializationContext.deserialize(JSONUtils.getJsonObject(layersObject, layerName), BlockModel.class)));
                }
            }
            boolean convertRenderTypes = JSONUtils.getBoolean(modelContents, "convert_render_types", true);
            return new MultiLayerModel(builder.build(), convertRenderTypes);
        }
    }
}
