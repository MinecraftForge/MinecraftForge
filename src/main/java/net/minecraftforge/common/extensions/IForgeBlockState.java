/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry.SpawnPlacementType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;

public interface IForgeBlockState
{
    default IBlockState getBlockState()
    {
        return (IBlockState)this;
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
    default int getLightValue(IWorldReader world, BlockPos pos)
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
    default boolean isLadder(IWorldReader world, BlockPos pos, EntityLivingBase entity)
    {
        return getBlockState().getBlock().isLadder(getBlockState(), world, pos, entity);
    }

    /**
     * Return true if the block is a normal, solid cube.  This
     * determines indirect power state, entity ejection from blocks, and a few
     * others.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True if the block is a full cube
     */
    default boolean isNormalCube(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().isNormalCube(getBlockState(), world, pos);
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
    default boolean doesSideBlockRendering(IWorldReader world, BlockPos pos, EnumFacing face)
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

    default boolean canSilkHarvest(IWorldReader world, BlockPos pos, EntityPlayer player)
    {
        return getBlockState().getBlock().canSilkHarvest(getBlockState(), world, pos, player);
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
    default boolean isBed(IWorldReader world, BlockPos pos, @Nullable EntityPlayer player)
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
    default boolean canCreatureSpawn(IWorldReader world, BlockPos pos, SpawnPlacementType type)
    {
        return getBlockState().getBlock().canCreatureSpawn(getBlockState(), world, pos, type);
    }
    /**
     * Returns the position that the player is moved to upon
     * waking up, or respawning at the bed.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param player The player or camera entity, null in some cases.
     * @return The spawn position
     */
    @Nullable
    default BlockPos getBedSpawnPosition(IWorldReader world, BlockPos pos, @Nullable EntityPlayer player)
    {
        return getBlockState().getBlock().getBedSpawnPosition(getBlockState(), world, pos, player);
    }

    /**
     * Called when a user either starts or stops sleeping in the bed.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param player The player or camera entity, null in some cases.
     * @param occupied True if we are occupying the bed, or false if they are stopping use of the bed
     */
    default void setBedOccupied(IWorldReader world, BlockPos pos, EntityPlayer player, boolean occupied)
    {
        getBlockState().getBlock().setBedOccupied(getBlockState(), world, pos, player, occupied);
    }

   /**
    * Returns the direction of the block. Same values that
    * are returned by BlockDirectional
    *
    * @param world The current world
    * @param pos Block position in world
    * @return Bed direction
    */
    default EnumFacing getBedDirection(IWorldReader world, BlockPos pos)
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
     * Determines if this block can prevent leaves connected to it from decaying.
     * @param world The current world
     * @param pos Block position in world
     * @return true if the presence this block can prevent leaves from decaying.
     */
    default boolean canSustainLeaves(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().canSustainLeaves(getBlockState(), world, pos);
    }

    /**
     * Determines if this block is considered a leaf block, used to apply the leaf decay and generation system.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return true if this block is considered leaves.
     */
    default boolean isLeaves(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().isLeaves(getBlockState(), world, pos);
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
    default boolean isAir(IWorldReader world, BlockPos pos)
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
     *
     * @param world The current world
     * @param pos Block position in world
     * @return  true if the block is wood (logs)
     */
    default boolean isWood(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().isWood(getBlockState(), world, pos);
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
    default boolean isReplaceableOreGen(IWorldReader world, BlockPos pos, Predicate<IBlockState> target)
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
     * Called when the block is destroyed by an explosion.
     * Useful for allowing the block to take into account tile entities,
     * state, etc. when exploded, before it is removed.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param explosion The explosion instance affecting the block
     */
    default void onBlockExploded(World world, BlockPos pos, Explosion explosion)
    {
        getBlockState().getBlock().onBlockExploded(getBlockState(), world, pos, explosion);
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
    default boolean canConnectRedstone(IWorldReader world, BlockPos pos, @Nullable EnumFacing side)
    {
        return getBlockState().getBlock().canConnectRedstone(getBlockState(), world, pos, side);
    }

    /**
     * Determines if a torch can be placed on the top surface of this block.
     * Useful for creating your own block that torches can be on, such as fences.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True to allow the torch to be placed
     */
    default boolean canPlaceTorchOnTop(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().canPlaceTorchOnTop(getBlockState(), world, pos);
    }

    /**
     *
     * Called when A user uses the creative pick block button on this block
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, empty itemstack if nothing should be added.
     */
    default ItemStack getPickBlock(RayTraceResult target, IBlockReader world, BlockPos pos, EntityPlayer player)
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
    default boolean addLandingEffects(WorldServer worldserver, BlockPos pos, IBlockState state2, EntityLivingBase entity, int numberOfParticles)
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
    default boolean canSustainPlant(IWorldReader world, BlockPos pos, EnumFacing facing, IPlantable plantable)
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
    default void onPlantGrow(World world, BlockPos pos, BlockPos source)
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
    default boolean isFertile(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().isFertile(getBlockState(), world, pos);
    }

   /**
    * Location aware and overrideable version of the lightOpacity array,
    * return the number to subtract from the light value when it passes through this block.
    *
    * This is not guaranteed to have the tile entity in place before this is called, so it is
    * Recommended that you have your tile entity call relight after being placed if you
    * rely on it for light info.
    *
    * @param state The Block state
    * @param world The current world
    * @param pos Block position in world
    * @return The amount of light to block, 0 for air, 255 for fully opaque.
    */
    default int getLightOpacity(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().getLightOpacity(getBlockState(), world, pos);
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
    default int getExpDrop(IWorldReader world, BlockPos pos, int fortune)
    {
        return getBlockState().getBlock().getExpDrop(getBlockState(), world, pos, fortune);
    }

    default boolean rotateBlock(IWorld world, BlockPos pos, EnumFacing axis)
    {
        return getBlockState().getBlock().rotateBlock(getBlockState(), world, pos, axis);
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

    default boolean recolorBlock(IWorld world, BlockPos pos, EnumFacing facing, EnumDyeColor color)
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
    default boolean shouldCheckWeakPower(IWorldReader world, BlockPos pos, EnumFacing side)
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
    default IBlockState getExtendedState(IBlockReader world, BlockPos pos)
    {
        return getBlockState().getBlock().getExtendedState(getBlockState(), world, pos);
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
     * @return null for default behavior, true if the entity is within the material, false if it was not.
     */
    @Nullable
    default Boolean isEntityInsideMaterial(IWorldReader world, BlockPos pos, Entity entity, double yToTest, Material material, boolean testingHead)
    {
        return getBlockState().getBlock().isEntityInsideMaterial(getBlockState(), world, pos, entity, yToTest, material, testingHead);
    }

    /**
     * Called when boats or fishing hooks are inside the block to check if they are inside
     * the material requested.
     *
     * @param world world that is being tested.
     * @param pos block thats being tested.
     * @param boundingBox box to test, generally the bounds of an entity that are besting tested.
     * @param material to check for.
     * @return null for default behavior, true if the box is within the material, false if it was not.
     */
    @Nullable
    default Boolean isAABBInsideMaterial(IWorldReader world, BlockPos pos, AxisAlignedBB boundingBox, Material material)
    {
        return getBlockState().getBlock().isAABBInsideMaterial(getBlockState(), world, pos, boundingBox, material);
    }

    /**
     * Called when entities are moving to check if they are inside a liquid
     *
     * @param world world that is being tested.
     * @param pos block thats being tested.
     * @param boundingBox box to test, generally the bounds of an entity that are besting tested.
     * @return null for default behavior, true if the box is within the material, false if it was not.
     */
    @Nullable
    default Boolean isAABBInsideLiquid(IWorldReader world, BlockPos pos, AxisAlignedBB boundingBox)
    {
        return getBlockState().getBlock().isAABBInsideLiquid(getBlockState(), world, pos, boundingBox);
    }

    /**
     * Queries if this block should render in a given layer.
     * ISmartBlockModel can use {@link net.minecraftforge.client.MinecraftForgeClient#getRenderLayer()} to alter their model based on layer.
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
    default IBlockState getStateAtViewpoint(IWorldReader world, BlockPos pos, Vec3d viewpoint)
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
    default IBlockState getStateForPlacement(EnumFacing facing, IBlockState state2, IWorld world, BlockPos pos1, BlockPos pos2, EnumHand hand)
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
    default boolean canBeConnectedTo(IWorldReader world, BlockPos pos, EnumFacing facing)
    {
        return getBlockState().getBlock().canBeConnectedTo(getBlockState(), world, pos, facing);
    }

    /**
     * Get the {@code PathNodeType} for this block. Return {@code null} for vanilla behavior.
     *
     * @return the PathNodeType
     */
    @Nullable
    default PathNodeType getAiPathNodeType(IWorldReader world, BlockPos pos)
    {
        return getBlockState().getBlock().getAiPathNodeType(getBlockState(), world, pos);
    }

    /**
     * @param blockState The state for this block
     * @param world The world this block is in
     * @param pos The position of this block
     * @param side The side of this block that the chest lid is trying to open into
     * @return true if the chest should be prevented from opening by this block
     */
    default boolean doesSideBlockChestOpening(IWorldReader world, BlockPos pos, EnumFacing side)
    {
        return getBlockState().getBlock().doesSideBlockChestOpening(getBlockState(), world, pos, side);
    }

    /**
     * @param state The state
     * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
     */
    default boolean isStickyBlock()
    {
        return getBlockState().getBlock().isStickyBlock(getBlockState());
    }

}
