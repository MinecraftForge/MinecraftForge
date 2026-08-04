/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.tuple.Pair;

public interface IForgeBakedModel
{
    default IBakedModel getBakedModel()
    {
        return (IBakedModel) this;
    }

    @Nonnull
    default List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        return getBakedModel().getQuads(state, side, rand);
    }

    default boolean isAmbientOcclusion(BlockState state) { return getBakedModel().isAmbientOcclusion(); }

    /**
     * Override to tell the new model loader that it shouldn't wrap this model
     */
    default boolean doesHandlePerspectives() { return false; }

    /*
     * Returns the pair of the model for the given perspective, and the matrix that
     * should be applied to the GL state before rendering it (matrix may be null).
     */
    default Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        return net.minecraftforge.client.ForgeHooksClient.handlePerspective(getBakedModel(), cameraTransformType);
    }

    default @Nonnull IModelData getModelData(@Nonnull IEnviromentBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
    {
        return tileData;
    }

    default TextureAtlasSprite getParticleTexture(@Nonnull IModelData data)
    {
        return getBakedModel().getParticleTexture();
    }
}
