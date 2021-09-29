/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.RenderType;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;

import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;

/**
 * A model that can be rendered in multiple {@link RenderType}.
 */
public final class MultiLayerModel implements IModelGeometry<MultiLayerModel>
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final ImmutableList<Pair<RenderType, UnbakedModel>> models;
    private final boolean convertRenderTypes;

    public MultiLayerModel(Map<RenderType, UnbakedModel> models)
    {
        this(models.entrySet().stream().map(kv -> Pair.of(kv.getKey(), kv.getValue())).collect(ImmutableList.toImmutableList()), true);
    }

    public MultiLayerModel(ImmutableList<Pair<RenderType, UnbakedModel>> models, boolean convertRenderTypes)
    {
        this.models = models;
        this.convertRenderTypes = convertRenderTypes;
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<Material> materials = Sets.newHashSet();
        materials.add(owner.resolveTexture("particle"));
        for (Pair<RenderType, UnbakedModel> m : models)
            materials.addAll(m.getSecond().getMaterials(modelGetter, missingTextureErrors));
        return materials;
    }

    private static ImmutableList<Pair<RenderType, BakedModel>> buildModels(List<Pair<RenderType, UnbakedModel>> models, ModelState modelTransform,
                                                                            ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ResourceLocation modelLocation)
    {
        ImmutableList.Builder<Pair<RenderType, BakedModel>> builder = ImmutableList.builder();
        for(Pair<RenderType, UnbakedModel> entry : models)
        {
            builder.add(Pair.of(entry.getFirst(), entry.getSecond().bake(bakery, spriteGetter, modelTransform, modelLocation)));
        }
        return builder.build();
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        return new MultiLayerBakedModel(
                owner.useSmoothLighting(), owner.isShadedInGui(), owner.isSideLit(),
                spriteGetter.apply(owner.resolveTexture("particle")), overrides, convertRenderTypes,
                buildModels(models, modelTransform, bakery, spriteGetter, modelLocation),
                PerspectiveMapWrapper.getTransforms(new CompositeModelState(owner.getCombinedTransform(), modelTransform))
        );
    }

    private static final class MultiLayerBakedModel implements IDynamicBakedModel
    {
        private final ImmutableMap<RenderType, BakedModel> models;
        private final ImmutableMap<TransformType, Transformation> cameraTransforms;
        protected final boolean ambientOcclusion;
        protected final boolean gui3d;
        protected final boolean isSideLit;
        protected final TextureAtlasSprite particle;
        protected final ItemOverrides overrides;
        private final List<BakedQuad> missing = ImmutableList.of();
        private final boolean convertRenderTypes;
        private final List<Pair<BakedModel, RenderType>> itemLayers;

        public MultiLayerBakedModel(
                boolean ambientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides,
                boolean convertRenderTypes, List<Pair<RenderType, BakedModel>> models,
                ImmutableMap<TransformType, Transformation> cameraTransforms)
        {
            this.isSideLit = isSideLit;
            this.cameraTransforms = cameraTransforms;
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

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
        {
            RenderType layer = MinecraftForgeClient.getRenderLayer();
            if (layer == null)
            {
                ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                for (BakedModel model : models.values())
                {
                    builder.addAll(model.getQuads(state, side, rand, extraData));
                }
                return builder.build();
            }
            // support for item layer rendering
            if (state == null && convertRenderTypes)
                layer = ITEM_RENDER_TYPE_MAPPING.inverse().getOrDefault(layer, layer);
            if (models.containsKey(layer))
                return models.get(layer).getQuads(state, side, rand, extraData);
            else
                return missing;
        }

        @Override
        public boolean useAmbientOcclusion()
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
        public boolean doesHandlePerspectives()
        {
            return true;
        }

        @Override
        public BakedModel handlePerspective(TransformType cameraTransformType, PoseStack poseStack)
        {
            return PerspectiveMapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType, poseStack);
        }

        @Override
        public ItemOverrides getOverrides()
        {
            return ItemOverrides.EMPTY;
        }

        @Override
        public boolean isLayered()
        {
            return true;
        }

        @Override
        public List<Pair<BakedModel, RenderType>> getLayerModels(ItemStack itemStack, boolean fabulous)
        {
            return itemLayers;
        }

        public static BiMap<RenderType, RenderType> ITEM_RENDER_TYPE_MAPPING = HashBiMap.create();
        // TODO ForgeRenderTypes
/*        static {
            ITEM_RENDER_TYPE_MAPPING.put(RenderType.solid(), ForgeRenderTypes.ITEM_LAYERED_SOLID.get());
            ITEM_RENDER_TYPE_MAPPING.put(RenderType.cutout(), ForgeRenderTypes.ITEM_LAYERED_CUTOUT.get());
            ITEM_RENDER_TYPE_MAPPING.put(RenderType.cutoutMipped(), ForgeRenderTypes.ITEM_LAYERED_CUTOUT_MIPPED.get());
            ITEM_RENDER_TYPE_MAPPING.put(RenderType.translucent(), ForgeRenderTypes.ITEM_LAYERED_TRANSLUCENT.get());
        }*/
    }

    public static final class Loader implements IModelLoader<MultiLayerModel>
    {
        public static final ImmutableBiMap<String, RenderType> BLOCK_LAYERS = ImmutableBiMap.<String, RenderType>builder()
                .put("solid", RenderType.solid())
                .put("cutout", RenderType.cutout())
                .put("cutout_mipped", RenderType.cutoutMipped())
                .put("translucent", RenderType.translucent())
                .put("tripwire", RenderType.tripwire())
                .build();

        public static final Loader INSTANCE = new Loader();

        private Loader() {}

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager)
        {

        }

        @Override
        public MultiLayerModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
        {
            ImmutableList.Builder<Pair<RenderType, UnbakedModel>> builder = ImmutableList.builder();
            JsonObject layersObject = GsonHelper.getAsJsonObject(modelContents, "layers");
            for(Map.Entry<String, RenderType> layer : BLOCK_LAYERS.entrySet()) // block layers
            {
                String layerName = layer.getKey(); // mc overrides toString to return the ID for the layer
                if(layersObject.has(layerName))
                {
                    builder.add(Pair.of(layer.getValue(), deserializationContext.deserialize(GsonHelper.getAsJsonObject(layersObject, layerName), BlockModel.class)));
                }
            }
            boolean convertRenderTypes = GsonHelper.getAsBoolean(modelContents, "convert_render_types", true);
            return new MultiLayerModel(builder.build(), convertRenderTypes);
        }
    }
}
