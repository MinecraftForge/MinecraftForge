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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * A model that can be rendered in multiple {@link BlockRenderLayer}.
 */
/* TODO: reimplement
public final class MultiLayerModel implements IUnbakedModel
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final MultiLayerModel INSTANCE = new MultiLayerModel(ImmutableMap.of());

    private final ImmutableMap<Optional<BlockRenderLayer>, ResourceLocation> models;

    public MultiLayerModel(ImmutableMap<Optional<BlockRenderLayer>, ResourceLocation> models)
    {
        this.models = models;
    }
    
    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.copyOf(models.values());
    }

    @Override
    public Collection<Material> func_225614_a_(Function<ResourceLocation, IUnbakedModel> p_225614_1_, Set<com.mojang.datafixers.util.Pair<String, String>> p_225614_2_)
    {
        return Collections.emptyList();
    }


    private static ImmutableMap<Optional<BlockRenderLayer>, IBakedModel> buildModels(ImmutableMap<Optional<BlockRenderLayer>, ResourceLocation> models, IModelTransform sprite, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ResourceLocation p_225613_4_)
    {
        ImmutableMap.Builder<Optional<BlockRenderLayer>, IBakedModel> builder = ImmutableMap.builder();
        for(Optional<BlockRenderLayer> key : models.keySet())
        {
        	IUnbakedModel model = ModelLoader.defaultModelGetter().apply(models.get(key));
            builder.put(key, model.func_225613_a_(bakery, spriteGetter, sprite, p_225613_4_));
        }
        return builder.build();
    }

    @Nullable
    @Override
    public IBakedModel func_225613_a_(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform sprite, ResourceLocation p_225613_4_)
    {
        IUnbakedModel missing = ModelLoader.instance().getMissingModel();
        return new MultiLayerBakedModel(
            buildModels(models, sprite, bakery, spriteGetter, p_225613_4_),
            missing.func_225613_a_(bakery, spriteGetter, sprite, p_225613_4_),
            PerspectiveMapWrapper.getTransforms(sprite)
        );
    }

    public MultiLayerModel process(ImmutableMap<String, String> customData)
    {
        ImmutableMap.Builder<Optional<BlockRenderLayer>, ResourceLocation> builder = ImmutableMap.builder();
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
        ImmutableMap<Optional<BlockRenderLayer>, ResourceLocation> models = builder.build();
        if(models.isEmpty()) return INSTANCE;
        return new MultiLayerModel(models);
    }

    private ResourceLocation getLocation(String json)
    {
        JsonElement e = new JsonParser().parse(json);
        if(e.isJsonPrimitive() && e.getAsJsonPrimitive().isString())
        {
            return new ModelResourceLocation(e.getAsString());
        }
        LOGGER.fatal("Expect ModelResourceLocation, got: {}", json);
        return new ModelResourceLocation("builtin/missing", "missing");
    }

    private static final class MultiLayerBakedModel implements IBakedModel
    {
        private final ImmutableMap<Optional<BlockRenderLayer>, IBakedModel> models;
        private final ImmutableMap<TransformType, TransformationMatrix> cameraTransforms;
        private final IBakedModel base;
        private final IBakedModel missing;

        public MultiLayerBakedModel(ImmutableMap<Optional<BlockRenderLayer>, IBakedModel> models, IBakedModel missing, ImmutableMap<TransformType, TransformationMatrix> cameraTransforms)
        {
            this.models = models;
            this.cameraTransforms = cameraTransforms;
            this.missing = missing;
            this.base = models.getOrDefault(Optional.empty(), missing);
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
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
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
        {
            return PerspectiveMapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType);
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return ItemOverrideList.EMPTY;
        }
    }
}
*/