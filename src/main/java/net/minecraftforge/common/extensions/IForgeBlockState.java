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

import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.IPlantable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

public interface IForgeBlockState
{
    private BlockState self()
    {
        return (BlockState)this;
    }

    /**
     * Gets the slipperiness at the given location at the given state. Normally
     * between 0 and 1.
     * <p>
     * Note that entities may reduce slipperiness by a certain factor of their own;
     * for {@link net.minecraft.entity.LivingEntity}, this is {@code .91}.
     * {@link net.minecraft.entity.item.ItemEntity} uses {@code .98}, and
     * {@link net.minecraft.entity.projectile.FishingBobberEntity} uses {@code .92}.
     *
     * @param world the world
     * @param pos the position in the world
     * @param entity the entity in question
     * @return the factor by which the entity's motion should be multiplied
     */
    default float getFriction(LevelReader world, BlockPos pos, @Nullable Entity entity)
    {
        return self().getBlock().getFriction(self(), world, pos, entity);
    }

    /**
     * Get a light value for this block, taking into account the given state and coordinates, normal ranges are between 0 and 15
     */
    default int getLightEmission(BlockGetter world, BlockPos pos)
    {
        return self().getBlock().getLightEmission(self(), world, pos);
    }

    /**
     * Checks if a player or entity can use this block to 'climb' like a ladder.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param entity The entity trying to use the ladder, CAN be null.
     * @return True if the block should act like a ladder
     */
    default boolean isLadder(LevelReader world, BlockPos pos, LivingEntity entity)
    {
        return self().getBlock().isLadder(self(), world, pos, entity);
    }

    /**
     * Determines if the player can harvest this block, obtaining it's drops when the block is destroyed.
     *
     * @param world The current world
     * @param pos The block's current position
     * @param player The player damaging the block
     * @return True to spawn the drops
     */
    default boolean canHarvestBlock(BlockGetter world, BlockPos pos, Player player)
    {
        return self().getBlock().canHarvestBlock(self(), world, pos, player);
    }

    /**
     * Called when a player removes a block.  This is responsible for
     * actually destroying the block, and the block is intact at time of call.
     * This is called regardless of whether the player can harvest the block or
     * not.
     *
     * Return true if the block is actually destroyed.
     *
     * Note: When used in multiplayer, this is called on both client and
     * server sides!
     *
     * @param world The current world
     * @param player The player damaging the block, may be null
     * @param pos Block position in world
     * @param willHarvest True if Block.harvestBlock will be called after this, if the return in true.
     *        Can be useful to delay the destruction of tile entities till after harvestBlock
     * @param fluid The current fluid and block state for the position in the world.
     * @return True if the block is actually destroyed.
     */
    default boolean removedByPlayer(Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        return self().getBlock().removedByPlayer(self(), world, pos, player, willHarvest, fluid);
    }

    /**
     * Determines if this block is classified as a Bed, Allowing
     * players to sleep in it, though the block has to specifically
     * perform the sleeping functionality in it's activated event.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param sleeper The sleeper or camera entity, null in some cases.
     * @return True to treat this as a bed
     */
    default boolean isBed(BlockGetter world, BlockPos pos, @Nullable LivingEntity sleeper)
    {
        return self().getBlock().isBed(self(), world, pos, sleeper);
    }

    /**
     * Determines if a specified mob type can spawn on this block, returning false will
     * prevent any mob from spawning on the block.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param type The Mob Category Type
     * @return True to allow a mob of the specified category to spawn, false to prevent it.
     */
    default boolean canCreatureSpawn(LevelReader world, BlockPos pos, Type type, EntityType<?> entityType)
    {
        return self().getBlock().canCreatureSpawn(self(), world, pos, type, entityType);
    }

    /**
     * Returns the position that the entity is moved to upon
     * respawning at this block.
     *
     * @param type The entity type used when checking if a dismount blockstate is dangerous. Currently always PLAYER.
     * @param world The current world
     * @param pos Block position in world
     * @param orientation The angle the entity had when setting the respawn point
     * @param entity The entity respawning, often null
     * @return The spawn position or the empty optional if respawning here is not possible
     */
    default Optional<Vec3> getRespawnPosition(EntityType<?> type, LevelReader world, BlockPos pos, float orientation, @Nullable LivingEntity entity)
    {
        return self().getBlock().getRespawnPosition(self(), type, world, pos, orientation, entity);
    }

    /**
     * Called when a user either starts or stops sleeping in the bed.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param sleeper The sleeper or camera entity, null in some cases.
     * @param occupied True if we are occupying the bed, or false if they are stopping use of the bed
     */
    default void setBedOccupied(Level world, BlockPos pos, LivingEntity sleeper, boolean occupied)
    {
        self().getBlock().setBedOccupied(self(), world, pos, sleeper, occupied);
    }

