/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("deprecation")
public interface IForgeBlockStateModel {
    private BlockStateModel self() {
        return (BlockStateModel)this;
    }

    default @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        return modelData;
    }

    default TextureAtlasSprite particleIcon(@NotNull ModelData data) {
        return self().particleIcon();
    }

    /**
     * Gets the set of {@link RenderType render types} to use when drawing this block in the level.
     * Supported types are those returned by {@link RenderType#chunkBufferLayers()}.
     * <p>
     * By default, defers query to {@link ItemBlockRenderTypes}.
     */
    default Collection<ChunkSectionLayer> getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return ItemBlockRenderTypes.getRenderLayers(state);
    }

    /**
     * Collects the parts of this model to render for the specified render type.
     * Not all paths call this, or use a valid render type. So it is recommended to also implement a sane default in
     * the normal {@link BlockStateModel#collectParts(RandomSource, List)} method.
     */
    default List<BlockModelPart> collectParts(RandomSource random, ModelData data, @Nullable ChunkSectionLayer renderType) {
        List<BlockModelPart> list = new ObjectArrayList<>();
        this.collectParts(random, list, data, renderType);
        return list;
    }

    /**
     * Collects the parts of this model to render for the specified render type.
     * Not all paths call this, or use a valid render type. So it is recommended to also implement a sane default in
     * the normal {@link BlockStateModel#collectParts(RandomSource, List)} method.
     */
    default void collectParts(RandomSource random, List<BlockModelPart> dest, ModelData data, @Nullable ChunkSectionLayer renderType) {
        self().collectParts(random, dest);
    }
}
