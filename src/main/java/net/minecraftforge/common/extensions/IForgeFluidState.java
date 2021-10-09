/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
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

import javax.annotation.Nullable;

public interface IForgeFluidState
{
    private FluidState self()
    {
        return (FluidState)this;
    }

    /**
     * Location sensitive version of getExplosionResistance
     *
     * @param level The current level
     * @param pos Block position in the level
     * @param explosion The explosion
     * @return The amount of the explosion absorbed.
     */
    default float getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return self().getType().getExplosionResistance(self(), level, pos, explosion);
    }

    /**
     * Queried for the Fluids Base {@link BlockPathTypes}.
     * Used to determine what the path node priority value is for the fluid.
     * <ul>
     * <li>Negative Values = Untraversable</li>
     * <li>0 = Best</li>
     * <li>Highest = Worst</li>
     * </ul>
     * @param level The current level's block reader
     * @param pos The position of the fluid
     * @return {@code null} for default behavior; otherwise, returns the fluid's PathNodeType for pathfinding purposes
     */
    @Nullable
    default BlockPathTypes getBlockPathType(BlockGetter level, BlockPos pos)
    {
        return getBlockPathType(level, pos, null);
    }
    
    /**
     * Queried for the Fluids Base {@link BlockPathTypes}.
     * The {@link BlockPathTypes} dictates the "danger" level for an entity to pathfind through/over a specific block.
     * This is what is used to dictate that for example "Lava should not be pathed through" when an entity pathfinder is trying to decide on a path.
     * <ul>
     * <li>Negative Values = Untraversable</li>
     * <li>0 = Best</li>
     * <li>Highest = Worst</li>
     * </ul>
     * @param level The current level's block getter
     * @param pos The position of the fluid
     * @param entity The pathing entity, can be null
     * @return {@code null} for default behavior; otherwise, returns the fluid's PathNodeType for pathfinding purposes
     */
    @Nullable
    default BlockPathTypes getBlockPathType(BlockGetter level, BlockPos pos, @Nullable Mob entity)
    {
        return self().getType().getBlockPathType(self(), level, pos, entity);
    }

    /**
     * Gets the {@link BlockPathTypes} of the fluid when adjacent to some pathfinding entity.
     * The {@link BlockPathTypes} dictates the "danger" level for an entity to pathfind through/over a specific block.
     * This is what is used to dictate that for example "Lava should not be pathed through" when an entity pathfinder is trying to decide on a path.
     * <ul>
     * <li>Negative Values = Untraversable</li>
     * <li>0 = Best</li>
     * <li>Highest = Worst</li>
     * </ul>
     * @param level The current level's block getter
     * @param pos The position of the fluid
     * @param originalType The {@link BlockPathTypes} obtained from {@link IForgeBlock#getBlockPathType(BlockState, BlockGetter, BlockPos, Mob)}
     * @return {@code null} for default behavior; otherwise, returns the fluid's adjacent {@link BlockPathTypes}
     */
    @Nullable
    default BlockPathTypes getAdjacentBlockPathType(BlockGetter level, BlockPos pos, BlockPathTypes originalType)
    {
        return getAdjacentBlockPathType(level, pos, null, originalType);
    }

    /**
     * Gets the {@link BlockPathTypes} of the fluid when adjacent to some pathfinding entity.
     * <ul>
     * <li>Negative Values = Untraversable</li>
     * <li>0 = Best</li>
     * <li>Highest = Worst</li>
     * </ul>
     * @param level The current level's block getter
     * @param pos The position of the fluid
     * @param entity The pathing entity, can be null
     * @param originalType The {@link BlockPathTypes} obtained from {@link IForgeBlock#getBlockPathType(BlockState, BlockGetter, BlockPos, Mob)}
     * @return {@code null} for default behavior; otherwise, returns the fluid's adjacent {@link BlockPathTypes}
     */
    @Nullable
    default BlockPathTypes getAdjacentBlockPathType(BlockGetter level, BlockPos pos, @Nullable Mob entity, BlockPathTypes originalType)
    {
        return self().getType().getAdjacentBlockPathType(self(), level, pos, entity, originalType);
    }

    /**
     * Handles acceleration or "pushing" while moving through the fluid.
     * This implementation is slightly modified default behavior for fluid acceleration, based on {@link Entity#updateFluidHeightAndDoFluidPushing(Tag, double)}.
     *
     * @param entity The current {@link Entity} that motion is being applied to
     * @return Whether the motion was successfully applied to the {@link Entity}
     */
    default boolean updateFluidHeightAndDoFluidPushing(Entity entity)
    {
        return self().getType().updateFluidHeightAndDoFluidPushing(self(), entity);
    }

    /**
     * Handles "motion" modification for fluids.
     * Things like slower movement, "swimming" slowdown, etc.
     *
     * @param entity The {@link LivingEntity} whose motion is being handled
     * @param travelVector The current travel {@link Vec3}
     * @param gravity The current gravity being applied to the {@link LivingEntity}
     */
    default void handleMotion(LivingEntity entity, Vec3 travelVector, double gravity)
    {
        self().getType().handleMotion(self(), entity, travelVector, gravity);
    }

    /**
     * Handles modification of 'jumps' inside of a fluid.
     *
     * @param entity The {@link LivingEntity} whose jump is being modified
     */
    default void jump(LivingEntity entity)
    {
        self().getType().jump(self(), entity);
    }

    /**
     * Handles modifications of 'sinking' inside of a fluid
     *
     * @param entity The {@link LivingEntity} whose jump is being modified
     */
    default void sink(LivingEntity entity)
    {
        self().getType().sink(self(), entity);
    }

    /**
     * Dictates whether an entity can swim in this fluid or not.
     * Swimming in this case refers to the "sneak" behavior swimming and swimming animation in custom fluids.
     *
     * @return Whether an entity can "swim" in this fluid
     */
    default boolean canSwim()
    {
        return self().getType().canSwim(self());
    }

    /**
     * Dictates whether a {@link LivingEntity} can drown in this fluid or not.
     *
     * @param entity The entity within the fluid
     * @return Whether the {@link LivingEntity} can drown or not
     */
    default boolean canDrown(LivingEntity entity)
    {
        return self().getType().canDrown(self(), entity);
    }

    /**
     * Dictates whether this {@link FluidState} can provide "hydration" to the provided {@link BlockState}
     * This is used for checks such as:
     * - {@link net.minecraft.world.level.block.FarmBlock#isNearWater(LevelReader, BlockPos)}
     * - {@link net.minecraft.world.level.block.ConcretePowderBlock#touchesLiquid(BlockGetter, BlockPos)}
     * - {@link net.minecraft.world.level.block.CoralBlock#scanForWater(BlockGetter, BlockPos)}
     * - {@link net.minecraft.world.level.block.SpongeBlock#tryAbsorbWater(Level, BlockPos)}
     * - {@link net.minecraft.world.level.block.SugarCaneBlock#canSurvive(BlockState, LevelReader, BlockPos)}
     *
     * @param blockState the provided {@link BlockState}
     * @return Whether the provided {@link FluidState} can provide hydration for the {@link BlockState}
     */
    default boolean canHydrate(BlockState blockState)
    {
        return self().getType().canHydrate(self(), blockState);
    }

    /**
     * This method is used to handle fluid interactions.
     * The current position of the fluid is the one that should be replaced during the interaction.
     * 
     * IE. (Fluid + Catalyst = Result) where the Fluid and Result are in the same position.
     * Lava(Source/Flowing) + Water = Obsidian/Cobblestone.
     * Lava(Source/Flowing) + Blue Ice = Basalt.
     *
     * @param level The {@link Level} containing the interaction
     * @param pos The {@link BlockPos} the interaction is being applied at
     * @return Whether a fluid tick needs to be scheduled. Should return true only if no reaction has occurred or the Result is another fluid.
     */
    default boolean handleFluidInteraction(Level level, BlockPos pos)
    {
        return self().getType().handleFluidInteraction(self(), level, pos);
    }

    /**
     * This is a checker method to check whether two fluidstates match each other using their Fluid as the reference point.
     *
     * @param otherState The secondary state to check
     * @return Whether the two provided {@link FluidState}s {@link Fluid}s match.
     */
    default boolean is(FluidState otherState)
    {
        return self().getType().is(self(), otherState);
    }

    /**
     * This method dictates whether this fluid supports boats being "usable" with it.
     *
     * @param boat The supplied {@link Boat} entity
     * @return Whether the fluid supports boats being used with it.
     */
    default boolean canBoat(Boat boat)
    {
        return self().getType().canBoat(self(), boat);
    }
}
