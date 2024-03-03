/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.data;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;

/**
 * Convenience interface with default implementation of {@link BakedModel#getQuads(BlockState, Direction, Random, IModelData)}.
 */
public interface IDynamicBakedModel extends BakedModel
{
    @Override
    default @Nonnull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand)
    {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }
    
    // Force this to be overriden otherwise this introduces a default cycle between the two overloads.
    @Override
    @Nonnull
    List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData);
}
