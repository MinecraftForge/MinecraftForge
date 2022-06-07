/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.data;

import java.util.List;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Convenience interface with default implementation of {@link BakedModel#getQuads(BlockState, Direction, RandomSource, IModelData)}.
 */
public interface IDynamicBakedModel extends BakedModel
{
    @Override
    default @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand)
    {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    // Force this to be overriden otherwise this introduces a default cycle between the two overloads.
    @Override
    @NotNull
    List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull IModelData extraData);
}