   /**
    * Returns the direction of the block. Same values that
    * are returned by BlockDirectional
    *
    * @param world The current world
    * @param pos Block position in world
    * @return Bed direction
    */
    default Direction getBedDirection(LevelReader world, BlockPos pos)
    {
        return self().getBlock().getBedDirection(self(), world, pos);
    }

    /**
     * Location sensitive version of getExplosionResistance
     *
     * @param world The current world
     * @param pos Block position in world
     * @param explosion The explosion
     * @return The amount of the explosion absorbed.
     */
    default float getExplosionResistance(BlockGetter world, BlockPos pos, Explosion explosion)
    {
        return self().getBlock().getExplosionResistance(self(), world, pos, explosion);
    }

    /**
     *
     * Called when A user uses the creative pick block button on this block
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, empty itemstack if nothing should be added.
     */
    default ItemStack getPickBlock(HitResult target, BlockGetter world, BlockPos pos, Player player)
    {
        return self().getBlock().getPickBlock(self(), target, world, pos, player);
    }

    /**
     *  Allows a block to override the standard EntityLivingBase.updateFallState
     *  particles, this is a server side method that spawns particles with
     *  WorldServer.spawnParticle.
     *
     * @param worldserver The current Server World
     * @param pos The position of the block.
     * @param state2 The state at the specific world/pos
     * @param entity The entity that hit landed on the block
     * @param numberOfParticles That vanilla world have spawned
     * @return True to prevent vanilla landing particles from spawning
     */
    default boolean addLandingEffects(ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles)
    {
        return self().getBlock().addLandingEffects(self(), worldserver, pos, state2, entity, numberOfParticles);
    }
   /**
    * Allows a block to override the standard vanilla running particles.
    * This is called from {@link Entity#handleRunningEffect} and is called both,
    * Client and server side, it's up to the implementor to client check / server check.
    * By default vanilla spawns particles only on the client and the server methods no-op.
    *
    * @param world  The world.
    * @param pos    The position at the entities feet.
    * @param entity The entity running on the block.
    * @return True to prevent vanilla running particles from spawning.
    */
    default boolean addRunningEffects(Level world, BlockPos pos, Entity entity)
    {
        return self().getBlock().addRunningEffects(self(), world, pos, entity);
    }

   /**
    * Determines if this block can support the passed in plant, allowing it to be planted and grow.
    * Some examples:
    *   Reeds check if its a reed, or if its sand/dirt/grass and adjacent to water
    *   Cacti checks if its a cacti, or if its sand
    *   Nether types check for soul sand
    *   Crops check for tilled soil
    *   Caves check if it's a solid surface
    *   Plains check if its grass or dirt
    *   Water check if its still water
    *
    * @param world The current world
    * @param facing The direction relative to the given position the plant wants to be, typically its UP
    * @param plantable The plant that wants to check
    * @return True to allow the plant to be planted/stay.
    */
    default boolean canSustainPlant(BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable)
    {
        return self().getBlock().canSustainPlant(self(), world, pos, facing, plantable);
    }

   /**
    * Checks if this soil is fertile, typically this means that growth rates
    * of plants on this soil will be slightly sped up.
    * Only vanilla case is tilledField when it is within range of water.
    *
    * @param world The current world
    * @param pos Block position in world
    * @return True if the soil should be considered fertile.
    */
    default boolean isFertile(BlockGetter world, BlockPos pos)
    {
        return self().getBlock().isFertile(self(), world, pos);
    }

    /**
     * Determines if this block can be used as the frame of a conduit.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param conduit Conduit position in world
     * @return True, to support the conduit, and make it active with this block.
     */
    default boolean isConduitFrame(LevelReader world, BlockPos pos, BlockPos conduit)
    {
        return self().getBlock().isConduitFrame(self(), world, pos, conduit);
    }

    /**
     * Determines if this block can be used as part of a frame of a nether portal.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True, to support being part of a nether portal frame, false otherwise.
     */
    default boolean isPortalFrame(BlockGetter world, BlockPos pos)
    {
        return self().getBlock().isPortalFrame(self(), world, pos);
    }

   /**
    * Gathers how much experience this block drops when broken.
    *
    * @param world The world
    * @param pos Block position
    * @param fortune
    * @return Amount of XP from breaking this block.
    */
    default int getExpDrop(LevelReader world, BlockPos pos, int fortune, int silktouch)
    {
        return self().getBlock().getExpDrop(self(), world, pos, fortune, silktouch);
    }

    default BlockState rotate(LevelAccessor world, BlockPos pos, Rotation direction)
    {
        return self().getBlock().rotate(self(), world, pos, direction);
    }

