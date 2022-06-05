/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;

public interface IForgeFluidState
{
    private FluidState self()
    {
        return (FluidState)this;
    }

    /**
     * Called when the entity is inside this block, may be used to determined if the entity can breathing,
     * display material overlays, or if the entity can swim inside a block.
     *
     * @param level that is being tested.
     * @param pos position thats being tested.
     * @param entity that is being tested.
     * @param yToTest, primarily for testingHead, which sends the the eye level of the entity, other wise it sends a y that can be tested vs liquid height.
     * @param tag to test for.
     * @param testingHead when true, its testing the entities head for vision, breathing ect... otherwise its testing the body, for swimming and movement adjustment.
     */
    default boolean isEntityInside(LevelReader level, BlockPos pos, Entity entity, double yToTest, HolderSet<Fluid> tag, boolean testingHead)
    {
//        return ifluidstate.isTagged(p_213290_1_) && d0 < (double)((float)blockpos.getY() + ifluidstate.getActualHeight(this.level, blockpos) + 0.11111111F);
        return self().getType().isEntityInside(self(), level, pos, entity, yToTest, tag, testingHead);
    }

    /**
     * Location sensitive version of getExplosionResistance
     *
     * @param level The current level
     * @param pos Block position in level
     * @param explosion The explosion
     * @return The amount of the explosion absorbed.
     */
    default float getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return self().getType().getExplosionResistance(self(), level, pos, explosion);
    }

}
