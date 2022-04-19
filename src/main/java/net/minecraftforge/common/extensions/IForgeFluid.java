/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.fluids.FluidAttributes;

public interface IForgeFluid
{
    /**
     * Called when the entity is inside this block, may be used to determined if the entity can breathing,
     * display material overlays, or if the entity can swim inside a block.
     *
     * @param level that is being tested.
     * @param pos position thats being tested.
     * @param entity that is being tested.
     * @param yToTest, primarily for testingHead, which sends the the eye level of the entity, other wise it sends a y that can be tested vs liquid height.
     * @param tag Fluid category
     * @param testingHead when true, its testing the entities head for vision, breathing ect... otherwise its testing the body, for swimming and movement adjustment.
     */
    default boolean isEntityInside(FluidState state, LevelReader level, BlockPos pos, Entity entity, double yToTest, HolderSet<Fluid> tag, boolean testingHead)
    {
        return state.is(tag) && yToTest < (double)(pos.getY() + state.getHeight(level, pos) + 0.11111111F);
    }

    /**
     * Called when boats or fishing hooks are inside the block to check if they are inside
     * the material requested.
     *
     * @param level level that is being tested.
     * @param pos block thats being tested.
     * @param boundingBox box to test, generally the bounds of an entity that are besting tested.
     * @param materialIn to check for.
     * @return null for default behavior, true if the box is within the material, false if it was not.
     */
    @Nullable
    default Boolean isAABBInsideMaterial(FluidState state, LevelReader level, BlockPos pos, AABB boundingBox, Material materialIn)
    {
        return null;
    }

    /**
     * Called when entities are moving to check if they are inside a liquid
     *
     * @param level level that is being tested.
     * @param pos block thats being tested.
     * @param boundingBox box to test, generally the bounds of an entity that are besting tested.
     * @return null for default behavior, true if the box is within the material, false if it was not.
     */
    @Nullable
    default Boolean isAABBInsideLiquid(FluidState state, LevelReader level, BlockPos pos, AABB boundingBox)
    {
        return null;
    }

    /**
     * Location sensitive version of getExplosionResistance
     *
     * @param level The current level
     * @param pos Block position in level
     * @param explosion The explosion
     * @return The amount of the explosion absorbed.
     */
    @SuppressWarnings("deprecation")
    default float getExplosionResistance(FluidState state, BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return state.getExplosionResistance();
    }

    /**
     * Queries if this fluid should render in a given layer.
     * A custom {@link IBakedModel} can use {@link net.minecraftforge.client.MinecraftForgeClient#getRenderType()} to alter the model based on layer.
     */
    /* TODO: reimplement
    default boolean canRenderInLayer(IFluidState state, BlockRenderLayer layer)
    {
        return this.getFluid().getRenderLayer() == layer;
    }*/

    /**
     * Retrieves the non-vanilla fluid attributes, including localized name.
     */
    FluidAttributes getAttributes();
}