   /**
    * Determines the amount of enchanting power this block can provide to an enchanting table.
    * @param world The World
    * @param pos Block position in world
    * @return The amount of enchanting power this block produces.
    */
    default float getEnchantPowerBonus(LevelReader world, BlockPos pos)
    {
        return self().getBlock().getEnchantPowerBonus(self(), world, pos);
    }

   /**
    * Called when a tile entity on a side of this block changes is created or is destroyed.
    * @param world The world
    * @param pos Block position in world
    * @param neighbor Block position of neighbor
    */
    default void onNeighborChange(LevelReader world, BlockPos pos, BlockPos neighbor)
    {
        self().getBlock().onNeighborChange(self(), world, pos, neighbor);
    }

   /**
    * Called to determine whether to allow the a block to handle its own indirect power rather than using the default rules.
    * @param world The world
    * @param pos Block position in world
    * @param side The INPUT side of the block to be powered - ie the opposite of this block's output side
    * @return Whether Block#isProvidingWeakPower should be called when determining indirect power
    */
    default boolean shouldCheckWeakPower(LevelReader world, BlockPos pos, Direction side)
    {
        return self().getBlock().shouldCheckWeakPower(self(), world, pos, side);
    }

    /**
     * If this block should be notified of weak changes.
     * Weak changes are changes 1 block away through a solid block.
     * Similar to comparators.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return true To be notified of changes
     */
    default boolean getWeakChanges(LevelReader world, BlockPos pos)
    {
        return self().getBlock().getWeakChanges(self(), world, pos);
    }

    /**
     * Sensitive version of getSoundType
     * @param world The world
     * @param pos The position. Note that the world may not necessarily have {@code state} here!
     * @param entity The entity that is breaking/stepping on/placing/hitting/falling on this block, or null if no entity is in this context
     * @return A SoundType to use
     */
    default SoundType getSoundType(LevelReader world, BlockPos pos, @Nullable Entity entity)
    {
        return self().getBlock().getSoundType(self(), world, pos, entity);
    }

    /**
     * @param world The world
     * @param pos The position of this state
     * @param beacon The position of the beacon
     * @return A float RGB [0.0, 1.0] array to be averaged with a beacon's existing beam color, or null to do nothing to the beam
     */
    @Nullable
    default float[] getBeaconColorMultiplier(LevelReader world, BlockPos pos, BlockPos beacon)
    {
        return self().getBlock().getBeaconColorMultiplier(self(), world, pos, beacon);
    }

    /**
     * Used to determine the state 'viewed' by an entity (see
     * {@link net.minecraft.client.Camera#getBlockAtCamera()}).
     * Can be used by fluid blocks to determine if the viewpoint is within the fluid or not.
     *
     * @param world     the world
     * @param pos       the position
     * @param viewpoint the viewpoint
     * @return the block state that should be 'seen'
     */
    default BlockState getStateAtViewpoint(BlockGetter world, BlockPos pos, Vec3 viewpoint)
    {
        return self().getBlock().getStateAtViewpoint(self(), world, pos, viewpoint);
    }

    /**
     * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
     */
    default boolean isSlimeBlock()
    {
        return self().getBlock().isSlimeBlock(self());
    }

    /**
     * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
     */
    default boolean isStickyBlock()
    {
        return self().getBlock().isStickyBlock(self());
    }

    /**
     * Determines if this block can stick to another block when pushed by a piston.
     * @param other Other block
     * @return True to link blocks
     */
    default boolean canStickTo(BlockState other)
    {
        return self().getBlock().canStickTo(self(), other);
    }

    /**
     * Chance that fire will spread and consume this block.
     * 300 being a 100% chance, 0, being a 0% chance.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param face The face that the fire is coming from
     * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
     */
    default int getFlammability(BlockGetter world, BlockPos pos, Direction face)
    {
        return self().getBlock().getFlammability(self(), world, pos, face);
    }

    /**
     * Called when fire is updating, checks if a block face can catch fire.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param face The face that the fire is coming from
     * @return True if the face can be on fire, false otherwise.
     */
    default boolean isFlammable(BlockGetter world, BlockPos pos, Direction face)
    {
        return self().getBlock().isFlammable(self(), world, pos, face);
    }

    /**
     * If the block is flammable, this is called when it gets lit on fire.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param face The face that the fire is coming from
     * @param igniter The entity that lit the fire
     */
    default void catchFire(Level world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter)
    {
        self().getBlock().catchFire(self(), world, pos, face, igniter);
    }

