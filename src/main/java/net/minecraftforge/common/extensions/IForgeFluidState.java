/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.Tag;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorldReader;

public interface IForgeFluidState
{
    default IFluidState getFluidState()
    {
        return (IFluidState)this;
    }

    /**
     * Called when the entity is inside this block, may be used to determined if the entity can breathing,
     * display material overlays, or if the entity can swim inside a block.
     *
     * @param world that is being tested.
     * @param pos position thats being tested.
     * @param entity that is being tested.
     * @param yToTest, primarily for testingHead, which sends the the eye level of the entity, other wise it sends a y that can be tested vs liquid height.
     * @param material to test for.
     * @param testingHead when true, its testing the entities head for vision, breathing ect... otherwise its testing the body, for swimming and movement adjustment.
     */
    default boolean isEntityInside(IWorldReader world, BlockPos pos, Entity entity, double yToTest, Tag<Fluid> tag, boolean testingHead)
    {
//        return ifluidstate.isTagged(p_213290_1_) && d0 < (double)((float)blockpos.getY() + ifluidstate.func_215679_a(this.world, blockpos) + 0.11111111F);
        return getFluidState().getFluid().isEntityInside(getFluidState(), world, pos, entity, yToTest, tag, testingHead);
    }



    /**
     * Location sensitive version of getExplosionResistance
     *
     * @param world The current world
     * @param pos Block position in world
     * @param exploder The entity that caused the explosion, can be null
     * @param explosion The explosion
     * @return The amount of the explosion absorbed.
     */
    default float getExplosionResistance(IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion)
    {
        return getFluidState().getFluid().getExplosionResistance(getFluidState(), world, pos, exploder, explosion);
    }
    
    /**
     * Queries if this fluidstate should render in a given layer.
     * A custom {@link IBakedModel} can use {@link net.minecraftforge.client.MinecraftForgeClient#getRenderLayer()} to alter the model based on layer.
     */
    default boolean canRenderInLayer(BlockRenderLayer layer)
    {
        return getFluidState().getFluid().canRenderInLayer(getFluidState(), layer);
    }
}
