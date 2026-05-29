/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.extensions;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftsingularity.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import java.util.List;

@SuppressWarnings("deprecation")
public interface IForgeBlockStateModel {
    private BlockStateModel self() {
        return (BlockStateModel)this;
    }

    default @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        return modelData;
    }

    default Material.Baked particleMaterial(@NotNull ModelData data) {
        return self().particleMaterial();
    }

    /**
     * Collects the parts of this model with the specified ModelData.
     * Not all paths call this, or pass in empty data. So it is recommended to also implement a sane default in
     * the normal {@link BlockStateModel#collectParts(RandomSource, List)} method.
     */
    default void collectParts(RandomSource random, List<BlockStateModelPart> output, ModelData data) {
        self().collectParts(random, output);
    }
}
