/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public interface IForgeFluid
{
    private Fluid self()
    {
        return (Fluid) this;
    }

    /**
     * Returns the explosion resistance of the fluid.
     *
     * @param state the state of the fluid
     * @param level the level the fluid is in
     * @param pos the position of the fluid
     * @param explosion the explosion the fluid is absorbing
     * @return the amount of the explosion the fluid can absorb
     */
    @SuppressWarnings("deprecation")
    default float getExplosionResistance(FluidState state, BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return state.getExplosionResistance();
    }

    /**
     * Returns the type of this fluid.
     *
     * <p>Important: This MUST be overridden on your fluid, otherwise an
     * error will be thrown.
     *
     * @return the type of this fluid
     */
    FluidType getFluidType();

    /**
     * Performs how an entity moves when within the fluid. If using custom
     * movement logic, the method should return {@code true}. Otherwise, the
     * movement logic will default to water.
     *
     * @param state the state of the fluid
     * @param entity the entity moving within the fluid
     * @param movementVector the velocity of how the entity wants to move
     * @param gravity the gravity to apply to the entity
     * @return {@code true} if custom movement logic is performed, {@code false} otherwise
     */
    default boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity)
    {
        return getFluidType().move(state, entity, movementVector, gravity);
    }

    /**
     * Returns whether the fluid can create a source.
     *
     * @param state the state of the fluid
     * @param level the level that can get the fluid
     * @param pos the location of the fluid
     * @return {@code true} if the fluid can create a source, {@code false} otherwise
     */
    default boolean canConvertToSource(FluidState state, Level level, BlockPos pos)
    {
        return getFluidType().canConvertToSource(state, level, pos);
    }

    /**
     * Returns whether the boat can be used on the fluid.
     *
     * @param state the state of the fluid
     * @param boat the boat trying to be used on the fluid
     * @return {@code true} if the boat can be used, {@code false} otherwise
     */
    default boolean supportsBoating(FluidState state, Boat boat)
    {
        return getFluidType().supportsBoating(state, boat);
    }

    /**
     * Gets the path type of this fluid when an entity is pathfinding. When
     * {@code null}, uses vanilla behavior.
     *
     * @param state the state of the fluid
     * @param level the level which contains this fluid
     * @param pos the position of the fluid
     * @param mob the mob currently pathfinding, may be {@code null}
     * @param canFluidLog {@code true} if the path is being applied for fluids that can log blocks,
     *                    should be checked against if the fluid can log a block
     * @return the path type of this fluid
     */
    @Nullable
    default BlockPathTypes getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @org.jetbrains.annotations.Nullable Mob mob, boolean canFluidLog)
    {
        return getFluidType().getBlockPathType(state, level, pos, mob, canFluidLog);
    }

    /**
     * Gets the path type of the adjacent fluid to a pathfinding entity.
     * Path types with a negative malus are not traversable for the entity.
     * Pathfinding entities will favor paths consisting of a lower malus.
     * When {@code null}, uses vanilla behavior.
     *
     * @param state the state of the fluid
     * @param level the level which contains this fluid
     * @param pos the position of the fluid
     * @param mob the mob currently pathfinding, may be {@code null}
     * @param originalType the path type of the source the entity is on
     * @return the path type of this fluid
     */
    @Nullable
    default BlockPathTypes getAdjacentBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @org.jetbrains.annotations.Nullable Mob mob, BlockPathTypes originalType)
    {
        return getFluidType().getAdjacentBlockPathType(state, level, pos, mob, originalType);
    }

    /**
     * Returns whether the block can be hydrated by a fluid.
     *
     * <p>Hydration is an arbitrary word which depends on the block.
     * <ul>
     *     <li>A farmland has moisture</li>
     *     <li>A sponge can soak up the liquid</li>
     *     <li>A coral can live</li>
     * </ul>
     *
     * @param state the state of the fluid
     * @param getter the getter which can get the fluid
     * @param pos the position of the fluid
     * @param source the state of the block being hydrated
     * @param sourcePos the position of the block being hydrated
     * @return {@code true} if the block can be hydrated, {@code false} otherwise
     */
    default boolean canHydrate(FluidState state, BlockGetter getter, BlockPos pos, BlockState source, BlockPos sourcePos)
    {
        return getFluidType().canHydrate(state, getter, pos, source, sourcePos);
    }

    /**
     * Returns whether the block can be extinguished by this fluid.
     *
     * @param state the state of the fluid
     * @param getter the getter which can get the fluid
     * @param pos the position of the fluid
     * @return {@code true} if the block can be extinguished, {@code false} otherwise
     */
    default boolean canExtinguish(FluidState state, BlockGetter getter, BlockPos pos)
    {
        return getFluidType().canExtinguish(state, getter, pos);
    }
}
