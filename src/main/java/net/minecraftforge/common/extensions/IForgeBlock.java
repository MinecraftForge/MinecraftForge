/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
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
import net.minecraftforge.client.ForgeHooksClient;
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
import net.minecraftforge.fml.loading.FMLEnvironment;

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
     * for {@link LivingEntity}, this is {@code .91}.
     * {@link ItemEntity} uses {@code .98}, and
     * {@link FishingHook} uses {@code .92}.
     *
     * @param state state of the block
     * @param level the level
     * @param pos the position in the level
     * @param entity the entity in question
     * @return the factor by which the entity's motion should be multiplied
     */
    default float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity)
    {
        return self().getFriction();
    }

    /**
     * Get a light value for this block, taking into account the given state and coordinates, normal ranges are between 0 and 15
     *
     * @return The light value
     */
    default int getLightEmission(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getLightEmission();
    }

    /**
     * Checks if a player or entity can use this block to 'climb' like a ladder.
     *
     * @param state The current state
     * @param level The current level
     * @param pos Block position in level
     * @param entity The entity trying to use the ladder, CAN be null.
     * @return True if the block should act like a ladder
     */
    default boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity)
    {
        return state.is(BlockTags.CLIMBABLE);
    }

    /**
     * Checks if this block makes an open trapdoor above it climbable.
     *
     * @param state The current state
     * @param level The current level
     * @param pos Block position in level
     * @param trapdoorState The current state of the open trapdoor above
     * @return True if the block should act like a ladder
     */
    default boolean makesOpenTrapdoorAboveClimbable(BlockState state, LevelReader level, BlockPos pos, BlockState trapdoorState)
    {
        return state.getBlock() instanceof LadderBlock && state.getValue(LadderBlock.FACING) == trapdoorState.getValue(TrapDoorBlock.FACING);
    }

    /**
     * Determines if this block should set fire and deal fire damage
     * to entities coming into contact with it.
     *
     * @param level The current level
     * @param pos Block position in level
     * @return True if the block should deal damage
     */
    default boolean isBurning(BlockState state, BlockGetter level, BlockPos pos)
    {
        return this == Blocks.FIRE || this == Blocks.LAVA;
    }

    /**
     * Determines if the player can harvest this block, obtaining it's drops when the block is destroyed.
     *
     * @param level The current level
     * @param pos The block's current position
     * @param player The player damaging the block
     * @return True to spawn the drops
     */
    default public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player)
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
     * @param level The current level
     * @param player The player damaging the block, may be null
     * @param pos Block position in level
     * @param willHarvest True if Block.harvestBlock will be called after this, if the return in true.
     *        Can be useful to delay the destruction of tile entities till after harvestBlock
     * @param fluid The current fluid state at current position
     * @return True if the block is actually destroyed.
     */
    default boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        self().playerWillDestroy(level, pos, state, player);
        return level.setBlock(pos, fluid.createLegacyBlock(), level.isClientSide ? 11 : 3);
    }

    /**
     * Determines if this block is classified as a Bed, Allowing
     * players to sleep in it, though the block has to specifically
     * perform the sleeping functionality in it's activated event.
     *
     * @param state The current state
     * @param level The current level
     * @param pos Block position in level
     * @param player The player or camera entity, null in some cases.
     * @return True to treat this as a bed
     */
    default boolean isBed(BlockState state, BlockGetter level, BlockPos pos, @Nullable Entity player)
    {
        return self() instanceof BedBlock; //TODO: Forge: Keep isBed function?
    }

    /**
     * Returns the position that the entity is moved to upon
     * respawning at this block.
     *
     * @param state The current state
     * @param type The entity type used when checking if a dismount blockstate is dangerous. Currently always PLAYER.
     * @param levelReader The current level
     * @param pos Block position in level
     * @param orientation The angle the entity had when setting the respawn point
     * @param entity The entity respawning, often null
     * @return The spawn position or the empty optional if respawning here is not possible
     */
    default Optional<Vec3> getRespawnPosition(BlockState state, EntityType<?> type, LevelReader levelReader, BlockPos pos, float orientation, @Nullable LivingEntity entity)
    {
        if (isBed(state, levelReader, pos, entity) && levelReader instanceof Level level && BedBlock.canSetSpawn(level))
        {
            return BedBlock.findStandUpPosition(type, levelReader, pos, orientation);
        }
        return Optional.empty();
    }

    /**
     * Determines if a specified mob type can spawn on this block, returning false will
     * prevent any mob from spawning on the block.
     *
     * @param state The current state
     * @param level The current level
     * @param pos Block position in level
     * @param type The Mob Category Type
     * @return True to allow a mob of the specified category to spawn, false to prevent it.
     */
    default boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType)
    {
        return state.isValidSpawn(level, pos, entityType);
    }

    /**
     * Called when a user either starts or stops sleeping in the bed.
     *
     * @param level The current level
     * @param pos Block position in level
     * @param sleeper The sleeper or camera entity, null in some cases.
     * @param occupied True if we are occupying the bed, or false if they are stopping use of the bed
     */
    default void setBedOccupied(BlockState state, Level level, BlockPos pos, LivingEntity sleeper, boolean occupied)
    {
        level.setBlock(pos, state.setValue(BedBlock.OCCUPIED, occupied), 3);
    }

   /**
    * Returns the direction of the block. Same values that
    * are returned by BlockDirectional. Called every frame tick for every living entity. Be VERY fast.
    *
    * @param state The current state
    * @param level The current level
    * @param pos Block position in level
    * @return Bed direction
    */
    default Direction getBedDirection(BlockState state, LevelReader level, BlockPos pos)
    {
        return state.getValue(HorizontalDirectionalBlock.FACING);
    }

    /**
     * Location sensitive version of getExplosionResistance
     *
     * @param level The current level
     * @param pos Block position in level
     * @param explosion The explosion
     * @return The amount of the explosion absorbed.
     */
    default float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion)
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
    default ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return self().getCloneItemStack(level, pos, state);
    }

    /**
     *  Allows a block to override the standard EntityLivingBase.updateFallState
     *  particles, this is a server side method that spawns particles with
     *  WorldServer.spawnParticle.
     *
     * @param level The current server level
     * @param pos The position of the block.
     * @param state2 The state at the specific level/pos
     * @param entity The entity that hit landed on the block
     * @param numberOfParticles That vanilla level have spawned
     * @return True to prevent vanilla landing particles from spawning
     */
    default boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles)
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
    * @param level  The level.
    * @param pos    The position at the entities feet.
    * @param entity The entity running on the block.
    * @return True to prevent vanilla running particles from spawning.
    */
    default boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity)
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
    * @param level The current level
    *
    * @param facing The direction relative to the given position the plant wants to be, typically its UP
    * @param plantable The plant that wants to check
    * @return True to allow the plant to be planted/stay.
    */
    boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable);

   /**
    * Checks if this soil is fertile, typically this means that growth rates
    * of plants on this soil will be slightly sped up.
    * Only vanilla case is tilledField when it is within range of water.
    *
    * @param level The current level
    * @param pos Block position in level
    * @return True if the soil should be considered fertile.
    */
    default boolean isFertile(BlockState state, BlockGetter level, BlockPos pos)
    {
        if (state.is(Blocks.FARMLAND))
            return state.getValue(FarmBlock.MOISTURE) > 0;

        return  false;
    }

    /**
     * Determines if this block can be used as the frame of a conduit.
     *
     * @param level The current level
     * @param pos Block position in level
     * @param conduit Conduit position in level
     * @return True, to support the conduit, and make it active with this block.
     */
    default boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit)
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
     * @param level The current level
     * @param pos Block position in level
     * @return True, to support being part of a nether portal frame, false otherwise.
     */
    default boolean isPortalFrame(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.is(Blocks.OBSIDIAN);
    }

   /**
    * Gathers how much experience this block drops when broken.
    *
    * @param state The current state
    * @param level The level
    * @param pos Block position
    * @param fortuneLevel fortune enchantment level of tool being used
    * @param silkTouchLevel silk touch enchantment level of tool being used
    * @return Amount of XP from breaking this block.
    */
    default int getExpDrop(BlockState state, LevelReader level, BlockPos pos, int fortuneLevel, int silkTouchLevel)
    {
       return 0;
    }

    default BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction)
    {
        return state.rotate(direction);
    }

   /**
    * Determines the amount of enchanting power this block can provide to an enchanting table.
    * @param level The level
    * @param pos Block position in level
    * @return The amount of enchanting power this block produces.
    */
    default float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos)
    {
        return state.is(Blocks.BOOKSHELF) ? 1: 0;
    }

   /**
    * Called when a tile entity on a side of this block changes is created or is destroyed.
    * @param level The level
    * @param pos Block position in level
    * @param neighbor Block position of neighbor
    */
    default void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor){}

   /**
    * Called to determine whether to allow the a block to handle its own indirect power rather than using the default rules.
    * @param level The level
    * @param pos Block position in level
    * @param side The INPUT side of the block to be powered - ie the opposite of this block's output side
    * @return Whether Block#isProvidingWeakPower should be called when determining indirect power
    */
    default boolean shouldCheckWeakPower(BlockState state, LevelReader level, BlockPos pos, Direction side)
    {
        return state.isRedstoneConductor(level, pos);
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
    default boolean getWeakChanges(BlockState state, LevelReader level, BlockPos pos)
    {
        return false;
    }

    /**
     * Sensitive version of getSoundType
     * @param state The state
     * @param level The level
     * @param pos The position. Note that the level may not necessarily have {@code state} here!
     * @param entity The entity that is breaking/stepping on/placing/hitting/falling on this block, or null if no entity is in this context
     * @return A SoundType to use
     */
    default SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity)
    {
        return self().getSoundType(state);
    }

    /**
     * @param state The state
     * @param level The level
     * @param pos The position of this state
     * @param beaconPos The position of the beacon
     * @return A float RGB [0.0, 1.0] array to be averaged with a beacon's existing beam color, or null to do nothing to the beam
     */
    @Nullable
    default float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        if (self() instanceof BeaconBeamBlock)
            return ((BeaconBeamBlock) self()).getColor().getTextureDiffuseColors();
        return null;
    }

    /**
     * Used to determine the state 'viewed' by an entity (see
     * {@link Camera#getBlockAtCamera()}).
     * Can be used by fluid blocks to determine if the viewpoint is within the fluid or not.
     *
     * @param state     the state
     * @param level     the level
     * @param pos       the position
     * @param viewpoint the viewpoint
     * @return the block state that should be 'seen'
     */
    default BlockState getStateAtViewpoint(BlockState state, BlockGetter level, BlockPos pos, Vec3 viewpoint)
    {
        return state;
    }

    /**
     * Get the {@code PathNodeType} for this block. Return {@code null} for vanilla behavior.
     *
     * @return the PathNodeType
     */
    @Nullable
    default BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob entity)
    {
        return state.getBlock() == Blocks.LAVA ? BlockPathTypes.LAVA : state.isBurning(level, pos) ? BlockPathTypes.DAMAGE_FIRE : null;
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
     * @param level The current level
     * @param pos Block position in level
     * @param direction The direction that the fire is coming from
     * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
     */
    default int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
    {
        return ((FireBlock)Blocks.FIRE).getBurnOdd(state);
    }

    /**
     * Called when fire is updating, checks if a block face can catch fire.
     *
     *
     * @param state The current state
     * @param level The current level
     * @param pos Block position in level
     * @param direction The direction that the fire is coming from
     * @return True if the face can be on fire, false otherwise.
     */
    default boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
    {
        return state.getFlammability(level, pos, direction) > 0;
    }

    /**
     * If the block is flammable, this is called when it gets lit on fire.
     *
     * @param state The current state
     * @param level The current level
     * @param pos Block position in level
     * @param direction The direction that the fire is coming from
     * @param igniter The entity that lit the fire
     */
    default void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {}

    /**
     * Called when fire is updating on a neighbor block.
     * The higher the number returned, the faster fire will spread around this block.
     *
     * @param state The current state
     * @param level The current level
     * @param pos Block position in level
     * @param direction The direction that the fire is coming from
     * @return A number that is used to determine the speed of fire growth around the block
     */
    default int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
    {
        return ((FireBlock)Blocks.FIRE).getFlameOdds(state);
    }

    /**
     * Currently only called by fire when it is on top of this block.
     * Returning true will prevent the fire from naturally dying during updating.
     * Also prevents firing from dying from rain.
     *
     * @param state The current state
     * @param level The current level
     * @param pos Block position in level
     * @param direction The direction that the fire is coming from
     * @return True if this block sustains fire, meaning it will never go out.
     */
    default boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction direction)
    {
        return state.is(level.dimensionType().infiniburn());
    }

    /**
     * Determines if this block is can be destroyed by the specified entities normal behavior.
     *
     * @param state The current state
     * @param level The current level
     * @param pos Block position in level
     * @return True to allow the ender dragon to destroy this block
     */
    default boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity)
    {
        if (entity instanceof EnderDragon)
        {
            return !this.self().defaultBlockState().is(BlockTags.DRAGON_IMMUNE);
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
    default boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return state.getBlock().dropFromExplosion(explosion);
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
    default void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion)
    {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        self().wasExploded(level, pos, explosion);
    }

    /**
     * Determines if this block's collision box should be treated as though it can extend above its block space.
     * Use this to replicate fence and wall behavior.
     */
    default boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity)
    {
        return state.is(BlockTags.FENCES) || state.is(BlockTags.WALLS) || self() instanceof FenceGateBlock;
    }

    /**
     * Called to determine whether this block should use the fluid overlay texture or flowing texture when it is placed under the fluid.
     *
     * @param state The current state
     * @param level The level
     * @param pos Block position in level
     * @param fluidState The state of the fluid
     * @return Whether the fluid overlay texture should be used
     */
    default boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState)
    {
        return state.getBlock() instanceof HalfTransparentBlock || state.getBlock() instanceof LeavesBlock;
    }

    /**
     * Returns the state that this block should transform into when right-clicked by a tool.
     * For example: Used to determine if {@link ToolActions#AXE_STRIP an axe can strip},
     * {@link ToolActions#SHOVEL_FLATTEN a shovel can path}, or {@link ToolActions#HOE_TILL a hoe can till}.
     * Returns {@code null} if nothing should happen.
     *
     * @param state The current state
     * @param context The use on context that the action was performed in
     * @param toolAction The action being performed by the tool
     * @param simulate If {@code true}, no actions that modify the world in any way should be performed. If {@code false}, the world may be modified.
     * @return The resulting state after the action has been performed
     */
    @Nullable
    default BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate)
    {
        BlockState toolModifiedState = getToolModifiedState(state, context.getLevel(), context.getClickedPos(),
                context.getPlayer(), context.getItemInHand(), toolAction);

        if (toolModifiedState == null && ToolActions.HOE_TILL == toolAction && context.getItemInHand().canPerformAction(ToolActions.HOE_TILL))
        {
            // Logic copied from HoeItem#TILLABLES; needs to be kept in sync during updating
            Block block = state.getBlock();
            if (block == Blocks.ROOTED_DIRT)
            {
                if (!simulate && !context.getLevel().isClientSide)
                {
                    Block.popResourceFromFace(context.getLevel(), context.getClickedPos(), context.getClickedFace(), new ItemStack(Items.HANGING_ROOTS));
                }
                return Blocks.DIRT.defaultBlockState();
            } else if ((block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.COARSE_DIRT) &&
                    context.getLevel().getBlockState(context.getClickedPos().above()).isAir())
            {
                return block == Blocks.COARSE_DIRT ? Blocks.DIRT.defaultBlockState() : Blocks.FARMLAND.defaultBlockState();
            }
        }

        return toolModifiedState;
    }

    /**
     * Returns the state that this block should transform into when right-clicked by a tool.
     * For example: Used to determine if {@link ToolActions#AXE_STRIP an axe can strip} or {@link ToolActions#SHOVEL_FLATTEN a shovel can path}.
     * Returns {@code null} if nothing should happen.
     *
     * @param state The current state
     * @param level The level
     * @param pos The block position in level
     * @param player The player clicking the block
     * @param stack The stack being used by the player
     * @param toolAction The action being performed by the tool
     * @return The resulting state after the action has been performed
     * @deprecated Override and use {@link #getToolModifiedState(BlockState, UseOnContext, ToolAction, boolean)} instead
     */
    @Nullable
    // TODO 1.19: Remove this and move the default impl to the newer method in 1.19. Has to stay here to preserve behavior of overrides on this method.
    @Deprecated(forRemoval = true, since = "1.18.2")
    default BlockState getToolModifiedState(BlockState state, Level level, BlockPos pos, Player player, ItemStack stack, ToolAction toolAction)
    {
        if (!stack.canPerformAction(toolAction)) return null;
        if (ToolActions.AXE_STRIP == toolAction) return AxeItem.getAxeStrippingState(state);
        else if(ToolActions.AXE_SCRAPE == toolAction) return WeatheringCopper.getPrevious(state).orElse(null);
        else if(ToolActions.AXE_WAX_OFF == toolAction) return Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock())).map((p_150694_) -> {
            return p_150694_.withPropertiesOf(state);
        }).orElse(null);
        else if (ToolActions.SHOVEL_FLATTEN == toolAction) return ShovelItem.getShovelPathingState(state);
        return null;
    }

    /**
     * Checks if a player or entity handles movement on this block like scaffolding.
     *
     * @param state The current state
     * @param level The current level
     * @param pos The block position in level
     * @param entity The entity on the scaffolding
     * @return True if the block should act like scaffolding
     */
    default boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity)
    {
        return state.is(Blocks.SCAFFOLDING);
    }

    /**
     * Whether redstone dust should visually connect to this block on a given side
     * <p>
     * The default implementation is identical to
     * {@code RedStoneWireBlock#shouldConnectTo(BlockState, Direction)}
     *
     * <p>
     * {@link RedStoneWireBlock} updates its visual connection when
     * {@link BlockState#updateShape(Direction, BlockState, LevelAccessor, BlockPos, BlockPos)}
     * is called, this callback is used during the evaluation of its new shape.
     *
     * @param state The current state
     * @param level The level
     * @param pos The block position in level
     * @param direction The coming direction of the redstone dust connection (with respect to the block at pos)
     * @return True if redstone dust should visually connect on the side passed
     * <p>
     * If the return value is evaluated based on level and pos (e.g. from BlockEntity), then the implementation of
     * this block should notify its neighbors to update their shapes when necessary. Consider using
     * {@link BlockState#updateNeighbourShapes(LevelAccessor, BlockPos, int, int)} or
     * {@link BlockState#updateShape(Direction, BlockState, LevelAccessor, BlockPos, BlockPos)}.
     * <p>
     * Example:
     * <p>
     * 1. {@code yourBlockState.updateNeighbourShapes(level, yourBlockPos, UPDATE_ALL);}
     * <p>
     * 2. {@code neighborState.updateShape(fromDirection, stateOfYourBlock, level, neighborBlockPos, yourBlockPos)},
     * where {@code fromDirection} is defined from the neighbor block's point of view.
     */
    default boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction)
    {
        if (state.is(Blocks.REDSTONE_WIRE))
        {
            return true;
        }
        else if (state.is(Blocks.REPEATER))
        {
            Direction facing = state.getValue(RepeaterBlock.FACING);
            return facing == direction || facing.getOpposite() == direction;
        }
        else if (state.is(Blocks.OBSERVER))
        {
            return direction == state.getValue(ObserverBlock.FACING);
        }
        else
        {
            return state.isSignalSource() && direction != null;
        }
    }

    /**
     * Whether this block hides the neighbors face pointed towards by the given direction.
     * <p>
     * This method should only be used for blocks you don't control, for your own blocks override
     * {@link Block#skipRendering(BlockState, BlockState, Direction)} on the respective block instead
     * <p>
     * WARNING: This method is likely to be called from a worker thread! If you want to retrieve a
     *          {@link net.minecraft.world.level.block.entity.BlockEntity} from the given level, make sure to use
     *          {@link net.minecraftforge.common.extensions.IForgeBlockGetter#getExistingBlockEntity(BlockPos)} to not
     *          accidentally create a new or delete an old {@link net.minecraft.world.level.block.entity.BlockEntity}
     *          off of the main thread as this would cause a write operation to the given {@link BlockGetter} and cause
     *          a CME in the process. Any other direct or indirect write operation to the {@link BlockGetter} will have
     *          the same outcome.
     *
     * @param level The world
     * @param pos The blocks position in the world
     * @param state The blocks {@link BlockState}
     * @param neighborState The neighboring blocks {@link BlockState}
     * @param dir The direction towards the neighboring block
     */
    default boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir)
    {
        return false;
    }

    /**
     * Whether this block allows a neighboring block to hide the face of this block it touches.
     * If this returns true, {@link IForgeBlockState#hidesNeighborFace(BlockGetter, BlockPos, BlockState, Direction)}
     * will be called on the neighboring block.
     */
    default boolean supportsExternalFaceHiding(BlockState state)
    {
        if (FMLEnvironment.dist.isClient())
        {
            return !ForgeHooksClient.isBlockInSolidLayer(state);
        }
        return true;
    }
}
