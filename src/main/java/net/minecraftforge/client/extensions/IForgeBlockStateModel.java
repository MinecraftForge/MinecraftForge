/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import net.minecraftforge.common.extensions.IForgeBlockGetter;
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
     * This method may be called from the main thread or from a meshing thread. As such, it is not inherently safe to directly access data
     * from block entities. Block entity data can safely be requested from the level instance through {@link IForgeBlockGetter#getModelData(BlockPos)}.
     * This returns the model data obtained from {@link IForgeBlockEntity#getModelData()}.<br>
     * <p>
     * Note that the output {@link BlockStateModelPart}s must not maintain references to the level or objects in the level. Maintaining such
     * references and accessing them from the {@link BlockStateModelPart} will cause concurrency issues and thus must be avoided.
     * The {@link BlockStateModelPart}s should be a static representation of the model's geometry.
     */
    default void collectParts(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, RandomSource random, List<BlockStateModelPart> output) {
        self().collectParts(random, output);
    }
}
