/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.*;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;

public interface IForgeBlockState
{
    default BlockState getBlockState()
    {
        return (BlockState)this;
    }

    /**
     * Gets the slipperiness at the given location at the given state. Normally
     * between 0 and 1.
     * <p>
     * Note that entities may reduce slipperiness by a certain factor of their own;
     * for {@link net.minecraft.entity.EntityLivingBase}, this is {@code .91}.
     * {@link net.minecraft.entity.item.EntityItem} uses {@code .98}, and
     * {@link net.minecraft.entity.projectile.EntityFishHook} uses {@code .92}.
     *
     * @param world the world
     * @param pos the position in the world
     * @param entity the entity in question
     * @return the factor by which the entity's motion should be multiplied
     */
    default float getSlipperiness(IWorldReader world, BlockPos pos, @Nullable Entity entity)
    {
        return getBlockState().getBlock().getSlipperiness(getBlockState(), world, pos, entity);
    }

    /**
     * Get a light value for this block, taking into account the given state and coordinates, normal ranges are between 0 and 15
     */
    default int getLightValue(IEnviromentBlockReader world, BlockPos pos)
    {
        return getBlockState().getBlock().getLightValue(getBlockState(), world, pos);
    }

    /**
     * Checks if a player or entity can use this block to 'climb' like a ladder.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param entity The entity trying to use the ladder, CAN be null.
     * @return True if the block should act like a ladder
     */
    default boolean isLadder(IWorldReader world, BlockPos pos, LivingEntity entity)
    {
        return getBlockState().getBlock().isLadder(getBlockState(), world, pos, entity);
    }

    /**
     * Check if the face of a block should block rendering.
     *
     * Faces which are fully opaque should return true, faces with transparency
     * or faces which do not span the full size of the block should return false.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param face The side to check
     * @return True if the block is opaque on the specified side.
     */
    default boolean doesSideBlockRendering(IEnviromentBlockReader world, BlockPos pos, Direction face)
    {
        return getBlockState().getBlock().doesSideBlockRendering(getBlockState(), world, pos, face);
    }

    /**
     * Called throughout the code as a replacement for block instanceof BlockContainer
     * Moving this to the Block base class allows for mods that wish to extend vanilla
     * blocks, and also want to have a tile entity on that block, may.
     *
     * Return true from this function to specify this block has a tile entity.
     *
     * @return True if block has a tile entity, false otherwise
     */
    default boolean hasTileEntity()
    {
        return getBlockState().getBlock().hasTileEntity(getBlockState());
    }

    /**
     * Called throughout the code as a replacement for ITileEntityProvider.createNewTileEntity
     * Return the same thing you would from that function.
     * This will fall back to ITileEntityProvider.createNewTileEntity(World) if this block is a ITileEntityProvider
     *
     * @param world The world to create the TE in
     * @return A instance of a class extending TileEntity
     */
    @Nullable
    default TileEntity createTileEntity(IBlockReader world)
    {
        return getBlockState().getBlock().createTileEntity(getBlockState(), world);
    }

