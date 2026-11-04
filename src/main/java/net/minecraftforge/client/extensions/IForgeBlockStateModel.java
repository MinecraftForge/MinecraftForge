/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import java.util.List;

@SuppressWarnings("deprecation")
public interface IForgeBlockStateModel {
    private BlockStateModel self() {
        return (BlockStateModel)this;
    }

    default Material.Baked particleMaterial(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return self().particleMaterial();
    }

    /**
     * Outputs geometry for the given level context to the given list.
     * <p>
     * Note that the output {@link BlockStateModelPart}s must not maintain references to the level or objects in the level. Maintaining such
     * references and accessing them from the {@link BlockStateModelPart} will cause concurrency issues and thus must be avoided.
     */
    default void collectParts(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, RandomSource random, List<BlockStateModelPart> output) {
        self().collectParts(random, output);
    }

    /**
     * Provides flags for the geometry that may result from {@link #collectParts(BlockAndTintGetter, BlockPos, BlockState, RandomSource, List)}
     * for the specific level context. Note that the result of this method says nothing about the geometry produced by this model under
     * different level context.
     *
     * @see BlockStateModel#materialFlags()
     */
    default @BakedQuad.MaterialFlags int materialFlags(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, RandomSource random) {
        return self().materialFlags();
    }

    /**
     * Provides flags for the geometry that may result from {@link #collectParts(BlockAndTintGetter, BlockPos, BlockState, RandomSource, List)}
     * for the specific level context. Note that the result of this method says nothing about the geometry produced by this model under
     * different level context.
     *
     * @see #materialFlags(BlockAndTintGetter, BlockPos, BlockState, RandomSource)
     * @see BlockStateModel#hasMaterialFlag(int)
     */
    default boolean hasMaterialFlag(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, RandomSource random, @BakedQuad.MaterialFlags int flag) {
        return (this.materialFlags(level, pos, state, random) & flag) != 0;
    }
}
