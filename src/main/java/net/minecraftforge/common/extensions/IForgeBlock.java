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
import java.util.Set;
import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;

import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

@SuppressWarnings("deprecation")
public interface IForgeBlock
{
    private Block self()
    {
        return (Block) this;
    }

    /**
     * Gets the slipperiness at the given location at the given state. Normally
     * between 0 and 1.
     * <p>
     * Note that entities may reduce slipperiness by a certain factor of their own;
     * for {@link net.minecraft.world.entity.LivingEntity}, this is {@code .91}.
     * {@link net.minecraft.world.entity.item.ItemEntity} uses {@code .98}, and
     * {@link net.minecraft.world.entity.projectile.FishingHook} uses {@code .92}.
     *
     * @param state state of the block
     * @param world the world
     * @param pos the position in the world
     * @param entity the entity in question
     * @return the factor by which the entity's motion should be multiplied
     */
    default float getFriction(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity)
    {
        return self().getFriction();
    }

    /**
     * Get a light value for this block, taking into account the given state and coordinates, normal ranges are between 0 and 15
     *
     * @param state
     * @param world
     * @param pos
     * @return The light value
     */
    default int getLightEmission(BlockState state, BlockGetter world, BlockPos pos)
    {
        return state.getLightEmission();
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
    default boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity)
    {
        return state.is(BlockTags.CLIMBABLE);
    }

    /**
     * Checks if this block makes an open trapdoor above it climbable.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param trapdoorState The current state of the open trapdoor above
     * @return True if the block should act like a ladder
     */
    default boolean makesOpenTrapdoorAboveClimbable(BlockState state, LevelReader world, BlockPos pos, BlockState trapdoorState)
    {
        return state.getBlock() instanceof LadderBlock && state.getValue(LadderBlock.FACING) == trapdoorState.getValue(TrapDoorBlock.FACING);
    }

    /**
     * Determines if this block should set fire and deal fire damage
     * to entities coming into contact with it.
     *
     * @param world The current world
     * @param pos Block position in world
     * @return True if the block should deal damage
     */
    default boolean isBurning(BlockState state, BlockGetter world, BlockPos pos)
    {
        return this == Blocks.FIRE || this == Blocks.LAVA;
    }