    /**
     * Called when fire is updating on a neighbor block.
     * The higher the number returned, the faster fire will spread around this block.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param face The face that the fire is coming from
     * @return A number that is used to determine the speed of fire growth around the block
     */
    default int getFireSpreadSpeed(BlockGetter world, BlockPos pos, Direction face)
    {
        return self().getBlock().getFireSpreadSpeed(self(), world, pos, face);
    }

    /**
     * Currently only called by fire when it is on top of this block.
     * Returning true will prevent the fire from naturally dying during updating.
     * Also prevents firing from dying from rain.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param side The face that the fire is coming from
     * @return True if this block sustains fire, meaning it will never go out.
     */
    default boolean isFireSource(LevelReader world, BlockPos pos, Direction side)
    {
        return self().getBlock().isFireSource(self(), world, pos, side);
    }

    /**
     * Determines if this block is can be destroyed by the specified entities normal behavior.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True to allow the ender dragon to destroy this block
     */
    default boolean canEntityDestroy(BlockGetter world, BlockPos pos, Entity entity)
    {
        return self().getBlock().canEntityDestroy(self(), world, pos, entity);
    }

    /**
     * Determines if this block should set fire and deal fire damage
     * to entities coming into contact with it.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True if the block should deal damage
     */
    default boolean isBurning(BlockGetter world, BlockPos pos)
    {
       return self().getBlock().isBurning(self(), world, pos);
    }

    /**
     * Get the {@code PathNodeType} for this block. Return {@code null} for vanilla behavior.
     *
     * @return the PathNodeType
     */
    @Nullable
    default BlockPathTypes getAiPathNodeType(BlockGetter world, BlockPos pos)
    {
        return getAiPathNodeType(world, pos, null);
    }

    /**
     * Get the {@code PathNodeType} for this block. Return {@code null} for vanilla behavior.
     *
     * @return the PathNodeType
     */
    @Nullable
    default BlockPathTypes getAiPathNodeType(BlockGetter world, BlockPos pos, @Nullable Mob entity)
    {
        return self().getBlock().getAiPathNodeType(self(), world, pos, entity);
    }

    /**
     * Determines if this block should drop loot when exploded.
     */
    default boolean canDropFromExplosion(BlockGetter world, BlockPos pos, Explosion explosion)
    {
        return self().getBlock().canDropFromExplosion(self(), world, pos, explosion);
    }

    /**
     * Called when the block is destroyed by an explosion.
     * Useful for allowing the block to take into account tile entities,
     * state, etc. when exploded, before it is removed.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param explosion The explosion instance affecting the block
     */
    default void onBlockExploded(Level world, BlockPos pos, Explosion explosion)
    {
        self().getBlock().onBlockExploded(self(), world, pos, explosion);
    }

    /**
     * Determines if this block's collision box should be treated as though it can extend above its block space.
     * This can be used to replicate fence and wall behavior.
     */
    default boolean collisionExtendsVertically(BlockGetter world, BlockPos pos, Entity collidingEntity)
    {
        return self().getBlock().collisionExtendsVertically(self(), world, pos, collidingEntity);
    }

    /**
     * Called to determine whether this block should use the fluid overlay texture or flowing texture when it is placed under the fluid.
     *
     * @param world The world
     * @param pos Block position in world
     * @param fluidState The state of the fluid
     * @return Whether the fluid overlay texture should be used
     */
    default boolean shouldDisplayFluidOverlay(BlockAndTintGetter world, BlockPos pos, FluidState fluidState)
    {
        return self().getBlock().shouldDisplayFluidOverlay(self(), world, pos, fluidState);
    }

    /**
     * Returns the state that this block should transform into when right clicked by a tool.
     * For example: Used to determine if an axe can strip, a shovel can path, or a hoe can till.
     * Return null if vanilla behavior should be disabled.
     *
     * @param world The world
     * @param pos The block position in world
     * @param player The player clicking the block
     * @param stack The stack being used by the player
     * @param toolType The tool type to be considered when performing the action
     * @return The resulting state after the action has been performed
     */
    @Nullable
    default BlockState getToolModifiedState(Level world, BlockPos pos, Player player, ItemStack stack, ToolAction toolAction)
    {
        BlockState eventState = net.minecraftforge.event.ForgeEventFactory.onToolUse(self(), world, pos, player, stack, toolAction);
        return eventState != self() ? eventState : self().getBlock().getToolModifiedState(self(), world, pos, player, stack, toolAction);
    }

    /**
     * Checks if a player or entity handles movement on this block like scaffolding.
     *
     * @param entity The entity on the scaffolding
     * @return True if the block should act like scaffolding
     */
    default boolean isScaffolding(LivingEntity entity)
    {
        return self().getBlock().isScaffolding(self(), entity.level, entity.blockPosition(), entity);
    }
}
