/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.context.UseOnContext;
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
import net.minecraftforge.common.ToolActions;

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
     * for {@link LivingEntity}, this is {@code .91}.
     * {@link ItemEntity} uses {@code .98}, and
     * {@link FishingHook} uses {@code .92}.
     *
     * @param level the level
     * @param pos the position in the level
     * @param entity the entity in question
     * @return the factor by which the entity's motion should be multiplied
     */
    default float getFriction(LevelReader level, BlockPos pos, @Nullable Entity entity)
    {
        return self().getBlock().getFriction(self(), level, pos, entity);
    }

    /**
     * Get a light value for this block, taking into account the given state and coordinates, normal ranges are between 0 and 15
     */
    default int getLightEmission(BlockGetter level, BlockPos pos)
    {
        return self().getBlock().getLightEmission(self(), level, pos);
    }

    /**
     * Checks if a player or entity can use this block to 'climb' like a ladder.
     *
     * @param level The current level
     * @param pos Block position in level
     * @param entity The entity trying to use the ladder, CAN be null.
     * @return True if the block should act like a ladder
     */
    default boolean isLadder(LevelReader level, BlockPos pos, LivingEntity entity)
    {
        return self().getBlock().isLadder(self(), level, pos, entity);
    }

    /**
     * Determines if the player can harvest this block, obtaining it's drops when the block is destroyed.
     *
     * @param level The current level
     * @param pos The block's current position
     * @param player The player damaging the block
     * @return True to spawn the drops
     */
    default boolean canHarvestBlock(BlockGetter level, BlockPos pos, Player player)
    {
        return self().getBlock().canHarvestBlock(self(), level, pos, player);
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
     * @param level The current level
     * @param player The player damaging the block, may be null
     * @param pos Block position in level
     * @param willHarvest True if Block.harvestBlock will be called after this, if the return in true.
     *        Can be useful to delay the destruction of tile entities till after harvestBlock
     * @param fluid The current fluid and block state for the position in the level.
     * @return True if the block is actually destroyed.
     */
    default boolean onDestroyedByPlayer(Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        return self().getBlock().onDestroyedByPlayer(self(), level, pos, player, willHarvest, fluid);
    }

    /**
     * Determines if this block is classified as a Bed, Allowing
     * players to sleep in it, though the block has to specifically
     * perform the sleeping functionality in it's activated event.
     *
     * @param level The current level
     * @param pos Block position in level
     * @param sleeper The sleeper or camera entity, null in some cases.
     * @return True to treat this as a bed
     */
    default boolean isBed(BlockGetter level, BlockPos pos, @Nullable LivingEntity sleeper)
    {
        return self().getBlock().isBed(self(), level, pos, sleeper);
    }

    /**
     * Determines if a specified mob type can spawn on this block, returning false will
     * prevent any mob from spawning on the block.
     *
     * @param level The current level
     * @param pos Block position in level
     * @param type The Mob Category Type
     * @return True to allow a mob of the specified category to spawn, false to prevent it.
     */
    default boolean isValidSpawn(LevelReader level, BlockPos pos, Type type, EntityType<?> entityType)
    {
        return self().getBlock().isValidSpawn(self(), level, pos, type, entityType);
    }

    /**
     * Returns the position that the entity is moved to upon
     * respawning at this block.
     *
     * @param type The entity type used when checking if a dismount blockstate is dangerous. Currently always PLAYER.
     * @param level The current level
     * @param pos Block position in level
     * @param orientation The angle the entity had when setting the respawn point
     * @param entity The entity respawning, often null
     * @return The spawn position or the empty optional if respawning here is not possible
     */
    default Optional<Vec3> getRespawnPosition(EntityType<?> type, LevelReader level, BlockPos pos, float orientation, @Nullable LivingEntity entity)
    {
        return self().getBlock().getRespawnPosition(self(), type, level, pos, orientation, entity);
    }

    /**
     * Called when a user either starts or stops sleeping in the bed.
     *
     * @param level The current level
     * @param pos Block position in level
     * @param sleeper The sleeper or camera entity, null in some cases.
     * @param occupied True if we are occupying the bed, or false if they are stopping use of the bed
     */
    default void setBedOccupied(Level level, BlockPos pos, LivingEntity sleeper, boolean occupied)
    {
        self().getBlock().setBedOccupied(self(), level, pos, sleeper, occupied);
    }

   /**
    * Returns the direction of the block. Same values that
    * are returned by BlockDirectional
    *
    * @param level The current level
    * @param pos Block position in level
    * @return Bed direction
    */
    default Direction getBedDirection(LevelReader level, BlockPos pos)
    {
        return self().getBlock().getBedDirection(self(), level, pos);
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
        return self().getBlock().getExplosionResistance(self(), level, pos, explosion);
    }

    /**
     *
     * Called when A user uses the creative pick block button on this block
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, empty itemstack if nothing should be added.
     */
    default ItemStack getCloneItemStack(HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return self().getBlock().getCloneItemStack(self(), target, level, pos, player);
    }

    /**
     *  Allows a block to override the standard EntityLivingBase.updateFallState
     *  particles, this is a server side method that spawns particles with
     *  WorldServer.spawnParticle.
     *
     * @param level The current server level
     * @param pos The position of the block.
     * @param state2 The state at the specific world/pos
     * @param entity The entity that hit landed on the block
     * @param numberOfParticles That vanilla world have spawned
     * @return True to prevent vanilla landing particles from spawning
     */
    default boolean addLandingEffects(ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles)
    {
        return self().getBlock().addLandingEffects(self(), level, pos, state2, entity, numberOfParticles);
    }
   /**
    * Allows a block to override the standard vanilla running particles.
    * This is called from {@code Entity#spawnSprintParticle()} and is called both,
    * Client and server side, it's up to the implementor to client check / server check.
    * By default vanilla spawns particles only on the client and the server methods no-op.
    *
    * @param level  The level.
    * @param pos    The position at the entities feet.
    * @param entity The entity running on the block.
    * @return True to prevent vanilla running particles from spawning.
    */
    default boolean addRunningEffects(Level level, BlockPos pos, Entity entity)
    {
        return self().getBlock().addRunningEffects(self(), level, pos, entity);
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
    * @param level The current level
    * @param facing The direction relative to the given position the plant wants to be, typically its UP
    * @param plantable The plant that wants to check
    * @return True to allow the plant to be planted/stay.
    */
    default boolean canSustainPlant(BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable)
    {
        return self().getBlock().canSustainPlant(self(), level, pos, facing, plantable);
    }

   /**
    * Checks if this soil is fertile, typically this means that growth rates
    * of plants on this soil will be slightly sped up.
    * Only vanilla case is tilledField when it is within range of water.
    *
    * @param level The current level
    * @param pos Block position in level
    * @return True if the soil should be considered fertile.
    */
    default boolean isFertile(BlockGetter level, BlockPos pos)
    {
        return self().getBlock().isFertile(self(), level, pos);
    }

    /**
     * Determines if this block can be used as the frame of a conduit.
     *
     * @param level The current level
     * @param pos Block position in level
     * @param conduit Conduit position in level
     * @return True, to support the conduit, and make it active with this block.
     */
    default boolean isConduitFrame(LevelReader level, BlockPos pos, BlockPos conduit)
    {
        return self().getBlock().isConduitFrame(self(), level, pos, conduit);
    }

    /**
     * Determines if this block can be used as part of a frame of a nether portal.
     *
     * @param level The current level
     * @param pos Block position in level
     * @return True, to support being part of a nether portal frame, false otherwise.
     */
    default boolean isPortalFrame(BlockGetter level, BlockPos pos)
    {
        return self().getBlock().isPortalFrame(self(), level, pos);
    }

   /**
    * Gathers how much experience this block drops when broken.
    *
    * @param level The level
    * @param pos Block position
    * @param fortuneLevel fortune enchantment level of tool being used
    * @param silkTouchLevel silk touch enchantment level of tool being used
    * @return Amount of XP from breaking this block.
    */
    default int getExpDrop(LevelReader level, BlockPos pos, int fortuneLevel, int silkTouchLevel)
    {
        return self().getBlock().getExpDrop(self(), level, pos, fortuneLevel, silkTouchLevel);
    }

    default BlockState rotate(LevelAccessor level, BlockPos pos, Rotation direction)
    {
        return self().getBlock().rotate(self(), level, pos, direction);
    }

   /**
    * Determines the amount of enchanting power this block can provide to an enchanting table.
    * @param level The level
    * @param pos Block position in level
    * @return The amount of enchanting power this block produces.
    */
    default float getEnchantPowerBonus(LevelReader level, BlockPos pos)
    {
        return self().getBlock().getEnchantPowerBonus(self(), level, pos);
    }

   /**
    * Called when a tile entity on a side of this block changes is created or is destroyed.
    * @param level The level
    * @param pos Block position in level
    * @param neighbor Block position of neighbor
    */
    default void onNeighborChange(LevelReader level, BlockPos pos, BlockPos neighbor)
    {
        self().getBlock().onNeighborChange(self(), level, pos, neighbor);
    }

   /**
    * Called to determine whether to allow the a block to handle its own indirect power rather than using the default rules.
    * @param level The level
    * @param pos Block position in level
    * @param side The INPUT side of the block to be powered - ie the opposite of this block's output side
    * @return Whether Block#isProvidingWeakPower should be called when determining indirect power
    */
    default boolean shouldCheckWeakPower(LevelReader level, BlockPos pos, Direction side)
    {
        return self().getBlock().shouldCheckWeakPower(self(), level, pos, side);
    }

    /**
     * If this block should be notified of weak changes.
     * Weak changes are changes 1 block away through a solid block.
     * Similar to comparators.
     *
     * @param level The current level
     * @param pos Block position in level
     * @return true To be notified of changes
     */
    default boolean getWeakChanges(LevelReader level, BlockPos pos)
    {
        return self().getBlock().getWeakChanges(self(), level, pos);
    }

    /**
     * Sensitive version of getSoundType
     * @param level The level
     * @param pos The position. Note that the level may not necessarily have {@code state} here!
     * @param entity The entity that is breaking/stepping on/placing/hitting/falling on this block, or null if no entity is in this context
     * @return A SoundType to use
     */
    default SoundType getSoundType(LevelReader level, BlockPos pos, @Nullable Entity entity)
    {
        return self().getBlock().getSoundType(self(), level, pos, entity);
    }

    /**
     * @param level The level
     * @param pos The position of this state
     * @param beacon The position of the beacon
     * @return A float RGB [0.0, 1.0] array to be averaged with a beacon's existing beam color, or null to do nothing to the beam
     */
    @Nullable
    default float[] getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beacon)
    {
        return self().getBlock().getBeaconColorMultiplier(self(), level, pos, beacon);
    }

    /**
     * Used to determine the state 'viewed' by an entity (see
     * {@link Camera#getBlockAtCamera()}).
     * Can be used by fluid blocks to determine if the viewpoint is within the fluid or not.
     *
     * @param level     the level
     * @param pos       the position
     * @param viewpoint the viewpoint
     * @return the block state that should be 'seen'
     */
    default BlockState getStateAtViewpoint(BlockGetter level, BlockPos pos, Vec3 viewpoint)
    {
        return self().getBlock().getStateAtViewpoint(self(), level, pos, viewpoint);
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
     * @param level The current level
     * @param pos Block position in level
     * @param face The face that the fire is coming from
     * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
     */
    default int getFlammability(BlockGetter level, BlockPos pos, Direction face)
    {
        return self().getBlock().getFlammability(self(), level, pos, face);
    }

    /**
     * Called when fire is updating, checks if a block face can catch fire.
     *
     * @param level The current level
     * @param pos Block position in level
     * @param face The face that the fire is coming from
     * @return True if the face can be on fire, false otherwise.
     */
    default boolean isFlammable(BlockGetter level, BlockPos pos, Direction face)
    {
        return self().getBlock().isFlammable(self(), level, pos, face);
    }

    /**
     * If the block is flammable, this is called when it gets lit on fire.
     *
     * @param level The current level
     * @param pos Block position in level
     * @param face The face that the fire is coming from
     * @param igniter The entity that lit the fire
     */
    default void onCaughtFire(Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter)
    {
        self().getBlock().onCaughtFire(self(), level, pos, face, igniter);
    }

    /**
     * Called when fire is updating on a neighbor block.
     * The higher the number returned, the faster fire will spread around this block.
     *
     * @param level The current level
     * @param pos Block position in level
     * @param face The face that the fire is coming from
     * @return A number that is used to determine the speed of fire growth around the block
     */
    default int getFireSpreadSpeed(BlockGetter level, BlockPos pos, Direction face)
    {
        return self().getBlock().getFireSpreadSpeed(self(), level, pos, face);
    }

    /**
     * Currently only called by fire when it is on top of this block.
     * Returning true will prevent the fire from naturally dying during updating.
     * Also prevents firing from dying from rain.
     *
     * @param level The current level
     * @param pos Block position in level
     * @param side The face that the fire is coming from
     * @return True if this block sustains fire, meaning it will never go out.
     */
    default boolean isFireSource(LevelReader level, BlockPos pos, Direction side)
    {
        return self().getBlock().isFireSource(self(), level, pos, side);
    }

    /**
     * Determines if this block is can be destroyed by the specified entities normal behavior.
     *
     * @param level The current level
     * @param pos Block position in level
     * @return True to allow the ender dragon to destroy this block
     */
    default boolean canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity)
    {
        return self().getBlock().canEntityDestroy(self(), level, pos, entity);
    }

    /**
     * Determines if this block should set fire and deal fire damage
     * to entities coming into contact with it.
     *
     * @param level The current level
     * @param pos Block position in level
     * @return True if the block should deal damage
     */
    default boolean isBurning(BlockGetter level, BlockPos pos)
    {
       return self().getBlock().isBurning(self(), level, pos);
    }

    /**
     * Get the {@code BlockPathTypes} for this block. Return {@code null} for vanilla behavior.
     *
     * @return the PathNodeType
     */
    @Nullable
    default BlockPathTypes getBlockPathType(BlockGetter level, BlockPos pos)
    {
        return getBlockPathType(level, pos, null);
    }

    /**
     * Get the {@code PathNodeType} for this block. Return {@code null} for vanilla behavior.
     *
     * @return the PathNodeType
     */
    @Nullable
    default BlockPathTypes getBlockPathType(BlockGetter level, BlockPos pos, @Nullable Mob mob)
    {
        return self().getBlock().getAiPathNodeType(self(), level, pos, mob);
    }

    /**
     * Determines if this block should drop loot when exploded.
     */
    default boolean canDropFromExplosion(BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return self().getBlock().canDropFromExplosion(self(), level, pos, explosion);
    }

    /**
     * Called when the block is destroyed by an explosion.
     * Useful for allowing the block to take into account tile entities,
     * state, etc. when exploded, before it is removed.
     *
     * @param level The current level
     * @param pos Block position in level
     * @param explosion The explosion instance affecting the block
     */
    default void onBlockExploded(Level level, BlockPos pos, Explosion explosion)
    {
        self().getBlock().onBlockExploded(self(), level, pos, explosion);
    }

    /**
     * Determines if this block's collision box should be treated as though it can extend above its block space.
     * This can be used to replicate fence and wall behavior.
     */
    default boolean collisionExtendsVertically(BlockGetter level, BlockPos pos, Entity collidingEntity)
    {
        return self().getBlock().collisionExtendsVertically(self(), level, pos, collidingEntity);
    }

    /**
     * Called to determine whether this block should use the fluid overlay texture or flowing texture when it is placed under the fluid.
     *
     * @param level The level
     * @param pos Block position in level
     * @param fluidState The state of the fluid
     * @return Whether the fluid overlay texture should be used
     */
    default boolean shouldDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluidState)
    {
        return self().getBlock().shouldDisplayFluidOverlay(self(), level, pos, fluidState);
    }

    /**
     * Returns the state that this block should transform into when right-clicked by a tool.
     * For example: Used to determine if {@link ToolActions#AXE_STRIP an axe can strip},
     * {@link ToolActions#SHOVEL_FLATTEN a shovel can path}, or {@link ToolActions#HOE_TILL a hoe can till}.
     * Returns {@code null} if nothing should happen.
     *
     * @param context The use on context that the action was performed in
     * @param toolAction The action being performed by the tool
     * @param simulate If {@code true}, no actions that modify the world in any way should be performed. If {@code false}, the world may be modified.
     * @return The resulting state after the action has been performed
     */
    @Nullable
    default BlockState getToolModifiedState(UseOnContext context, ToolAction toolAction, boolean simulate)
    {
        BlockState eventState = net.minecraftforge.event.ForgeEventFactory.onToolUse(self(), context, toolAction, simulate);
        return eventState != self() ? eventState : self().getBlock().getToolModifiedState(self(), context, toolAction, simulate);
    }

    /**
     * Returns the state that this block should transform into when right-clicked by a tool.
     * For example: Used to determine if {@link ToolActions#AXE_STRIP an axe can strip} or {@link ToolActions#SHOVEL_FLATTEN a shovel can path}.
     * Returns {@code null} if nothing should happen.
     *
     * @param level The level
     * @param pos The block position in level
     * @param player The player clicking the block
     * @param stack The stack being used by the player
     * @param toolAction The tool type to be considered when performing the action
     * @return The resulting state after the action has been performed
     * @deprecated Use {@link #getToolModifiedState(UseOnContext, ToolAction, boolean)} instead
     */
    @Nullable
    // TODO 1.19: Remove
    @Deprecated(forRemoval = true, since = "1.18.2")
    default BlockState getToolModifiedState(Level level, BlockPos pos, Player player, ItemStack stack, ToolAction toolAction)
    {
        BlockState eventState = net.minecraftforge.event.ForgeEventFactory.onToolUse(self(), level, pos, player, stack, toolAction);
        return eventState != self() ? eventState : self().getBlock().getToolModifiedState(self(), level, pos, player, stack, toolAction);
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

    /**
     * Whether redstone dust should visually connect to this block on a side.
     * <p>
     * Modded redstone wire blocks should call this function to determine visual connections.
     *
     * @param level The level
     * @param pos The block position in level
     * @param direction The coming direction of the redstone dust connection (with respect to the block at pos)
     * @return True if redstone dust should visually connect on the side passed
     */
    default boolean canRedstoneConnectTo(BlockGetter level, BlockPos pos, @Nullable Direction direction)
    {
        return self().getBlock().canConnectRedstone(self(), level, pos, direction);
    }

    /**
     * Whether this block hides the neighbors face pointed towards by the given direction.
     * <p>
     * This method should only be used for blocks you don't control, for your own blocks override
     * {@link net.minecraft.world.level.block.Block#skipRendering(BlockState, BlockState, Direction)}
     * on the respective block instead
     *
     * @param level The world
     * @param pos The blocks position in the world
     * @param neighborState The neighboring blocks {@link BlockState}
     * @param dir The direction towards the neighboring block
     */
    default boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir)
    {
        return self().getBlock().hidesNeighborFace(level, pos, self(), neighborState, dir);
    }

    /**
     * Whether this block allows a neighboring block to hide the face of this block it touches.
     * If this returns true, {@link IForgeBlockState#hidesNeighborFace(BlockGetter, BlockPos, BlockState, Direction)}
     * will be called on the neighboring block.
     */
    default boolean supportsExternalFaceHiding()
    {
        return self().getBlock().supportsExternalFaceHiding(self());
    }
}
