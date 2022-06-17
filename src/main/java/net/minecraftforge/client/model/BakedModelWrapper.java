/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BakedModelWrapper<T extends BakedModel> implements BakedModel
{
    protected final T originalModel;

    public BakedModelWrapper(T originalModel)
    {
        this.originalModel = originalModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand)
    {
        return originalModel.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean useAmbientOcclusion(BlockState state)
    {
        return originalModel.useAmbientOcclusion(state);
    }

    @Override
    public boolean isGui3d()
    {
        return originalModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight()
    {
        return originalModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer()
    {
        return originalModel.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return originalModel.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms()
    {
        return originalModel.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides()
    {
        return originalModel.getOverrides();
    }

    @Override
    public boolean doesHandlePerspectives()
    {
        return originalModel.doesHandlePerspectives();
    }

    @Override
    public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack poseStack)
    {
        return originalModel.handlePerspective(cameraTransformType, poseStack);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        return originalModel.getParticleIcon(data);
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull IModelData extraData)
    {
        return originalModel.getQuads(state, side, rand, extraData);
    }

    @NotNull
    @Override
    public IModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull IModelData modelData)
    {
        return originalModel.getModelData(level, pos, state, modelData);
    }
}
