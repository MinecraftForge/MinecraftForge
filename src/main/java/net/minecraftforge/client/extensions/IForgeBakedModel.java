/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Extension interface for {@link IForgeBakedModel}.
 */
@SuppressWarnings("deprecation")
public interface IForgeBakedModel {
    private BakedModel self() {
        return (BakedModel)this;
    }

    /**
     * A null {@link RenderType} is used for the breaking overlay as well as non-standard rendering, so models should return all their quads.
     */
    @NotNull
    default List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        return self().getQuads(state, side, rand);
    }

    default boolean useAmbientOcclusion(BlockState state) {
        return self().useAmbientOcclusion();
    }

    default boolean useAmbientOcclusion(BlockState state, RenderType renderType) {
        return self().useAmbientOcclusion(state);
    }

    /**
     * Applies a transform for the given {@link ItemDisplayContext} and {@code applyLeftHandTransform}, and
     * returns the model to be rendered.
     */
    default BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        self().getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
        return self();
    }

    default @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        return modelData;
    }

    default TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        return self().getParticleIcon();
    }

    /**
     * Gets the set of {@link RenderType render types} to use when drawing this block in the level.
     * Supported types are those returned by {@link RenderType#chunkBufferLayers()}.
     * <p>
     * By default, defers query to {@link ItemBlockRenderTypes}.
     */
    default ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return ItemBlockRenderTypes.getRenderLayers(state);
    }
}
