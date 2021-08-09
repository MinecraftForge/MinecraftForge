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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.Nullable;
import java.util.Set;

public interface IForgeFluid
{
    /**
     * Location sensitive version of getExplosionResistance
     *
     * @param state the provided {@link FluidState}
     * @param level the current {@link Level}
     * @param pos the provided {@link BlockPos} location
     * @param explosion the occurring {@link Explosion}
     * @return The amount of the explosion absorbed, defaults to the states getExplosionResistance value.
     */
    @SuppressWarnings("deprecation")
    default float getExplosionResistance(FluidState state, BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return state.getExplosionResistance();
    }

    /**
     * Retrieves a list of tags names this is known to be associated with.
     * This should be used in favor of TagCollection.getOwningTags, as this caches the result and automatically updates when the TagCollection changes.
     */
    Set<ResourceLocation> getTags();

    /**
     * Retrieves the non-vanilla fluid attributes.
     */
    FluidAttributes getAttributes();

    /**
     * Queried for the Fluids Base {@link BlockPathTypes}.
     * Used to determine what the path node priority value is for the fluid.
     * <ul>
     * <li>Negative Values = Untraversable</li>
     * <li>0 = Best</li>
     * <li>Highest = Worst</li>
     * </ul>
     * @param state The current FluidState
     * @param level The current {@link Level}'s block reader
     * @param pos The position of the fluid
     * @param entity The pathing entity, can be null
     * @return {@code null} for default behavior; otherwise, returns the fluid's {@link BlockPathTypes} for pathfinding purposes
     */
    @Nullable
    default BlockPathTypes getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob entity)
    {
        return null;
    }

    /**
     * Gets the {@link BlockPathTypes} of the fluid when adjacent to some pathfinding entity.
     * <ul>
     * <li>Negative Values = Untraversable</li>
     * <li>0 = Best</li>
     * <li>Highest = Worst</li>
     * </ul>
     * @param state the current FluidState
     * @param level the current {@link Level}'s block reader
     * @param pos the position of the fluid
     * @param entity the pathing entity, can be null
     * @param originalType the {@link BlockPathTypes} obtained from {@link IForgeBlock#getBlockPathType(BlockState, BlockGetter, BlockPos, Mob)}
     * @return {@code null} for default behavior; otherwise, returns the fluid's adjacent {@link BlockPathTypes}
     */
    @Nullable
    default BlockPathTypes getAdjacentBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob entity, BlockPathTypes originalType)
    {
        return null;
    }

    /**
     * Handles acceleration or "pushing" while moving through the fluid.
     * This implementation is slightly modified default behavior for fluid acceleration, based on {@link Entity#updateFluidHeightAndDoFluidPushing(net.minecraft.tags.Tag, double)}.
     *
     * @param state The {@link FluidState} the entity is in
     * @param entity The current {@link Entity} that motion is being applied to
     * @return Whether the motion was successfully applied to the {@link Entity}
     */
    default boolean updateFluidHeightAndDoFluidPushing(FluidState state, Entity entity)
    {
        if (entity.touchingUnloadedChunk()) return false;
        else
        {
            AABB box = entity.getBoundingBox().deflate(0.001D);
            int minX = Mth.floor(box.minX);
            int maxX = Mth.ceil(box.maxX);
            int minY = Mth.floor(box.minY);
            int maxY = Mth.ceil(box.maxY);
            int minZ = Mth.floor(box.minZ);
            int maxZ = Mth.ceil(box.maxZ);
            double eyeLevel = 0.0D;
            boolean isInFluid = false;
            Vec3 motion = Vec3.ZERO;
            int withinFluidBlocks = 0;
            BlockPos.MutableBlockPos fluidPos = new BlockPos.MutableBlockPos();

            for (int x = minX; x < maxX; ++x)
            {
                for (int y = minY; y < maxY; ++y)
                {
                    for (int z = minZ; z < maxZ; ++z)
                    {
                        fluidPos.set(x, y, z);
                        FluidState currentState = entity.level.getFluidState(fluidPos);
                        if (currentState == state)
                        {
                            double fluidHeight = y + currentState.getHeight(entity.level, fluidPos);
                            if (fluidHeight >= box.minY)
                            {
                                isInFluid = true;
                                eyeLevel = Math.max(fluidHeight - box.minY, eyeLevel);
                                if (entity.isPushedByFluid(currentState))
                                {
                                    Vec3 fluidFlow = currentState.getFlow(entity.level, fluidPos);
                                    if (eyeLevel < 0.4D) fluidFlow = fluidFlow.scale(eyeLevel);
                                    motion = motion.add(fluidFlow);
                                    ++withinFluidBlocks;
                                }
                            }
                        }
                    }
                }
            }
            
            if (motion.length() > 0.0D)
            {
                if (withinFluidBlocks > 0) motion = motion.scale(1.0D / (double) withinFluidBlocks);
                if (!(this instanceof Player)) motion = motion.normalize();
                Vec3 entityMotion = entity.getDeltaMovement();
                motion = motion.scale(getAttributes().getMotionScale(state, entity));
                if (Math.abs(entityMotion.x) < 0.003D && Math.abs(entityMotion.z) < 0.003D && motion.length() < 0.0045D)
                    motion = motion.normalize().scale(0.0045D);
                entity.setDeltaMovement(entityMotion.add(motion));
            }
            
            entity.setInFluid(state);
            entity.addFluidHeight(state, eyeLevel);
            if (isInFluid)
            {
                FluidAttributes attr = state.getType().getAttributes();
                entity.fallDistance *= attr.getFallDistanceModifier(state, entity);
                if (canExtinguish(state, entity)) entity.clearFire();
            }
            return isInFluid;
        }
    }

    /**
     * Handles "motion" modification for fluids.
     * Things like slower movement, "swimming" slowdown, etc.
     *
     * @param state The current {@link FluidState}
     * @param entity The {@link LivingEntity} whose motion is being handled
     * @param travelVector The current travel {@link Vec3}
     * @param gravity The current gravity being applied to the {@link LivingEntity}
     */
    default void handleMotion(FluidState state, LivingEntity entity, Vec3 travelVector, double gravity)
    {
        FluidAttributes attributes = state.getType().getAttributes();
        boolean hasNegativeYMotion = entity.getDeltaMovement().y <= 0.0D;
        double originalY = entity.getY();
        float horizontalModifier = attributes.getHorizontalMotionModifier(state, entity);
        float movementAmount = 0.02F;
        float depthStriderModifier = attributes.getEnchantmentMotionModifier(Enchantments.DEPTH_STRIDER, entity);

        if (depthStriderModifier > 3.0F) depthStriderModifier = 3.0F;
        if (!entity.isOnGround()) depthStriderModifier *= 0.5F;
        if (depthStriderModifier > 0.0F)
        {
            horizontalModifier += (0.546 - horizontalModifier) * depthStriderModifier / 3.0F;
            movementAmount += (entity.getSpeed() - movementAmount) * depthStriderModifier / 3.0F;
        }
        if (entity.hasEffect(MobEffects.DOLPHINS_GRACE)) attributes.modifyMotionByEffect(MobEffects.DOLPHINS_GRACE, entity, horizontalModifier, true);

        movementAmount *= entity.getAttribute(ForgeMod.SWIM_SPEED.get()).getValue();
        entity.moveRelative(movementAmount, travelVector);
        entity.move(MoverType.SELF, entity.getDeltaMovement());

        Vec3 entityMotion = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.onClimbable())
            entityMotion = new Vec3(entityMotion.x, 0.2D, entityMotion.z);
        entity.setDeltaMovement(entityMotion.multiply(horizontalModifier, 0.8F, horizontalModifier));
        Vec3 finalMotion = entity.getFluidFallingAdjustedMovement(gravity, hasNegativeYMotion, entity.getDeltaMovement());
        entity.setDeltaMovement(finalMotion);
        if (entity.horizontalCollision && entity.isFree(finalMotion.x, finalMotion.y + 0.6D - entity.getY() + originalY, finalMotion.z))
            entity.setDeltaMovement(finalMotion.x, 0.3F, finalMotion.z);
    }

    /**
     * Handles modification of jumps inside of a fluid.
     *
     * @param state The current {@link FluidState} the {@link LivingEntity} is in
     * @param entity The {@link LivingEntity} whose jump is being modified
     */
    default void jump(FluidState state, LivingEntity entity)
    {
        entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.04D * entity.getAttribute(ForgeMod.SWIM_SPEED.get()).getValue(), 0.0D));
    }

    /**
     * Handles modifications of 'sinking' inside of a fluid
     *
     * @param state The current {@link FluidState} the {@link LivingEntity} is in
     * @param entity The {@link LivingEntity} whose jump is being modified
     */
    default void sink(FluidState state, LivingEntity entity)
    {
        entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, (double)-0.04F * entity.getAttribute(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get()).getValue(), 0.0D));
    }

    /**
     * This method is used to handle fluid interactions.
     * The current position of the fluid is the one that should be replaced during the interaction.
     * 
     * IE. (Fluid + Catalyst = Result) where the Fluid and Result are in the same position.
     * Lava(Source/Flowing) + Water = Obsidian/Cobblestone.
     * Lava(Source/Flowing) + Blue Ice = Basalt.
     *
     * @param state The current {@link FluidState}
     * @param level The {@link Level} containing the interaction
     * @param pos The {@link BlockPos} the interaction is being applied at
     * @return Whether a fluid tick needs to be scheduled. Should return true only if no reaction has occurred or the Result is another fluid.
     */
    default boolean handleFluidInteraction(FluidState state, Level level, BlockPos pos)
    {
        return true;
    }

    /**
     * This is a checker method to check whether two fluidstates match each other using their Fluid as the reference point.
     *
     * @param state The primary state to check
     * @param otherState The secondary state to check
     * @return Whether the two provided {@link FluidState}s {@link Fluid}s match.
     */
    default boolean is(FluidState state, FluidState otherState)
    {
        return state.getType() == otherState.getType();
    }

    /**
     * Dictates whether a player can swim in this fluid or not.
     * Swimming in this case refers the the "sneak" behavior swimming and swimming animation in custom fluids.
     *
     * @param state The current {@link FluidState} the player is in
     * @return Whether a player can "swim" in this fluid
     */
    default boolean canSwim(FluidState state)
    {
        return state.getType().getAttributes().canSwim(state);
    }

    /**
     * Dictates whether a {@link LivingEntity} can drown in this fluid or not.
     *
     * @param state The current {@link FluidState}
     * @param entity The entity within the fluid
     * @return Whether the {@link LivingEntity} can drown or not
     */
    default boolean canDrown(FluidState state, LivingEntity entity)
    {
        return state.getType().getAttributes().canDrown(state, entity);
    }

    /**
     * Dictates whether a {@link Entity} can be extinguished by the fluid if on fire.
     *
     * @param state the current {@link FluidState}
     * @param entity the {@link Entity} to test against
     * @return Whether the provided {@link Entity} can be extinguished
     */
    default boolean canExtinguish(FluidState state, Entity entity) {
        return state.getType().getAttributes().canExtinguish(state, entity);
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
     * @param fluidState the provided {@link FluidState}
     * @param blockState the provided {@link BlockState}
     * @return Whether the provided {@link FluidState} can provide hydration for the {@link BlockState}
     */
    default boolean canHydrate(FluidState fluidState, BlockState blockState)
    {
        return fluidState.getType().getAttributes().canHydrate(fluidState, blockState);
    }

    /**
     * This method dictates whether this fluid supports boats being "usable" with it.
     *
     * @param state The supplied {@link FluidState}
     * @param boat The supplied {@link Boat} entity
     * @return Whether the fluid supports boats being used with it.
     */
    default boolean canBoat(FluidState state, Boat boat)
    {
        return state.getType().getAttributes().canBoat(state, boat);
    }
}
