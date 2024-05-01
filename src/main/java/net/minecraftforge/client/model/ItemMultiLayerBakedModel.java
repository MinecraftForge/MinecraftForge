/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import com.mojang.math.Transformation;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ItemMultiLayerBakedModel implements IDynamicBakedModel
{
    private final boolean smoothLighting;
    private final boolean shadedInGui;
    private final boolean sideLit;
    private final TextureAtlasSprite particle;
    private final ItemOverrides overrides;
    private final ImmutableList<Pair<BakedModel, RenderType>> layerModels;
    private final ImmutableMap<ItemTransforms.TransformType, Transformation> cameraTransforms;

    public ItemMultiLayerBakedModel(boolean smoothLighting, boolean shadedInGui, boolean sideLit,
                                    TextureAtlasSprite particle, ItemOverrides overrides,
                                    ImmutableMap<ItemTransforms.TransformType, Transformation> cameraTransforms,
                                    ImmutableList<Pair<BakedModel, RenderType>> layerModels)
    {
        this.smoothLighting = smoothLighting;
        this.shadedInGui = shadedInGui;
        this.sideLit = sideLit;
        this.particle = particle;
        this.overrides = overrides;
        this.layerModels = layerModels;
        this.cameraTransforms = cameraTransforms;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData modelData)
    {
        List<BakedQuad> quads = Lists.newArrayList();
        layerModels.forEach(lm -> quads.addAll(lm.getFirst().getQuads(state, side, rand, modelData)));
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return smoothLighting;
    }

    @Override
    public boolean isGui3d()
    {
        return shadedInGui;
    }

    @Override
    public boolean usesBlockLight()
    {
        return sideLit;
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
    public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack poseStack)
    {
        return PerspectiveMapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType, poseStack);
    }

    //@Override
    public boolean isLayered()
    {
        return true;
    }

    //@Override
    public List<Pair<BakedModel, RenderType>> getLayerModels(ItemStack itemStack, boolean fabulous)
    {
        return layerModels;
    }

    public static Builder builder(IModelConfiguration owner, TextureAtlasSprite particle, ItemOverrides overrides,
                                  ImmutableMap<ItemTransforms.TransformType, Transformation> cameraTransforms)
    {
        return new Builder(owner, particle, overrides, cameraTransforms);
    }

    public static class Builder
    {
        private final ImmutableList.Builder<Pair<BakedModel, RenderType>> builder = ImmutableList.builder();
        private final List<BakedQuad> quads = Lists.newArrayList();
        private final ItemOverrides overrides;
        private final ImmutableMap<ItemTransforms.TransformType, Transformation> cameraTransforms;
        private final IModelConfiguration owner;
        private TextureAtlasSprite particle;
        private RenderType lastRt = null;

        private Builder(IModelConfiguration owner, TextureAtlasSprite particle, ItemOverrides overrides,
                        ImmutableMap<ItemTransforms.TransformType, Transformation> cameraTransforms)
        {
            this.owner = owner;
            this.particle = particle;
            this.overrides = overrides;
            this.cameraTransforms = cameraTransforms;
        }

        private void addLayer(ImmutableList.Builder<Pair<BakedModel, RenderType>> builder, List<BakedQuad> quads, RenderType rt)
        {
            BakedModel model = new BakedItemModel(ImmutableList.copyOf(quads), particle, ImmutableMap.of(), ItemOverrides.EMPTY, true, owner.isSideLit());
            builder.add(Pair.of(model, rt));
        }

        private void flushQuads(RenderType rt)
        {
            if (rt != lastRt)
            {
                if (quads.size() > 0)
                {
                    addLayer(builder, quads, lastRt);
                    quads.clear();
                }
                lastRt = rt;
            }
        }

        public Builder setParticle(TextureAtlasSprite particleSprite)
        {
            this.particle = particleSprite;
            return this;
        }

        public Builder addQuads(RenderType rt, BakedQuad... quadsToAdd)
        {
            flushQuads(rt);
            Collections.addAll(quads, quadsToAdd);
            return this;
        }

        public Builder addQuads(RenderType rt, Collection<BakedQuad> quadsToAdd)
        {
            flushQuads(rt);
            quads.addAll(quadsToAdd);
            return this;
        }

        public BakedModel build()
        {
            if (quads.size() > 0)
            {
                addLayer(builder, quads, lastRt);
            }
            return new ItemMultiLayerBakedModel(owner.useSmoothLighting(), owner.isShadedInGui(), owner.isSideLit(),
                    particle, overrides, cameraTransforms, builder.build());
        }
    }
}
