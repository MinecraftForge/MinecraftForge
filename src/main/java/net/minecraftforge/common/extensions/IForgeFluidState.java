/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public interface IForgeFluidState
{
    default FluidState getFluidState()
    {
        return (FluidState)this;
    }

    /**
     * Called when the entity is inside this block, may be used to determined if the entity can breathing,
     * display material overlays, or if the entity can swim inside a block.
     *
     * @param world that is being tested.
     * @param pos position thats being tested.
     * @param entity that is being tested.
     * @param yToTest, primarily for testingHead, which sends the the eye level of the entity, other wise it sends a y that can be tested vs liquid height.
     * @param tag to test for.
     * @param testingHead when true, its testing the entities head for vision, breathing ect... otherwise its testing the body, for swimming and movement adjustment.
     */
    default boolean isEntityInside(IWorldReader world, BlockPos pos, Entity entity, double yToTest, Tag<Fluid> tag, boolean testingHead)
    {
//        return ifluidstate.isTagged(p_213290_1_) && d0 < (double)((float)blockpos.getY() + ifluidstate.getActualHeight(this.world, blockpos) + 0.11111111F);
        return getFluidState().getType().isEntityInside(getFluidState(), world, pos, entity, yToTest, tag, testingHead);
    }



    /**
     * Location sensitive version of getExplosionResistance
     *
     * @param world The current world
     * @param pos Block position in world
     * @param explosion The explosion
     * @return The amount of the explosion absorbed.
     */
    default float getExplosionResistance(IBlockReader world, BlockPos pos, Explosion explosion)
    {
        return getFluidState().getType().getExplosionResistance(getFluidState(), world, pos, explosion);
    }

    /**
     * Queries if this fluidstate should render in a given layer.
     * A custom {@link IBakedModel} can use {@link net.minecraftforge.client.MinecraftForgeClient#getRenderLayer()} to alter the model based on layer.
     */
    /* TODO: reimplement
    default boolean canRenderInLayer(BlockRenderLayer layer)
    {
        return getFluidState().getFluid().canRenderInLayer(getFluidState(), layer);
    }*/
}