    /**
     * Determines if the player can harvest this block, obtaining it's drops when the block is destroyed.
     *
     * @param world The current world
     * @param pos The block's current position
     * @param player The player damaging the block
     * @return True to spawn the drops
     */
    default public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player)
    {
        return ForgeHooks.isCorrectToolForDrops(state, player);
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
    default boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        self().playerWillDestroy(world, pos, state, player);
        return world.setBlock(pos, fluid.createLegacyBlock(), world.isClientSide ? 11 : 3);
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
    default boolean isBed(BlockState state, BlockGetter world, BlockPos pos, @Nullable Entity player)
    {
        return self() instanceof BedBlock; //TODO: Forge: Keep isBed function?
    }

    /**
     * Returns the position that the entity is moved to upon
     * respawning at this block.
     *
     * @param state The current state
     * @param type The entity type used when checking if a dismount blockstate is dangerous. Currently always PLAYER.
     * @param world The current world
     * @param pos Block position in world
     * @param orientation The angle the entity had when setting the respawn point
     * @param entity The entity respawning, often null
     * @return The spawn position or the empty optional if respawning here is not possible
     */
    default Optional<Vec3> getRespawnPosition(BlockState state, EntityType<?> type, LevelReader world, BlockPos pos, float orientation, @Nullable LivingEntity entity)
    {
        if (isBed(state, world, pos, entity) && world instanceof Level level && BedBlock.canSetSpawn(level))
        {
            return BedBlock.findStandUpPosition(type, world, pos, orientation);
        }
        return Optional.empty();
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
    default boolean canCreatureSpawn(BlockState state, BlockGetter world, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType)
    {
        return state.isValidSpawn(world, pos, entityType);
    }

    /**
     * Called when a user either starts or stops sleeping in the bed.
     *
     * @param state
     * @param world The current world
     * @param pos Block position in world
     * @param sleeper The sleeper or camera entity, null in some cases.
     * @param occupied True if we are occupying the bed, or false if they are stopping use of the bed
     */
    default void setBedOccupied(BlockState state, Level world, BlockPos pos, LivingEntity sleeper, boolean occupied)
    {
        world.setBlock(pos, state.setValue(BedBlock.OCCUPIED, occupied), 3);
    }

   /**
    * Returns the direction of the block. Same values that
    * are returned by BlockDirectional. Called every frame tick for every living entity. Be VERY fast.
    *
    * @param state The current state
    * @param world The current world
    * @param pos Block position in world
    * @return Bed direction
    */
    default Direction getBedDirection(BlockState state, LevelReader world, BlockPos pos)
    {
        return state.getValue(HorizontalDirectionalBlock.FACING);
    }

    /**
     * Location sensitive version of getExplosionResistance
     *
     * @param world The current world
     * @param pos Block position in world
     * @param explosion The explosion
     * @return The amount of the explosion absorbed.
     */
    default float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion)
    {
        return self().getExplosionResistance();
    }

    /**
     *
     * Called when A user uses the creative pick block button on this block
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, empty itemstack if nothing should be added.
     */
    default ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player)
    {
        return self().getCloneItemStack(world, pos, state);
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
    default boolean addLandingEffects(BlockState state1, ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles)
    {
        return false;
    }

   /**
    * Allows a block to override the standard vanilla running particles.
    * This is called from Entity.spawnSprintParticle and is called both,
    * Client and server side, it's up to the implementor to client check / server check.
    * By default vanilla spawns particles only on the client and the server methods no-op.
    *
    * @param state  The BlockState the entity is running on.
    * @param world  The world.
    * @param pos    The position at the entities feet.
    * @param entity The entity running on the block.
    * @return True to prevent vanilla running particles from spawning.
    */
    default boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity)
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
    boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable);

   /**
    * Checks if this soil is fertile, typically this means that growth rates
    * of plants on this soil will be slightly sped up.
    * Only vanilla case is tilledField when it is within range of water.
    *
    * @param world The current world
    * @param pos Block position in world
    * @return True if the soil should be considered fertile.
    */
    default boolean isFertile(BlockState state, BlockGetter world, BlockPos pos)
    {
        if (state.is(Blocks.FARMLAND))
            return state.getValue(FarmBlock.MOISTURE) > 0;

        return  false;
    }

    /**
     * Determines if this block can be used as the frame of a conduit.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param conduit Conduit position in world
     * @return True, to support the conduit, and make it active with this block.
     */
    default boolean isConduitFrame(BlockState state, LevelReader world, BlockPos pos, BlockPos conduit)
    {
        return  state.getBlock() == Blocks.PRISMARINE ||
                state.getBlock() == Blocks.PRISMARINE_BRICKS ||
                state.getBlock() == Blocks.SEA_LANTERN ||
                state.getBlock() == Blocks.DARK_PRISMARINE;
    }

    /**
     * Determines if this block can be used as part of a frame of a nether portal.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @return True, to support being part of a nether portal frame, false otherwise.
     */
    default boolean isPortalFrame(BlockState state, BlockGetter world, BlockPos pos)
    {
        return state.is(Blocks.OBSIDIAN);
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
    default int getExpDrop(BlockState state, LevelReader world, BlockPos pos, int fortune, int silktouch)
    {
       return 0;
    }

    default BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction)
    {
        return state.rotate(direction);
    }

   /**
    * Determines the amount of enchanting power this block can provide to an enchanting table.
    * @param world The World
    * @param pos Block position in world
    * @return The amount of enchanting power this block produces.
    */
    default float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos)
    {
        return state.is(Blocks.BOOKSHELF) ? 1: 0;
    }

   /**
    * Called when a tile entity on a side of this block changes is created or is destroyed.
    * @param world The world
    * @param pos Block position in world
    * @param neighbor Block position of neighbor
    */
    default void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor){}

   /**
    * Called to determine whether to allow the a block to handle its own indirect power rather than using the default rules.
    * @param world The world
    * @param pos Block position in world
    * @param side The INPUT side of the block to be powered - ie the opposite of this block's output side
    * @return Whether Block#isProvidingWeakPower should be called when determining indirect power
    */
    default boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side)
    {
        return state.isRedstoneConductor(world, pos);
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
    default boolean getWeakChanges(BlockState state, LevelReader world, BlockPos pos)
    {
        return false;
    }

    /**
     * Sensitive version of getSoundType
     * @param state The state
     * @param world The world
     * @param pos The position. Note that the world may not necessarily have {@code state} here!
     * @param entity The entity that is breaking/stepping on/placing/hitting/falling on this block, or null if no entity is in this context
     * @return A SoundType to use
     */
    default SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity)
    {
        return self().getSoundType(state);
    }

    /**
     * @param state The state
     * @param world The world
     * @param pos The position of this state
     * @param beaconPos The position of the beacon
     * @return A float RGB [0.0, 1.0] array to be averaged with a beacon's existing beam color, or null to do nothing to the beam
     */
    @Nullable
    default float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos)
    {
        if (self() instanceof BeaconBeamBlock)
            return ((BeaconBeamBlock) self()).getColor().getTextureDiffuseColors();
        return null;
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
    default BlockState getStateAtViewpoint(BlockState state, BlockGetter world, BlockPos pos, Vec3 viewpoint)
    {
        return state;
    }

    /**
     * Get the {@code PathNodeType} for this block. Return {@code null} for vanilla behavior.
     *
     * @return the PathNodeType
     */
    @Nullable
    default BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity)
    {
        return state.getBlock() == Blocks.LAVA ? BlockPathTypes.LAVA : state.isBurning(world, pos) ? BlockPathTypes.DAMAGE_FIRE : null;
    }

    /**
     * @param state The state
     * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
     */
    default boolean isSlimeBlock(BlockState state)
    {
        return state.getBlock() == Blocks.SLIME_BLOCK;
    }

    /**
     * @param state The state
     * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
     */
    default boolean isStickyBlock(BlockState state)
    {
        return state.getBlock() == Blocks.SLIME_BLOCK || state.getBlock() == Blocks.HONEY_BLOCK;
    }

    /**
     * Determines if this block can stick to another block when pushed by a piston.
     * @param state My state
     * @param other Other block
     * @return True to link blocks
     */
    default boolean canStickTo(BlockState state, BlockState other)
    {
        if (state.getBlock() == Blocks.HONEY_BLOCK && other.getBlock() == Blocks.SLIME_BLOCK) return false;
        if (state.getBlock() == Blocks.SLIME_BLOCK && other.getBlock() == Blocks.HONEY_BLOCK) return false;
        return state.isStickyBlock() || other.isStickyBlock();
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
    default int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return ((FireBlock)Blocks.FIRE).getBurnOdd(state);
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
    default boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return state.getFlammability(world, pos, face) > 0;
    }

    /**
     * If the block is flammable, this is called when it gets lit on fire.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @param face The face that the fire is coming from
     * @param igniter The entity that lit the fire
     */
    default void catchFire(BlockState state, Level world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {}

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
    default int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return ((FireBlock)Blocks.FIRE).getFlameOdds(state);
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
    default boolean isFireSource(BlockState state, LevelReader world, BlockPos pos, Direction side)
    {
        return state.is(world.dimensionType().infiniburn());
    }

    /**
     * Determines if this block is can be destroyed by the specified entities normal behavior.
     *
     * @param state The current state
     * @param world The current world
     * @param pos Block position in world
     * @return True to allow the ender dragon to destroy this block
     */
    default boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity)
    {
        if (entity instanceof EnderDragon)
        {
            return !BlockTags.DRAGON_IMMUNE.contains(this.self());
        }
        else if ((entity instanceof WitherBoss) ||
                 (entity instanceof WitherSkull))
        {
            return state.isAir() || WitherBoss.canDestroy(state);
        }

        return true;
    }

    /**
     * Determines if this block should drop loot when exploded.
     */
    default boolean canDropFromExplosion(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion)
    {
        return state.getBlock().dropFromExplosion(explosion);
    }

    /**
     * Retrieves a list of tags names this is known to be associated with.
     * This should be used in favor of TagCollection.getOwningTags, as this caches the result and automatically updates when the TagCollection changes.
     */
    Set<ResourceLocation> getTags();

    /**
     * Called when the block is destroyed by an explosion.
     * Useful for allowing the block to take into account tile entities,
     * state, etc. when exploded, before it is removed.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param explosion The explosion instance affecting the block
     */
    default void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion)
    {
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        self().wasExploded(world, pos, explosion);
    }

    /**
     * Determines if this block's collision box should be treated as though it can extend above its block space.
     * Use this to replicate fence and wall behavior.
     */
    default boolean collisionExtendsVertically(BlockState state, BlockGetter world, BlockPos pos, Entity collidingEntity)
    {
        return state.is(BlockTags.FENCES) || state.is(BlockTags.WALLS) || self() instanceof FenceGateBlock;
    }

    /**
     * Called to determine whether this block should use the fluid overlay texture or flowing texture when it is placed under the fluid.
     *
     * @param state The current state
     * @param world The world
     * @param pos Block position in world
     * @param fluidState The state of the fluid
     * @return Whether the fluid overlay texture should be used
     */
    default boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState)
    {
        return state.getBlock() instanceof HalfTransparentBlock || state.getBlock() instanceof LeavesBlock;
    }

    /**
     * Returns the state that this block should transform into when right clicked by a tool.
     * For example: Used to determine if an axe can strip, a shovel can path, or a hoe can till.
     * Return null if vanilla behavior should be disabled.
     *
     * @param state The current state
     * @param world The world
     * @param pos The block position in world
     * @param player The player clicking the block
     * @param stack The stack being used by the player
     * @param toolAction The action being performed by the tool
     * @return The resulting state after the action has been performed
     */
    @Nullable
    default BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolAction toolAction)
    {
        if (!stack.canPerformAction(toolAction)) return null;
        if (ToolActions.AXE_STRIP.equals(toolAction)) return AxeItem.getAxeStrippingState(state);
        else if(ToolActions.AXE_SCRAPE.equals(toolAction)) return WeatheringCopper.getPrevious(state).orElse(null);
        else if(ToolActions.AXE_WAX_OFF.equals(toolAction)) return Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock())).map((p_150694_) -> {
            return p_150694_.withPropertiesOf(state);
        }).orElse(null);
        //else if(ToolActions.HOE_TILL.equals(toolAction)) return HoeItem.getHoeTillingState(state); //TODO HoeItem bork
        else if (ToolActions.SHOVEL_FLATTEN.equals(toolAction)) return ShovelItem.getShovelPathingState(state);
        return null;
    }

    /**
     * Checks if a player or entity handles movement on this block like scaffolding.
     *
     * @param state The current state
     * @param world The current world
     * @param pos The block position in world
     * @param entity The entity on the scaffolding
     * @return True if the block should act like scaffolding
     */
    default boolean isScaffolding(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity)
    {
        return state.is(Blocks.SCAFFOLDING);
    }
}
