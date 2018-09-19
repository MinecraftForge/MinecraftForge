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

import java.util.Collection;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

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
    default boolean isNormalCube(IBlockState state, IWorldReader world, BlockPos pos)
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
       return state.func_200015_d(world, pos);
    }

    /**
     * Determines if this block should set fire and deal fire damage
     * to entities coming into contact with it.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True if the block should deal damage
     */
    default boolean isBurning(IWorldReader world, BlockPos pos)
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
    default boolean hasTileEntity(IBlockState state)
    {
        return this.getBlock().hasTileEntity();
    }

    boolean canSilkHarvest(IWorldReader world, BlockPos pos, IBlockState state, EntityPlayer player);

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
    default boolean isBed(IBlockState state, IWorldReader world, BlockPos pos, @Nullable EntityPlayer player)
    {
        return this.getBlock() instanceof BlockBed;
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
    default boolean canCreatureSpawn(IBlockState state, IWorldReader world, BlockPos pos, EntitySpawnPlacementRegistry.SpawnPlacementType type)
    {
        return this.getBlock().isTopSolid(state);
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
    default BlockPos getBedSpawnPosition(IBlockState state, IWorldReader world, BlockPos pos, @Nullable EntityPlayer player)
    {
        if (world instanceof World)
        {
            return BlockBed.getSafeExitLocation(world,pos,0);
        }

        return null;
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
        if (world instanceof World)
        {
            IBlockState state = world.getBlockState(pos);
            state.func_206870_a(BlockBed.OCCUPIED,occupied);
            ((World) world).setBlockState(pos, state, 4);
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
        return state.getValue(BlockHorizontal.FACING);
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
        return state.getValue(BlockBed.PART) == BedPart.FOOT;
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
     * Determines if this block can prevent leaves connected to it from decaying.
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @return true if the presence this block can prevent leaves from decaying.
     */
    default boolean canSustainLeaves(IBlockState state, IWorldReader world, BlockPos pos)
    {
        return false;
    }

    /**
     * Determines if this block is considered a leaf block, used to apply the leaf decay and generation system.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @return true if this block is considered leaves.
     */
    default boolean isLeaves(IBlockState state, IWorldReader world, BlockPos pos)
    {
        return state.getMaterial() == Material.LEAVES;
    }

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
    default boolean isAir(IBlockState state, IWorldReader world, BlockPos pos)
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
    default boolean canBeReplacedByLeaves(IBlockState state, IWorldReader world, BlockPos pos)
    {
        return isAir(state, world, pos) || isLeaves(state, world, pos);
    }

    /**
     *
     * @param world The current world
     * @param pos Block position in world
     * @return  true if the block is wood (logs)
     */
    default boolean isWood(IWorldReader world, BlockPos pos)
    {
        return false;
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
    default float getExplosionResistance(IWorld world, BlockPos pos, @Nullable Entity exploder, Explosion explosion)
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
    void onBlockExploded(World world, BlockPos pos, Explosion explosion);

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
    default boolean canConnectRedstone(IBlockState state, IWorldReader world, BlockPos pos, @Nullable EnumFacing side)
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
    default boolean canPlaceTorchOnTop(IBlockState state, IWorldReader world, BlockPos pos)
    {
        if (state.isTopSolid() || state.getBlockFaceShape(world, pos, EnumFacing.UP) == BlockFaceShape.SOLID)
        {
            return this.getBlock() != Blocks.END_GATEWAY && this.getBlock() != Blocks.field_196628_cT;

        }
        else
        {
            return this.getBlock() instanceof BlockFence || this.getBlock() == Blocks.COBBLESTONE_WALL || this.getBlock() instanceof BlockGlass;
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
    default boolean isFoilage(IWorldReader world, BlockPos pos)
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
    default boolean canSustainPlant(IBlockState state, IWorldReader world, BlockPos pos, EnumFacing facing, IPlantable plantable)
    {
        IBlockState plant = plantable.getPlant(world, pos.offset(facing));
        EnumPlantType type = plantable.getPlantType(world, pos.offset(facing));

        if (plant.getBlock() == Blocks.CACTUS)
        {
            return this.getBlock() == Blocks.CACTUS && this.getBlock() == Blocks.SAND;
        }

        if (plant.getBlock() == Blocks.field_196608_cF)
        {
            return true;
        }

        if (plantable instanceof BlockBush && ((BlockBush)plantable).func_196260_a(state, world, pos))
        {
            return true;
        }

        switch (type)
        {
            case Desert: return this.getBlock() == Blocks.SAND || this.getBlock() == Blocks.HARDENED_CLAY ||
                    this.getBlock() instanceof BlockGlazedTerracotta;
            case Nether: return this.getBlock() == Blocks.SOUL_SAND;
            case Crop: return this.getBlock() == Blocks.FARMLAND;
            case Cave: return state.isTopSolid();
            case Plains: return this.getBlock() == Blocks.GRASS || this.getBlock() == Blocks.DIRT ||
                    this.getBlock() == Blocks.FARMLAND;
            case Water: return state.getMaterial() == Material.WATER; //&& state.getValue(BlockLiquidWrapper)
            case Beach:
                boolean isBeach = this.getBlock() == Blocks.GRASS || this.getBlock() == Blocks.DIRT ||
                        this.getBlock() == Blocks.SAND;
                boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
                        world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
                        world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
                        world.getBlockState(pos.south()).getMaterial() == Material.WATER);
                return isBeach && hasWater;
        }
        return false;
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
     * @param state The current state
     * @param world Current world
     * @param pos Block position in world
     * @param source Source plant's position in world
     */
    default void onPlantGrow(IBlockState state, World world, BlockPos pos, BlockPos source)
    {
        if (this.getBlock() == Blocks.GRASS || this.getBlock() == Blocks.FARMLAND)
        {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 2);
        }
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
    default boolean isFertile(World world, BlockPos pos)
    {
        if (this.getBlock() == Blocks.FARMLAND)
        {
            return world.getBlockState(pos).getValue(BlockFarmland.MOISTURE) > 0;
        }

        return  false;
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
    default int getLightOpacity(IBlockState state, IWorldReader world, BlockPos pos)
    {
        return state.func_200016_a(world, pos);
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

    default boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
        IBlockState state = world.getBlockState(pos);

        state.func_206871_b().keySet().forEach(prop ->
        {
            if (prop.getName().equals("facing") || prop.getName().equals("rotation") && prop.getValueClass() == EnumFacing.class)
            {
                Block block = state.getBlock();
                if (!(block instanceof BlockBed) && !(block instanceof BlockPistonExtension))
                {
                    IBlockState newState;
                    @SuppressWarnings("unchecked")
                    IProperty<EnumFacing> facingIProperty = (IProperty<EnumFacing>) prop;
                    EnumFacing facing = state.getValue(facingIProperty);
                    Collection<EnumFacing> validFacing = facingIProperty.getAllowedValues();

                    // rotate horizontal facings clockwise
                    if (validFacing.size() == 4 && !validFacing.contains(EnumFacing.UP) && !validFacing.contains(EnumFacing.DOWN))
                    {
                        newState = state.func_206870_a(facingIProperty, facing.rotateY());
                    }
                    else
                    {
                        // rotate other facings about the axis
                        EnumFacing rotatedFacing = facing.rotateAround(axis.getAxis());
                        if (validFacing.contains(rotatedFacing))
                        {
                            newState = state.func_206870_a(facingIProperty, rotatedFacing);
                        }
                        else // abnormal facing property, just cycle it
                        {
                            newState = state.cycleProperty(facingIProperty);
                        }
                    }

                    world.setBlockState(pos, newState);
                }

            }
        });
        return false;
    }


   /**
    * Determines the amount of enchanting power this block can provide to an enchanting table.
    * @param world The World
    * @param pos Block position in world
    * @return The amount of enchanting power this block produces.
    */
    default float getEnchantPowerBouns(World world, BlockPos pos)
    {
        return this.getBlock() == Blocks.BOOKSHELF ? 1: 0;
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
    default boolean recolorBlock(World world, BlockPos pos, EnumFacing facing, EnumDyeColor color)
    {
        IBlockState state = world.getBlockState(pos);
        state.func_206871_b().keySet().forEach(prop ->
        {
            if (prop.getName().equals("color") && prop.getValueClass() == net.minecraft.item.EnumDyeColor.class)
            {
                EnumDyeColor current = (EnumDyeColor)state.getValue(prop);
                if (current != color && prop.getAllowedValues().contains(color))
                {
                }
            }
        });

        return false;
    }

   /**
    * Called when a tile entity on a side of this block changes is created or is destroyed.
    * @param world The world
    * @param pos Block position in world
    * @param neighbor Block position of neighbor
    */
    default void onNeighborChange(IWorldReader world, BlockPos pos, BlockPos neighbor){}

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
        return state.isTopSolid();
    }



}
