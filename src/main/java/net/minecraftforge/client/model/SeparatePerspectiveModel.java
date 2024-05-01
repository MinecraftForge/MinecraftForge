/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;

public class SeparatePerspectiveModel implements IModelGeometry<SeparatePerspectiveModel>
{
    private final BlockModel baseModel;
    private final ImmutableMap<ItemTransforms.TransformType, BlockModel> perspectives;

    public SeparatePerspectiveModel(BlockModel baseModel, ImmutableMap<ItemTransforms.TransformType, BlockModel> perspectives)
    {
        this.baseModel = baseModel;
        this.perspectives = perspectives;
    }

    @Override
    public net.minecraft.client.resources.model.BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        return new BakedModel(
                owner.useSmoothLighting(), owner.isShadedInGui(), owner.isSideLit(),
                spriteGetter.apply(owner.resolveTexture("particle")), overrides,
                baseModel.bake(bakery, baseModel, spriteGetter, modelTransform, modelLocation, owner.isSideLit()),
                ImmutableMap.copyOf(Maps.transformValues(perspectives, value -> {
                    return value.bake(bakery, value, spriteGetter, modelTransform, modelLocation, owner.isSideLit());
                }))
        );
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<Material> textures = Sets.newHashSet();
        textures.addAll(baseModel.getMaterials(modelGetter, missingTextureErrors));
        for(BlockModel model : perspectives.values())
            textures.addAll(model.getMaterials(modelGetter, missingTextureErrors));
        return textures;
    }

    public static class BakedModel implements net.minecraft.client.resources.model.BakedModel
    {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final boolean isSideLit;
        private final TextureAtlasSprite particle;
        private final ItemOverrides overrides;
        private final net.minecraft.client.resources.model.BakedModel baseModel;
        private final ImmutableMap<ItemTransforms.TransformType, net.minecraft.client.resources.model.BakedModel> perspectives;

        public BakedModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, net.minecraft.client.resources.model.BakedModel baseModel, ImmutableMap<ItemTransforms.TransformType, net.minecraft.client.resources.model.BakedModel> perspectives)
        {
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.isSideLit = isSideLit;
            this.particle = particle;
            this.overrides = overrides;
            this.baseModel = baseModel;
            this.perspectives = perspectives;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
        {
            return Collections.emptyList();
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
        public boolean doesHandlePerspectives()
        {
            return true;
        }

        @Override
        public ItemTransforms getTransforms()
        {
            return ItemTransforms.NO_TRANSFORMS;
        }

        @Override
        public net.minecraft.client.resources.model.BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack poseStack)
        {
            if (perspectives.containsKey(cameraTransformType))
            {
                net.minecraft.client.resources.model.BakedModel p = perspectives.get(cameraTransformType);
                return p.handlePerspective(cameraTransformType, poseStack);
            }
            return baseModel.handlePerspective(cameraTransformType, poseStack);
        }
    }

    public static class Loader implements IModelLoader<SeparatePerspectiveModel>
    {
        public static final Loader INSTANCE = new Loader();

        public static final ImmutableBiMap<String, ItemTransforms.TransformType> PERSPECTIVES = ImmutableBiMap.<String, ItemTransforms.TransformType>builder()
                .put("none", ItemTransforms.TransformType.NONE)
                .put("third_person_left_hand", ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
                .put("third_person_right_hand", ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
                .put("first_person_left_hand", ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND)
                .put("first_person_right_hand", ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
                .put("head", ItemTransforms.TransformType.HEAD)
                .put("gui", ItemTransforms.TransformType.GUI)
                .put("ground", ItemTransforms.TransformType.GROUND)
                .put("fixed", ItemTransforms.TransformType.FIXED)
                .build();

        @Override
        public void onResourceManagerReload(final ResourceManager p_10758_) {

        }

        @Override
        public SeparatePerspectiveModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
        {
            BlockModel baseModel = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "base"), BlockModel.class);

            JsonObject perspectiveData = GsonHelper.getAsJsonObject(modelContents, "perspectives");

            ImmutableMap.Builder<ItemTransforms.TransformType, BlockModel> perspectives = ImmutableMap.builder();
            for(Map.Entry<String, ItemTransforms.TransformType> perspective : PERSPECTIVES.entrySet())
            {
                if (perspectiveData.has(perspective.getKey()))
                {
                    BlockModel perspectiveModel = deserializationContext.deserialize(GsonHelper.getAsJsonObject(perspectiveData, perspective.getKey()), BlockModel.class);
                    perspectives.put(perspective.getValue(), perspectiveModel);
                }
            }

            return new SeparatePerspectiveModel(baseModel, perspectives.build());
        }
    }
}