    /**
     * Determines if the player can harvest this block, obtaining it's drops when the block is destroyed.
     *
     * @param world The current world
     * @param pos The block's current position
     * @param player The player damaging the block
     * @return True to spawn the drops
     */
    default boolean canHarvestBlock(IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return getBlockState().getBlock().canHarvestBlock(getBlockState(), world, pos, player);
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
    default boolean removedByPlayer(World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid)
    {
        return getBlockState().getBlock().removedByPlayer(getBlockState(), world, pos, player, willHarvest, fluid);
    }

    /**
     * Determines if this block is classified as a Bed, Allowing
     * players to sleep in it, though the block has to specifically
     * perform the sleeping functionality in it's activated event.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param player The player or camera entity, null in some cases.
     * @return True to treat this as a bed
     */
    default boolean isBed(IBlockReader world, BlockPos pos, @Nullable LivingEntity player)
    {
        return getBlockState().getBlock().isBed(getBlockState(), world, pos, player);
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
    default boolean canCreatureSpawn(IWorldReader world, BlockPos pos, PlacementType type, EntityType<?> entityType)
    {
        return getBlockState().getBlock().canCreatureSpawn(getBlockState(), world, pos, type, entityType);
    }
    /**
     * Returns the position that the sleeper is moved to upon
     * waking up, or respawning at the bed.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param sleeper The sleeper or camera entity, null in some cases.
     * @return The spawn position
     */
    default Optional<Vec3d> getBedSpawnPosition(EntityType<?> type, IWorldReader world, BlockPos pos, @Nullable LivingEntity sleeper)
    {
        return getBlockState().getBlock().getBedSpawnPosition(type, getBlockState(), world, pos, sleeper);
    }

    /**
     * Called when a user either starts or stops sleeping in the bed.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param sleeper The sleeper or camera entity, null in some cases.
     * @param occupied True if we are occupying the bed, or false if they are stopping use of the bed
     */
    default void setBedOccupied(IWorldReader world, BlockPos pos, LivingEntity sleeper, boolean occupied)
    {
        getBlockState().getBlock().setBedOccupied(getBlockState(), world, pos, sleeper, occupied);
    }

   /**
    * Returns the direction of the block. Same values that
    * are returned by BlockDirectional
    *
    * @param world The current world
    * @param pos Block position in world
    * @return Bed direction
    */
    default Direction getBedDirection(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().getBedDirection(getBlockState(), world, pos);
    }

    /**
     * Determines if the current block is the foot half of the bed.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True if the current block is the foot side of a bed.
     */
    default boolean isBedFoot(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().isBedFoot(getBlockState(), world, pos);
    }

    /**
     * Called when a leaf should start its decay process.
     *
     * @param world The current world
     * @param pos Block position in world
     */
    default void beginLeaveDecay(IWorldReader world, BlockPos pos)
    {
        getBlockState().getBlock().beginLeaveDecay(getBlockState(), world, pos);
    }

    /**
     * Determines this block should be treated as an air block
     * by the rest of the code. This method is primarily
     * useful for creating pure logic-blocks that will be invisible
     * to the player and otherwise interact as air would.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True if the block considered air
     */
    default boolean isAir(IBlockReader world, BlockPos pos)
    {
        return getBlockState().getBlock().isAir(getBlockState(), world, pos);
    }

    /**
     * Used during tree growth to determine if newly generated leaves can replace this block.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return true if this block can be replaced by growing leaves.
     */
    default boolean canBeReplacedByLeaves(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().canBeReplacedByLeaves(getBlockState(), world, pos);
    }

    /**
     * Used during tree growth to determine if newly generated logs can replace this block.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @return true if this block can be replaced by growing leaves.
     */
    default boolean canBeReplacedByLogs(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().canBeReplacedByLogs(getBlockState(), world, pos);
    }

    /**
     * Determines if the current block is replaceable by Ore veins during world generation.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param target The generic target block the gen is looking for, Standards define stone
     *      for overworld generation, and neatherack for the nether.
     * @return True to allow this block to be replaced by a ore
     */
    default boolean isReplaceableOreGen(IWorldReader world, BlockPos pos, Predicate<BlockState> target)
    {
        return getBlockState().getBlock().isReplaceableOreGen(getBlockState(), world, pos, target);
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
        return getBlockState().getBlock().getExplosionResistance(getBlockState(), world, pos, exploder, explosion);
    }

    /**
     * Determine if this block can make a redstone connection on the side provided,
     * Useful to control which sides are inputs and outputs for redstone wires.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param side The side that is trying to make the connection, CAN BE NULL
     * @return True to make the connection
     */
    default boolean canConnectRedstone(IBlockReader world, BlockPos pos, @Nullable Direction side)
    {
        return getBlockState().getBlock().canConnectRedstone(getBlockState(), world, pos, side);
    }

    /**
     *
     * Called when A user uses the creative pick block button on this block
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, empty itemstack if nothing should be added.
     */
    default ItemStack getPickBlock(RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return getBlockState().getBlock().getPickBlock(getBlockState(), target, world, pos, player);
    }

    /**
     * Used by getTopSoilidOrLiquidBlock while placing biome decorations, villages, etc
     * Also used to determine if the player can spawn in this block.
     *
     * @return False to disallow spawning.
     */
    default boolean isFoliage(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().isFoliage(getBlockState(), world, pos);
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
    default boolean addLandingEffects(ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles)
    {
        return getBlockState().getBlock().addLandingEffects(getBlockState(), worldserver, pos, state2, entity, numberOfParticles);
    }
   /**
    * Allows a block to override the standard vanilla running particles.
    * This is called from {@link Entity#spawnRunningParticles} and is called both,
    * Client and server side, it's up to the implementor to client check / server check.
    * By default vanilla spawns particles only on the client and the server methods no-op.
    *
    * @param world  The world.
    * @param pos    The position at the entities feet.
    * @param entity The entity running on the block.
    * @return True to prevent vanilla running particles from spawning.
    */
    default boolean addRunningEffects(World world, BlockPos pos, Entity entity)
    {
        return getBlockState().getBlock().addRunningEffects(getBlockState(), world, pos, entity);
    }

    /**
     * Spawn a digging particle effect in the world, this is a wrapper
     * around EffectRenderer.addBlockHitEffects to allow the block more
     * control over the particles. Useful when you have entirely different
     * texture sheets for different sides/locations in the world.
     *
     * @param world The current world
     * @param target The target the player is looking at {x/y/z/side/sub}
     * @param manager A reference to the current particle manager.
     * @return True to prevent vanilla digging particles form spawning.
     */
    @OnlyIn(Dist.CLIENT)
    default boolean addHitEffects(World world, RayTraceResult target, ParticleManager manager)
    {
        return getBlockState().getBlock().addHitEffects(getBlockState(), world, target, manager);
    }

   /**
    * Spawn particles for when the block is destroyed. Due to the nature
    * of how this is invoked, the x/y/z locations are not always guaranteed
    * to host your block. So be sure to do proper sanity checks before assuming
    * that the location is this block.
    *
    * @param world The current world
    * @param pos Position to spawn the particle
    * @param manager A reference to the current particle manager.
    * @return True to prevent vanilla break particles from spawning.
    */
    @OnlyIn(Dist.CLIENT)
    default boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
    {
        return getBlockState().getBlock().addDestroyEffects(getBlockState(), world, pos, manager);
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
    default boolean canSustainPlant(IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable)
    {
        return getBlockState().getBlock().canSustainPlant(getBlockState(), world, pos, facing, plantable);
    }

    /**
     * Called when a plant grows on this block, only implemented for saplings using the WorldGen*Trees classes right now.
     * Modder may implement this for custom plants.
     * This does not use ForgeDirection, because large/huge trees can be located in non-representable direction,
     * so the source location is specified.
     * Currently this just changes the block to dirt if it was grass.
     *
     * Note: This happens DURING the generation, the generation may not be complete when this is called.
     *
     * @param world Current world
     * @param pos Block position in world
     * @param source Source plant's position in world
     */
    default void onPlantGrow(IWorld world, BlockPos pos, BlockPos source)
    {
        getBlockState().getBlock().onPlantGrow(getBlockState(), world, pos, source);
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
    default boolean isFertile(IBlockReader world, BlockPos pos)
    {
        return getBlockState().getBlock().isFertile(getBlockState(), world, pos);
    }

   /**
    * Determines if this block can be used as the base of a beacon.
    *
    * @param world The current world
    * @param pos Block position in world
    * @param beacon Beacon position in world
    * @return True, to support the beacon, and make it active with this block.
    */
    default boolean isBeaconBase(IWorldReader world, BlockPos pos, BlockPos beacon)
    {
        return getBlockState().getBlock().isBeaconBase(getBlockState(), world, pos, beacon);
    }

   /**
    * Gathers how much experience this block drops when broken.
    *
    * @param world The world
    * @param pos Block position
    * @param fortune
    * @return Amount of XP from breaking this block.
    */
    default int getExpDrop(IWorldReader world, BlockPos pos, int fortune, int silktouch)
    {
        return getBlockState().getBlock().getExpDrop(getBlockState(), world, pos, fortune, silktouch);
    }

    default BlockState rotate(IWorld world, BlockPos pos, Rotation direction)
    {
        return getBlockState().getBlock().rotate(getBlockState(), world, pos, direction);
    }

   /**
    * Determines the amount of enchanting power this block can provide to an enchanting table.
    * @param world The World
    * @param pos Block position in world
    * @return The amount of enchanting power this block produces.
    */
    default float getEnchantPowerBonus(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().getEnchantPowerBonus(getBlockState(), world, pos);
    }

    /**
     * //TODO: Re-Evaluate
     * Gathers how much experience this block drops when broken.
     *
     * @param state The current state
     * @param world The world
     * @param pos Block position
     * @param fortune
     * @return Amount of XP from breaking this block.
     */
    default boolean recolorBlock(IWorld world, BlockPos pos, Direction facing, DyeColor color)
    {
        return getBlockState().getBlock().recolorBlock(getBlockState(), world, pos, facing, color);
    }

   /**
    * Called when a tile entity on a side of this block changes is created or is destroyed.
    * @param world The world
    * @param pos Block position in world
    * @param neighbor Block position of neighbor
    */
    default void onNeighborChange(IWorldReader world, BlockPos pos, BlockPos neighbor)
    {
        getBlockState().getBlock().onNeighborChange(getBlockState(), world, pos, neighbor);
    }

   /**
    * Called on an Observer block whenever an update for an Observer is received.
    *
    * @param observerState The Observer block's state.
    * @param world The current world.
    * @param pos The Observer block's position.
    * @param changed The updated block.
    * @param changedPos The updated block's position.
    */
    default void observedNeighborChange(World world, BlockPos pos, Block changed, BlockPos changedPos)
    {
        getBlockState().getBlock().observedNeighborChange(getBlockState(), world, pos, changed, changedPos);
    }

   /**
    * Called to determine whether to allow the a block to handle its own indirect power rather than using the default rules.
    * @param world The world
    * @param pos Block position in world
    * @param side The INPUT side of the block to be powered - ie the opposite of this block's output side
    * @return Whether Block#isProvidingWeakPower should be called when determining indirect power
    */
    default boolean shouldCheckWeakPower(IWorldReader world, BlockPos pos, Direction side)
    {
        return getBlockState().getBlock().shouldCheckWeakPower(getBlockState(), world, pos, side);
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
    default boolean getWeakChanges(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().getWeakChanges(getBlockState(), world, pos);
    }

    /**
     * Queries the class of tool required to harvest this block, if null is returned
     * we assume that anything can harvest this block.
     */
    default ToolType getHarvestTool()
    {
        return getBlockState().getBlock().getHarvestTool(getBlockState());
    }

    default int getHarvestLevel()
    {
        return getBlockState().getBlock().getHarvestLevel(getBlockState());
    }

    /**
     * Checks if the specified tool type is efficient on this block,
     * meaning that it digs at full speed.
     */
    default boolean isToolEffective(ToolType tool)
    {
        return getBlockState().getBlock().isToolEffective(getBlockState(), tool);
    }

    /**
     * Can return IExtendedBlockState
     */
    default BlockState getExtendedState(IBlockReader world, BlockPos pos)
    {
        return getBlockState().getBlock().getExtendedState(getBlockState(), world, pos);
    }

    /**
     * Queries if this blockstate should render in a given layer.
     * A custom {@link IBakedModel} can use {@link net.minecraftforge.client.MinecraftForgeClient#getRenderLayer()} to alter the model based on layer.
     */
    default boolean canRenderInLayer(BlockRenderLayer layer)
    {
        return getBlockState().getBlock().canRenderInLayer(getBlockState(), layer);
    }

    /**
     * Sensitive version of getSoundType
     * @param world The world
     * @param pos The position. Note that the world may not necessarily have {@code state} here!
     * @param entity The entity that is breaking/stepping on/placing/hitting/falling on this block, or null if no entity is in this context
     * @return A SoundType to use
     */
    default SoundType getSoundType(IWorldReader world, BlockPos pos, @Nullable Entity entity)
    {
        return getBlockState().getBlock().getSoundType(getBlockState(), world, pos, entity);
    }

    /**
     * @param world The world
     * @param pos The position of this state
     * @param beaconPos The position of the beacon
     * @return A float RGB [0.0, 1.0] array to be averaged with a beacon's existing beam color, or null to do nothing to the beam
     */
    @Nullable
    default float[] getBeaconColorMultiplier(IWorldReader world, BlockPos pos, BlockPos beacon)
    {
        return getBlockState().getBlock().getBeaconColorMultiplier(getBlockState(), world, pos, beacon);
    }

    /**
     * Use this to change the fog color used when the entity is "inside" a material.
     * Vec3d is used here as "r/g/b" 0 - 1 values.
     *
     * @param world         The world.
     * @param pos           The position at the entity viewport.
     * @param entity        the entity
     * @param originalColor The current fog color, You are not expected to use this, Return as the default if applicable.
     * @return The new fog color.
     */
    @OnlyIn(Dist.CLIENT)
    default Vec3d getFogColor(IWorldReader world, BlockPos pos, Entity entity, Vec3d originalColor, float partialTicks)
    {
        return getBlockState().getBlock().getFogColor(getBlockState(), world, pos, entity, originalColor, partialTicks);
    }

    /**
     * Used to determine the state 'viewed' by an entity (see
     * {@link ActiveRenderInfo#getBlockStateAtEntityViewpoint(World, Entity, float)}).
     * Can be used by fluid blocks to determine if the viewpoint is within the fluid or not.
     *
     * @param world     the world
     * @param pos       the position
     * @param viewpoint the viewpoint
     * @return the block state that should be 'seen'
     */
    default BlockState getStateAtViewpoint(IBlockReader world, BlockPos pos, Vec3d viewpoint)
    {
        return getBlockState().getBlock().getStateAtViewpoint(getBlockState(), world, pos, viewpoint);
    }

    /** //TODO: Re-Evaluate
     * Gets the {@link IBlockState} to place
     * @param world The world the block is being placed in
     * @param pos The position the block is being placed at
     * @param facing The side the block is being placed on
     * @param hitX The X coordinate of the hit vector
     * @param hitY The Y coordinate of the hit vector
     * @param hitZ The Z coordinate of the hit vector
     * @param meta The metadata of {@link ItemStack} as processed by {@link Item#getMetadata(int)}
     * @param placer The entity placing the block
     * @param hand The player hand used to place this block
     * @return The state to be placed in the world
     */
    default BlockState getStateForPlacement(Direction facing, BlockState state2, IWorld world, BlockPos pos1, BlockPos pos2, Hand hand)
    {
        return getBlockState().getBlock().getStateForPlacement(getBlockState(), facing, state2, world, pos1, pos2, hand);
    }

    /**
     * Determines if another block can connect to this block
     *
     * @param world The current world
     * @param pos The position of this block
     * @param facing The side the connecting block is on
     * @return True to allow another block to connect to this block
     */
    default boolean canBeConnectedTo(IBlockReader world, BlockPos pos, Direction facing)
    {
        return getBlockState().getBlock().canBeConnectedTo(getBlockState(), world, pos, facing);
    }

    /**
     * @param state The state
     * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
     */
    default boolean isStickyBlock()
    {
        return getBlockState().getBlock().isStickyBlock(getBlockState());
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
    default int getFlammability(IBlockReader world, BlockPos pos, Direction face)
    {
        return getBlockState().getBlock().getFlammability(getBlockState(), world, pos, face);
    }

    /**
     * Called when fire is updating, checks if a block face can catch fire.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param face The face that the fire is coming from
     * @return True if the face can be on fire, false otherwise.
     */
    default boolean isFlammable(IBlockReader world, BlockPos pos, Direction face)
    {
        return getBlockState().getBlock().isFlammable(getBlockState(), world, pos, face);
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
    default int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face)
    {
        return getBlockState().getBlock().getFireSpreadSpeed(getBlockState(), world, pos, face);
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
    default boolean isFireSource(IBlockReader world, BlockPos pos, Direction side)
    {
        return getBlockState().getBlock().isFireSource(getBlockState(), world, pos, side);
    }

    /**
     * Determines if this block is can be destroyed by the specified entities normal behavior.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True to allow the ender dragon to destroy this block
     */
    default boolean canEntityDestroy(IBlockReader world, BlockPos pos, Entity entity)
    {
        return getBlockState().getBlock().canEntityDestroy(getBlockState(), world, pos, entity);
    }

    /**
     * Get the rotations that can apply to the block at the specified coordinates. Null means no rotations are possible.
     * Note, this is up to the block to decide. It may not be accurate or representative.
     * @param world The world
     * @param pos Block position in world
     * @return An array of valid axes to rotate around, or null for none or unknown
     */
    @Nullable
    default Direction[] getValidRotations(IBlockReader world, BlockPos pos)
    {
        return getBlockState().getBlock().getValidRotations(getBlockState(), world, pos);
    }

    /**
     * Determines if this block should set fire and deal fire damage
     * to entities coming into contact with it.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True if the block should deal damage
     */
    default boolean isBurning(IBlockReader world, BlockPos pos)
    {
       return getBlockState().getBlock().isBurning(getBlockState(), world, pos);
    }

    /**
     * Get the {@code PathNodeType} for this block. Return {@code null} for vanilla behavior.
     *
     * @return the PathNodeType
     */
    @Nullable
    default PathNodeType getAiPathNodeType(IBlockReader world, BlockPos pos, @Nullable MobEntity entity)
    {
        return getBlockState().getBlock().getAiPathNodeType(getBlockState(), world, pos, entity);
    }

    /**
     * Determines if this block should drop loot when exploded.
     */
    default boolean canDropFromExplosion(IBlockReader world, BlockPos pos, Explosion explosion)
    {
        return getBlockState().getBlock().canDropFromExplosion(getBlockState(), world, pos, explosion);
    }
}
