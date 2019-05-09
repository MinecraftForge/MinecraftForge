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

import java.util.Collection;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.EndDimension;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;

public interface IForgeBlock
{
    default Block getBlock()
    {
        return (Block) this;
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
     * @param state state of the block
     * @param world the world
     * @param pos the position in the world
     * @param entity the entity in question
     * @return the factor by which the entity's motion should be multiplied
     */
    float getSlipperiness(IBlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity);

    /**
     * Get a light value for this block, taking into account the given state and coordinates, normal ranges are between 0 and 15
     *
     * @param state
     * @param world
     * @param pos
     * @return The light value
     */
    default int getLightValue(IBlockState state, IWorldReader world, BlockPos pos)
    {
        return state.getLightValue();
    }

    /**
     * Checks if a player or entity can use this block to 'climb' like a ladder.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param entity The entity trying to use the ladder, CAN be null.
     * @return True if the block should act like a ladder
     */
    default boolean isLadder(IBlockState state, IWorldReader world, BlockPos pos, EntityLivingBase entity)
    {
        return false;
    }

    /**
     * Return true if the block is a normal, solid cube.  This
     * determines indirect power state, entity ejection from blocks, and a few
     * others.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @return True if the block is a full cube
     */
    default boolean isNormalCube(IBlockState state, IBlockReader world, BlockPos pos)
    {
        return state.isNormalCube();
    }
    /**
     * Check if the face of a block should block rendering.
     *
     * Faces which are fully opaque should return true, faces with transparency
     * or faces which do not span the full size of the block should return false.
     *
     * @param state The current block state
     * @param world The current world
     * @param pos Block position in world
     * @param face The side to check
     * @return True if the block is opaque on the specified side.
     */
    default boolean doesSideBlockRendering(IBlockState state, IWorldReader world, BlockPos pos, EnumFacing face)
    {
       return state.isOpaqueCube(world, pos);
    }

    /**
     * Determines if this block should set fire and deal fire damage
     * to entities coming into contact with it.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True if the block should deal damage
     */
    default boolean isBurning(IBlockState state, IBlockReader world, BlockPos pos)
    {
        return false;
    }

    /**
     * Called throughout the code as a replacement for block instanceof BlockContainer
     * Moving this to the Block base class allows for mods that wish to extend vanilla
     * blocks, and also want to have a tile entity on that block, may.
     *
     * Return true from this function to specify this block has a tile entity.
     *
     * @param state State of the current block
     * @return True if block has a tile entity, false otherwise
     */
    @SuppressWarnings("deprecation")
    default boolean hasTileEntity(IBlockState state)
    {
        return this instanceof ITileEntityProvider;
    }

    /**
     * Called throughout the code as a replacement for ITileEntityProvider.createNewTileEntity
     * Return the same thing you would from that function.
     * This will fall back to ITileEntityProvider.createNewTileEntity(World) if this block is a ITileEntityProvider
     *
     * @param state The state of the current block
     * @param world The world to create the TE in
     * @return A instance of a class extending TileEntity
     */
    @SuppressWarnings("deprecation")
    @Nullable
    default TileEntity createTileEntity(IBlockState state, IBlockReader world)
    {
        if (getBlock() instanceof ITileEntityProvider)
            return ((ITileEntityProvider)getBlock()).createNewTileEntity(world);
        return null;
    }

    /**
     * Return true from this function if the player with silk touch can harvest this block directly, and not it's normal drops.
     *
     * @param state current block state
     * @param world The world
     * @param pos Block position in world
     * @param player The player doing the harvesting
     * @return True if the block can be directly harvested using silk touch
     */
    boolean canSilkHarvest(IBlockState state, IWorldReader world, BlockPos pos, EntityPlayer player);

    /**
     * Determines if the player can harvest this block, obtaining it's drops when the block is destroyed.
     *
     * @param world The current world
     * @param pos The block's current position
     * @param player The player damaging the block
     * @return True to spawn the drops
     */
    default public boolean canHarvestBlock(IBlockState state, IBlockReader world, BlockPos pos, EntityPlayer player)
    {
        return ForgeHooks.canHarvestBlock(state, player, world, pos);
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
     * @param state The current state.
     * @param world The current world
     * @param player The player damaging the block, may be null
     * @param pos Block position in world
     * @param willHarvest True if Block.harvestBlock will be called after this, if the return in true.
     *        Can be useful to delay the destruction of tile entities till after harvestBlock
     * @param fluid The current fluid state at current position
     * @return True if the block is actually destroyed.
     */
    default boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest, IFluidState fluid)
    {
        getBlock().onBlockHarvested(world, pos, state, player);
        return world.setBlockState(pos, fluid.getBlockState(), world.isRemote ? 11 : 3);
    }

    /**
     * Determines if this block is classified as a Bed, Allowing
     * players to sleep in it, though the block has to specifically
     * perform the sleeping functionality in it's activated event.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param player The player or camera entity, null in some cases.
     * @return True to treat this as a bed
     */
    default boolean isBed(IBlockState state, IBlockReader world, BlockPos pos, @Nullable Entity player)
    {
        return this.getBlock() instanceof BlockBed; //TODO: Forge: Keep isBed function?
    }

    /**
     * Determines if a specified mob type can spawn on this block, returning false will
     * prevent any mob from spawning on the block.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param type The Mob Category Type
     * @return True to allow a mob of the specified category to spawn, false to prevent it.
     */
    default boolean canCreatureSpawn(IBlockState state, IWorldReaderBase world, BlockPos pos, EntitySpawnPlacementRegistry.SpawnPlacementType type, @Nullable EntityType<? extends EntityLiving> entityType)
    {
        return state.isTopSolid() || entityType != null && EntitySpawnPlacementRegistry.func_209345_a(entityType, state);
    }
    /**
     * Returns the position that the player is moved to upon
     * waking up, or respawning at the bed.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param player The player or camera entity, null in some cases.
     * @return The spawn position
     */
    @Nullable
    default BlockPos getBedSpawnPosition(IBlockState state, IBlockReader world, BlockPos pos, @Nullable EntityPlayer player)
    {
        if (world instanceof World)
        {
            return BlockBed.getSafeExitLocation(world,pos,0);
        }

        return null;
    }

    /**
     * Called when a user either starts or stops sleeping in the bed.
     * @param state
     *
     * @param world The current world
     * @param pos Block position in world
     * @param player The player or camera entity, null in some cases.
     * @param occupied True if we are occupying the bed, or false if they are stopping use of the bed
     */
    default void setBedOccupied(IBlockState state, IWorldReader world, BlockPos pos, EntityPlayer player, boolean occupied)
    {
        if (world instanceof IWorldWriter)
        {
            ((IWorldWriter)world).setBlockState(pos, state.with(BlockBed.OCCUPIED,occupied), 4);
        }
    }

   /**
    * Returns the direction of the block. Same values that
    * are returned by BlockDirectional
    *
    * @param state The current state
    * @param world The current world
    * @param pos Block position in world
    * @return Bed direction
    */
    default EnumFacing getBedDirection(IBlockState state, IWorldReader world, BlockPos pos)
    {
        return state.get(BlockHorizontal.HORIZONTAL_FACING);
    }

    /**
     * Determines if the current block is the foot half of the bed.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True if the current block is the foot side of a bed.
     */
    default boolean isBedFoot(IBlockState state, IWorldReader world, BlockPos pos)
    {
        return state.get(BlockBed.PART) == BedPart.FOOT;
    }

    /**
     * Called when a leaf should start its decay process.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     */
    default void beginLeaveDecay(IBlockState state, IWorldReader world, BlockPos pos) {}

    /**
     * Determines this block should be treated as an air block
     * by the rest of the code. This method is primarily
     * useful for creating pure logic-blocks that will be invisible
     * to the player and otherwise interact as air would.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @return True if the block considered air
     */
    default boolean isAir(IBlockState state, IBlockReader world, BlockPos pos)
    {
        return state.getMaterial() == Material.AIR;
    }

    /**
     * Used during tree growth to determine if newly generated leaves can replace this block.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @return true if this block can be replaced by growing leaves.
     */
    default boolean canBeReplacedByLeaves(IBlockState state, IWorldReaderBase world, BlockPos pos)
    {
        return (isAir(state, world, pos) || state.isIn(BlockTags.LEAVES)) || !state.isOpaqueCube(world, pos);
    }

    /**
     * Determines if the current block is replaceable by Ore veins during world generation.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param target The generic target block the gen is looking for, Standards define stone
     *      for overworld generation, and neatherack for the nether.
     * @return True to allow this block to be replaced by a ore
     */
    default boolean isReplaceableOreGen(IBlockState state, IWorldReader world, BlockPos pos, Predicate<IBlockState> target)
    {
        return target.test(state);
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
    default float getExplosionResistance(IBlockState state, IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion)
    {
        return this.getBlock().getExplosionResistance();
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
    default void onBlockExploded(IBlockState state, World world, BlockPos pos, Explosion explosion) {
        world.removeBlock(pos);
        getBlock().onExplosionDestroy(world, pos, explosion);
    }

    /**
     * Determine if this block can make a redstone connection on the side provided,
     * Useful to control which sides are inputs and outputs for redstone wires.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param side The side that is trying to make the connection, CAN BE NULL
     * @return True to make the connection
     */
    default boolean canConnectRedstone(IBlockState state, IBlockReader world, BlockPos pos, @Nullable EnumFacing side)
    {
        return state.canProvidePower() && side != null;
    }

    /**
     * Determines if a torch can be placed on the top surface of this block.
     * Useful for creating your own block that torches can be on, such as fences.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @return True to allow the torch to be placed
     */
    default boolean canPlaceTorchOnTop(IBlockState state, IWorldReaderBase world, BlockPos pos)
    {
        // Keep conditionals in sync with BlockTorch#isValidPosition
        if (this == Blocks.END_GATEWAY) {
            return false;
        } else if (this instanceof BlockFence || this instanceof BlockStainedGlass || this == Blocks.GLASS || this == Blocks.COBBLESTONE_WALL || this == Blocks.MOSSY_COBBLESTONE_WALL || state.isTopSolid()) {
            return true;
        } else {
            BlockFaceShape shape = state.getBlockFaceShape(world, pos, EnumFacing.UP);
            return (shape == BlockFaceShape.SOLID || shape == BlockFaceShape.CENTER || shape == BlockFaceShape.CENTER_BIG) && !Block.isExceptionBlockForAttaching(getBlock());
        }
    }

    /**
     *
     * Called when A user uses the creative pick block button on this block
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, empty itemstack if nothing should be added.
     */
    default ItemStack getPickBlock(IBlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, EntityPlayer player)
    {
        return this.getBlock().getItem(world, pos, state);
    }

    /**
     * Used by getTopSoilidOrLiquidBlock while placing biome decorations, villages, etc
     * Also used to determine if the player can spawn in this block.
     *
     * @return False to disallow spawning.
     */
    default boolean isFoliage(IBlockState state, IWorldReader world, BlockPos pos)
    {
        return false;
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
    default boolean addLandingEffects(IBlockState state1, WorldServer worldserver, BlockPos pos, IBlockState state2, EntityLivingBase entity, int numberOfParticles)
    {
        return false;
    }

   /**
    * Allows a block to override the standard vanilla running particles.
    * This is called from {@link Entity#spawnRunningParticles} and is called both,
    * Client and server side, it's up to the implementor to client check / server check.
    * By default vanilla spawns particles only on the client and the server methods no-op.
    *
    * @param state  The BlockState the entity is running on.
    * @param world  The world.
    * @param pos    The position at the entities feet.
    * @param entity The entity running on the block.
    * @return True to prevent vanilla running particles from spawning.
    */
    default boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity)
    {
        return false;
    }

    /**
     * Spawn a digging particle effect in the world, this is a wrapper
     * around EffectRenderer.addBlockHitEffects to allow the block more
     * control over the particles. Useful when you have entirely different
     * texture sheets for different sides/locations in the world.
     *
     * @param state The current state
     * @param world The current world
     * @param target The target the player is looking at {x/y/z/side/sub}
     * @param manager A reference to the current particle manager.
     * @return True to prevent vanilla digging particles form spawning.
     */
    @OnlyIn(Dist.CLIENT)
    default boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager)
    {
        return false;
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
    default boolean addDestroyEffects(IBlockState state, World world, BlockPos pos, ParticleManager manager)
    {
        return false;
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
    * @param state The Current state
    * @param world The current world
    *
    * @param facing The direction relative to the given position the plant wants to be, typically its UP
    * @param plantable The plant that wants to check
    * @return True to allow the plant to be planted/stay.
    */
    boolean canSustainPlant(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing facing, IPlantable plantable);

    /**
     * Called when a plant grows on this block, only implemented for saplings using the WorldGen*Trees classes right now.
     * Modder may implement this for custom plants.
     * This does not use ForgeDirection, because large/huge trees can be located in non-representable direction,
     * so the source location is specified.
     * Currently this just changes the block to dirt if it was grass.
     *
     * Note: This happens DURING the generation, the generation may not be complete when this is called.
     *
     * @param state The current state
     * @param world Current world
     * @param pos Block position in world
     * @param source Source plant's position in world
     */
    default void onPlantGrow(IBlockState state, IWorld world, BlockPos pos, BlockPos source)
    {
        if (this.getBlock() == Blocks.GRASS_BLOCK || this.getBlock() == Blocks.MYCELIUM || this.getBlock() == Blocks.FARMLAND || this.getBlock() == Blocks.AIR)
            world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 2);
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
    default boolean isFertile(IBlockState state, IBlockReader world, BlockPos pos)
    {
        if (this.getBlock() == Blocks.FARMLAND)
            return state.get(BlockFarmland.MOISTURE) > 0;

        return  false;
    }

   /**
    * Determines if this block can be used as the base of a beacon.
    *
    * @param world The current world
    * @param pos Block position in world
    * @param beacon Beacon position in world
    * @return True, to support the beacon, and make it active with this block.
    */
    default boolean isBeaconBase(IBlockState state, IWorldReader world, BlockPos pos, BlockPos beacon)
    {
        return this.getBlock() == Blocks.EMERALD_BLOCK || this.getBlock() == Blocks.GOLD_BLOCK ||
                this.getBlock() == Blocks.DIAMOND_BLOCK || this.getBlock() == Blocks.IRON_BLOCK;
    }
   /**
    * Gathers how much experience this block drops when broken.
    *
    * @param state The current state
    * @param world The world
    * @param pos Block position
    * @param fortune
    * @return Amount of XP from breaking this block.
    */
    default int getExpDrop(IBlockState state, IWorldReader world, BlockPos pos, int fortune)
    {
       return 0;
    }

    @SuppressWarnings("deprecation")
    default IBlockState rotate(IBlockState state, IWorld world, BlockPos pos, Rotation direction)
    {
        return state.rotate(direction);
    }

    /**
     * Get the rotations that can apply to the block at the specified coordinates. Null means no rotations are possible.
     * Note, this is up to the block to decide. It may not be accurate or representative.
     * @param state The current state
     * @param world The world
     * @param pos Block position in world
     * @return An array of valid axes to rotate around, or null for none or unknown
     */
    @Nullable
    default EnumFacing[] getValidRotations(IBlockState state, IBlockReader world, BlockPos pos)
    {
        for (IProperty<?> prop : state.getProperties())
        {
            if ((prop.getName().equals("facing") || prop.getName().equals("rotation")) && prop.getValueClass() == EnumFacing.class)
            {
                @SuppressWarnings("unchecked")
                Collection<EnumFacing> values = ((Collection<EnumFacing>)prop.getAllowedValues());
                return values.toArray(new EnumFacing[values.size()]);
            }
        }
        return null;
    }

   /**
    * Determines the amount of enchanting power this block can provide to an enchanting table.
    * @param world The World
    * @param pos Block position in world
    * @return The amount of enchanting power this block produces.
    */
    default float getEnchantPowerBonus(IBlockState state, IWorldReader world, BlockPos pos)
    {
        return this.getBlock() == Blocks.BOOKSHELF ? 1: 0;
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
    @SuppressWarnings("unchecked")
    default boolean recolorBlock(IBlockState state, IWorld world, BlockPos pos, EnumFacing facing, EnumDyeColor color)
    {
        for (IProperty<?> prop : state.getProperties())
        {
            if (prop.getName().equals("color") && prop.getValueClass() == EnumDyeColor.class)
            {
                EnumDyeColor current = (EnumDyeColor)state.get(prop);
                if (current != color && prop.getAllowedValues().contains(color))
                {
                    world.setBlockState(pos, state.with(((IProperty<EnumDyeColor>)prop), color), 3);
                    return true;
                }
            }
        }
        return false;
    }

   /**
    * Called when a tile entity on a side of this block changes is created or is destroyed.
    * @param world The world
    * @param pos Block position in world
    * @param neighbor Block position of neighbor
    */
    default void onNeighborChange(IBlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor){}

   /**
    * Called on an Observer block whenever an update for an Observer is received.
    *
    * @param observerState The Observer block's state.
    * @param world The current world.
    * @param observerPos The Observer block's position.
    * @param changedBlock The updated block.
    * @param changedBlockPos The updated block's position.
    */
    default void observedNeighborChange(IBlockState observerState, World world, BlockPos observerPos, Block changedBlock, BlockPos changedBlockPos){}

   /**
    * Called to determine whether to allow the a block to handle its own indirect power rather than using the default rules.
    * @param world The world
    * @param pos Block position in world
    * @param side The INPUT side of the block to be powered - ie the opposite of this block's output side
    * @return Whether Block#isProvidingWeakPower should be called when determining indirect power
    */
    default boolean shouldCheckWeakPower(IBlockState state, IWorldReader world, BlockPos pos, EnumFacing side)
    {
        return state.isNormalCube(world, pos);
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
    default boolean getWeakChanges(IBlockState state, IWorldReader world, BlockPos pos)
    {
        return false;
    }

    /**
     * Queries the class of tool required to harvest this block, if null is returned
     * we assume that anything can harvest this block.
     */
    ToolType getHarvestTool(IBlockState state);

    /**
     * Queries the harvest level of this item stack for the specified tool class,
     * Returns -1 if this tool is not of the specified type
     *
     * @return Harvest level, or -1 if not the specified tool type.
     */
    int getHarvestLevel(IBlockState state);

    /**
     * Checks if the specified tool type is efficient on this block,
     * meaning that it digs at full speed.
     */
    default boolean isToolEffective(IBlockState state, ToolType tool)
    {
        if (tool == ToolType.PICKAXE && (this.getBlock() == Blocks.REDSTONE_ORE || this.getBlock() == Blocks.REDSTONE_LAMP || this.getBlock() == Blocks.OBSIDIAN))
            return false;
        return tool == getHarvestTool(state);
    }

    /**
     * Can return IExtendedBlockState
     */
    default IBlockState getExtendedState(IBlockState state, IBlockReader world, BlockPos pos)
    {
        return state;
    }

    /**
     * Queries if this block should render in a given layer.
     * A custom {@link IBakedModel} can use {@link net.minecraftforge.client.MinecraftForgeClient#getRenderLayer()} to alter the model based on layer.
     */
    default boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return this.getBlock().getRenderLayer() == layer;
    }

    /**
     * Sensitive version of getSoundType
     * @param state The state
     * @param world The world
     * @param pos The position. Note that the world may not necessarily have {@code state} here!
     * @param entity The entity that is breaking/stepping on/placing/hitting/falling on this block, or null if no entity is in this context
     * @return A SoundType to use
     */
    default SoundType getSoundType(IBlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity)
    {
        return this.getBlock().getSoundType();
    }

    /**
     * @param state The state
     * @param world The world
     * @param pos The position of this state
     * @param beaconPos The position of the beacon
     * @return A float RGB [0.0, 1.0] array to be averaged with a beacon's existing beam color, or null to do nothing to the beam
     */
    @Nullable
    default float[] getBeaconColorMultiplier(IBlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos)
    {
        return null;
    }

    /**
     * Use this to change the fog color used when the entity is "inside" a material.
     * Vec3d is used here as "r/g/b" 0 - 1 values.
     *
     * @param world         The world.
     * @param pos           The position at the entity viewport.
     * @param state         The state at the entity viewport.
     * @param entity        the entity
     * @param originalColor The current fog color, You are not expected to use this, Return as the default if applicable.
     * @return The new fog color.
     */
    @OnlyIn(Dist.CLIENT)
    default Vec3d getFogColor(IBlockState state, IWorldReader world, BlockPos pos, Entity entity, Vec3d originalColor, float partialTicks)
    {
        if (state.getMaterial() == Material.WATER)
        {
            float f12 = 0.0F;

            if (entity instanceof EntityLivingBase)
            {
                EntityLivingBase ent = (EntityLivingBase)entity;
                f12 = (float) EnchantmentHelper.getRespirationModifier(ent) * 0.2F;

                if (ent.isPotionActive(MobEffects.WATER_BREATHING))
                {
                    f12 = f12 * 0.3F + 0.6F;
                }
            }
            return new Vec3d(0.02F + f12, 0.02F + f12, 0.2F + f12);
        }
        else if (state.getMaterial() == Material.LAVA)
        {
            return new Vec3d(0.6F, 0.1F, 0.0F);
        }
        return originalColor;
    }

    /**
     * Used to determine the state 'viewed' by an entity (see
     * {@link ActiveRenderInfo#getBlockStateAtEntityViewpoint(World, Entity, float)}).
     * Can be used by fluid blocks to determine if the viewpoint is within the fluid or not.
     *
     * @param state     the state
     * @param world     the world
     * @param pos       the position
     * @param viewpoint the viewpoint
     * @return the block state that should be 'seen'
     */
    default IBlockState getStateAtViewpoint(IBlockState state, IBlockReader world, BlockPos pos, Vec3d viewpoint)
    {
        return state;
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
    @SuppressWarnings("deprecation")
    default IBlockState getStateForPlacement(IBlockState state, EnumFacing facing, IBlockState state2, IWorld world, BlockPos pos1, BlockPos pos2, EnumHand hand)
    {
        return this.getBlock().updatePostPlacement(state, facing, state2, world, pos1, pos2);
    }


    /**
     * Determines if another block can connect to this block
     *
     * @param world The current world
     * @param pos The position of this block
     * @param facing The side the connecting block is on
     * @return True to allow another block to connect to this block
     */
    default boolean canBeConnectedTo(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing facing)
    {
        return false;
    }

    /**
     * Get the {@code PathNodeType} for this block. Return {@code null} for vanilla behavior.
     *
     * @return the PathNodeType
     */
    @Nullable
    default PathNodeType getAiPathNodeType(IBlockState state, IBlockReader world, BlockPos pos, @Nullable EntityLiving entity)
    {
        return state.isBurning(world, pos) ? PathNodeType.DAMAGE_FIRE : null;
    }

    /**
     * @param blockState The state for this block
     * @param world The world this block is in
     * @param pos The position of this block
     * @param side The side of this block that the chest lid is trying to open into
     * @return true if the chest should be prevented from opening by this block
     */
    default boolean doesSideBlockChestOpening(IBlockState blockState, IBlockReader world, BlockPos pos, EnumFacing side)
    {
        ResourceLocation registryName = this.getBlock().getRegistryName();
        if (registryName != null && "minecraft".equals(registryName.getNamespace()))
        {
            // maintain the vanilla behavior of https://bugs.mojang.com/browse/MC-378
            return blockState.isNormalCube(world, pos);
        }
        return blockState.isTopSolid();
    }

    /**
     * @param state The state
     * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
     */
    default boolean isStickyBlock(IBlockState state)
    {
        return state.getBlock() == Blocks.SLIME_BLOCK;
    }

    /**
     * This gets a complete list of items dropped from this block.
     *
     * @param state Current state
     * @param drops add all items this block drops to this drops list
     * @param world The current world, Currently hard 'World' and not 'IBlockReder' because vanilla needs it.
     * @param pos Block position in world
     * @param fortune Breakers fortune level
     */
    default void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune)
    {
        int count = getBlock().getItemsToDropCount(state, fortune, world, pos, world.rand);
        for (int i = 0; i < count; i++)
        {
           Item item = getBlock().getItemDropped(state, world, pos, fortune).asItem();
           if (item != Items.AIR)
              drops.add(new ItemStack(item));
        }
     }

    /**
     * Chance that fire will spread and consume this block.
     * 300 being a 100% chance, 0, being a 0% chance.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param face The face that the fire is coming from
     * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
     */
    default int getFlammability(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing face)
    {
        return ((BlockFire)Blocks.FIRE).getFlammability(getBlock());
    }

    /**
     * Called when fire is updating, checks if a block face can catch fire.
     *
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param face The face that the fire is coming from
     * @return True if the face can be on fire, false otherwise.
     */
    default boolean isFlammable(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing face)
    {
        return state.getFlammability(world, pos, face) > 0;
    }

    /**
     * Called when fire is updating on a neighbor block.
     * The higher the number returned, the faster fire will spread around this block.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param face The face that the fire is coming from
     * @return A number that is used to determine the speed of fire growth around the block
     */
    default int getFireSpreadSpeed(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing face)
    {
        return ((BlockFire)Blocks.FIRE).getEncouragement(getBlock());
    }

    /**
     * Currently only called by fire when it is on top of this block.
     * Returning true will prevent the fire from naturally dying during updating.
     * Also prevents firing from dying from rain.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param side The face that the fire is coming from
     * @return True if this block sustains fire, meaning it will never go out.
     */
    default boolean isFireSource(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing side)
    {
        if (side != EnumFacing.UP)
            return false;
        if (getBlock() == Blocks.NETHERRACK || getBlock() == Blocks.MAGMA_BLOCK)
            return true;
        if (world instanceof World && ((World)world).dimension instanceof EndDimension && getBlock() == Blocks.BEDROCK)
            return true;
        return false;
    }

    /**
     * Determines if this block is can be destroyed by the specified entities normal behavior.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @return True to allow the ender dragon to destroy this block
     */
    default boolean canEntityDestroy(IBlockState state, IBlockReader world, BlockPos pos, Entity entity)
    {
        if (entity instanceof EntityDragon)
        {
            return this != Blocks.BARRIER &&
                   this != Blocks.OBSIDIAN &&
                   this != Blocks.END_STONE &&
                   this != Blocks.BEDROCK &&
                   this != Blocks.END_PORTAL &&
                   this != Blocks.END_PORTAL_FRAME &&
                   this != Blocks.COMMAND_BLOCK &&
                   this != Blocks.REPEATING_COMMAND_BLOCK &&
                   this != Blocks.CHAIN_COMMAND_BLOCK &&
                   this != Blocks.IRON_BARS &&
                   this != Blocks.END_GATEWAY;
        }
        else if ((entity instanceof EntityWither) ||
                 (entity instanceof EntityWitherSkull))
        {
            return EntityWither.canDestroyBlock(getBlock());
        }

        return true;
    }

    /**
     * Determines if the top is consider 'solid'. This is a helper for getBlockFaceShape(UP) == SOLID.
     * Sadly some vanilla logic doesn't sync this value, so we have to have this special function.
     *
     * @param world The world
     * @param pos Block position in world
     * @return True if the top is considered solid
     */
     default boolean isTopSolid(IBlockState state, IWorldReader world, BlockPos pos)
     {
         return state.isTopSolid();
     }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param start The start vector
     * @param end The end vector
     * @param original The original result from {@link Block#collisionRayTrace(IBlockState, World, BlockPos, Vec3d, Vec3d)}
     * @return A result that suits your block
     */
    @Nullable
    default RayTraceResult getRayTraceResult(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end, RayTraceResult original)
    {
        return original;
    }
}
