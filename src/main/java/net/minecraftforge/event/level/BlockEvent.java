/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.util.BlockSnapshot;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.util.HasResult;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public sealed interface BlockEvent
        permits BlockEvent.BlockToolModificationEvent, BlockEvent.BreakEvent, BlockEvent.CropGrowEvent,
                BlockEvent.EntityPlaceEvent, BlockEvent.FarmlandTrampleEvent, BlockEvent.FluidPlaceBlockEvent,
                BlockEvent.NeighborNotifyEvent, BlockEvent.PortalSpawnEvent, NoteBlockEvent, PistonEvent {
    boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugBlockEvent", "false"));

    LevelAccessor level();
    BlockPos pos();
    BlockState state();

    /**
     * Event that is fired when an Block is about to be broken by a player
     * Canceling this event will prevent the Block from being broken.
     */
    final class BreakEvent extends MutableEvent implements Cancellable, BlockEvent {
        public static final CancellableEventBus<BreakEvent> BUS = CancellableEventBus.create(BreakEvent.class);

        private final LevelAccessor level;
        private final BlockPos pos;
        private final BlockState state;

        /** Reference to the Player who broke the block. If no player is available, use a EntityFakePlayer */
        private final Player player;
        private int exp;

        public BreakEvent(Level level, BlockPos pos, BlockState state, Player player) {
            this.level = level;
            this.pos = pos;
            this.state = state;
            this.player = player;

            if (state == null || !ForgeHooks.isCorrectToolForDrops(state, player)) { // Handle empty block or player unable to break block scenario
                this.exp = 0;
            } else {
                var lookup = level.registryAccess().lookup(Registries.ENCHANTMENT).get();

                int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(lookup.getOrThrow(Enchantments.FORTUNE), player.getMainHandItem());
                int silkTouchLevel = EnchantmentHelper.getItemEnchantmentLevel(lookup.getOrThrow(Enchantments.SILK_TOUCH), player.getMainHandItem());
                this.exp = state.getExpDrop(level, level.random, pos, fortuneLevel, silkTouchLevel);
            }
        }

        @Override
        public LevelAccessor level() {
            return level;
        }

        @Override
        public BlockPos pos() {
            return pos;
        }

        @Override
        public BlockState state() {
            return state;
        }

        public Player getPlayer() {
            return player;
        }

        /**
         * Get the experience dropped by the block after the event has processed
         *
         * @return The experience to drop or 0 if the event was canceled
         */
        public int getExpToDrop() {
            return this.isCanceled() ? 0 : exp;
        }

        /**
         * Set the amount of experience dropped by the block after the event has processed
         *
         * @param exp 1 or higher to drop experience, else nothing will drop
         */
        public void setExpToDrop(int exp) {
            this.exp = exp;
        }
    }

    /**
     * Called when a block is placed.
     *
     * If a Block Place event is cancelled, the block will not be placed.
     */
    record EntityPlaceEvent(
            LevelAccessor level,
            BlockPos pos,
            BlockState state,
            Entity entity,
            BlockSnapshot blockSnapshot,
            BlockState placedBlock,
            BlockState placedAgainst
    ) implements Cancellable, BlockEvent, RecordEvent {
        public static final CancellableEventBus<EntityPlaceEvent> BUS = CancellableEventBus.create(EntityPlaceEvent.class);

        public static EntityPlaceEvent of(BlockSnapshot blockSnapshot, BlockState placedAgainst, Entity entity) {
            var placedBlock = !(entity instanceof Player) ? blockSnapshot.getReplacedBlock() : blockSnapshot.getCurrentBlock();
            var ret = new EntityPlaceEvent(blockSnapshot.getLevel(), blockSnapshot.getPos(), placedBlock, entity, blockSnapshot, placedBlock, placedAgainst);
            if (DEBUG)
                System.out.printf("Created EntityPlaceEvent - [PlacedBlock: %s ][PlacedAgainst: %s ][Entity: %s ]\n", placedBlock, placedAgainst, entity);

            return ret;
        }
    }

    /**
     * Fired when a single block placement triggers the
     * creation of multiple blocks(e.g. placing a bed block). The block returned
     * by {@link #state} and its related methods is the block where
     * the placed block would exist if the placement only affected a single
     * block.
     */
    @Cancelable
    public static class EntityMultiPlaceEvent extends EntityPlaceEvent {
        private final List<BlockSnapshot> blockSnapshots;

        public EntityMultiPlaceEvent(@NotNull List<BlockSnapshot> blockSnapshots, @NotNull BlockState placedAgainst, @Nullable Entity entity) {
            super(blockSnapshots.get(0), placedAgainst, entity);
            this.blockSnapshots = ImmutableList.copyOf(blockSnapshots);
            if (DEBUG) {
                System.out.printf("Created EntityMultiPlaceEvent - [PlacedAgainst: %s ][Entity: %s ]\n", placedAgainst, entity);
            }
        }

        /**
         * Gets a list of BlockSnapshots for all blocks which were replaced by the
         * placement of the new blocks. Most of these blocks will just be of type AIR.
         *
         * @return immutable list of replaced BlockSnapshots
         */
        public List<BlockSnapshot> getReplacedBlockSnapshots() {
            return blockSnapshots;
        }
    }

    /**
     * Fired when a physics update occurs on a block. This event acts as
     * a way for mods to detect physics updates, in the same way a BUD switch
     * does. This event is only called on the server.
     *
     * @param notifiedSides A list of directions from the base block that updates will occur upon.
     * @param forceRedstoneUpdate If redstone update was forced during setBlock call (0x16 to flags)
     */
    record NeighborNotifyEvent(
            Level level,
            BlockPos pos,
            BlockState state,
            EnumSet<Direction> notifiedSides,
            boolean forceRedstoneUpdate
    ) implements Cancellable, BlockEvent, RecordEvent {
        public static final CancellableEventBus<NeighborNotifyEvent> BUS = CancellableEventBus.create(NeighborNotifyEvent.class);
    }

    /**
     * Fired to check whether a non-source block can turn into a source block.
     * A result of ALLOW causes a source block to be created even if the liquid
     * usually doesn't do that (like lava), and a result of DENY prevents creation
     * even if the liquid usually does do that (like water).
     */
    record CreateFluidSourceEvent(Level level, BlockPos pos, BlockState state, Result.Holder resultHolder)
            implements RecordEvent, HasResult.Record {
        public static final EventBus<CreateFluidSourceEvent> BUS = EventBus.create(CreateFluidSourceEvent.class);
    }

    /**
     * Fired when a liquid places a block. Use {@link #setNewState(BlockState)} to change the result of
     * a cobblestone generator or add variants of obsidian. Alternatively, you  could execute
     * arbitrary code when lava sets blocks on fire, even preventing it.
     *
     * {@link #getState()} will return the block that was originally going to be placed.
     * {@link #getPos()} will return the position of the block to be changed.
     */
    final class FluidPlaceBlockEvent extends MutableEvent implements Cancellable, BlockEvent {
        public static final CancellableEventBus<FluidPlaceBlockEvent> BUS = CancellableEventBus.create(FluidPlaceBlockEvent.class);

        private final LevelAccessor level;
        private final BlockPos pos;
        private final BlockState state;

        private final BlockPos liquidPos;
        private BlockState newState;
        private BlockState origState;

        public FluidPlaceBlockEvent(LevelAccessor level, BlockPos pos, BlockPos liquidPos, BlockState state) {
            this.level = level;
            this.pos = pos;
            this.state = state;

            this.liquidPos = liquidPos;
            this.newState = state;
            this.origState = level.getBlockState(pos);
        }

        /**
         * @return The position of the liquid this event originated from. This may be the same as {@link #getPos()}.
         */
        public BlockPos getLiquidPos() {
            return liquidPos;
        }

        /**
         * @return The block state that will be placed after this event resolves.
         */
        public BlockState getNewState() {
            return newState;
        }

        public void setNewState(BlockState state) {
            this.newState = state;
        }

        /**
         * @return The state of the block to be changed before the event was fired.
         */
        public BlockState getOriginalState() {
            return origState;
        }

        @Override
        public LevelAccessor level() {
            return level;
        }

        @Override
        public BlockPos pos() {
            return pos;
        }

        @Override
        public BlockState state() {
            return state;
        }
    }

    /**
     * Fired when a crop block grows.
     * @see CropGrowEvent.Pre
     * @see CropGrowEvent.Post
     */
    sealed interface CropGrowEvent extends BlockEvent {
        /**
         * Fired when any "growing age" blocks (for example cacti, chorus plants, or crops
         * in vanilla) attempt to advance to the next growth age state during a random tick.<br>
         * <br>
         * {@link Result#DEFAULT} will pass on to the vanilla growth mechanics.<br>
         * {@link Result#ALLOW} will force the plant to advance a growth stage.<br>
         * {@link Result#DENY} will prevent the plant from advancing a growth stage.<br>
         * <br>
         * This event is not {@link Cancelable}.<br>
         * <br>
         */
        record Pre(Level level, BlockPos pos, BlockState state, Result.Holder resultHolder)
                implements CropGrowEvent, RecordEvent, HasResult.Record {
            public static final EventBus<Pre> BUS = EventBus.create(Pre.class);
        }

        /**
         * Fired when "growing age" blocks (for example cacti, chorus plants, or crops
         * in vanilla) have successfully grown. The block's original state is available,
         * in addition to its new state.<br>
         * <br>
         * This event is not {@link Cancelable}.<br>
         * <br>
         * This event does not have a result. {@link HasResult}<br>
         */
        record Post(Level level, BlockPos pos, BlockState originalState, BlockState state) implements CropGrowEvent, RecordEvent {
            public static final EventBus<Post> BUS = EventBus.create(Post.class);
        }
    }

    /**
     * Fired when when farmland gets trampled
     * This event is {@link Cancelable}
     */
    record FarmlandTrampleEvent(
            Level level,
            BlockPos pos,
            BlockState state,
            float fallDistance,
            Entity entity
    ) implements Cancellable, BlockEvent, RecordEvent {
        public static final CancellableEventBus<FarmlandTrampleEvent> BUS = CancellableEventBus.create(FarmlandTrampleEvent.class);
    }

    /** Fired when an attempt is made to spawn a nether portal from
     * {@link BaseFireBlock#onPlace(BlockState, Level, BlockPos, BlockState, boolean)}.
     * <br>
     * If cancelled, the portal will not be spawned.
     */
    record PortalSpawnEvent(
            LevelAccessor level,
            BlockPos pos,
            BlockState state,
            PortalShape portalSize
    ) implements Cancellable, BlockEvent, RecordEvent {
        public static final CancellableEventBus<PortalSpawnEvent> BUS = CancellableEventBus.create(PortalSpawnEvent.class);
    }

    /**
     * Fired when a block is right-clicked by a tool to change its state.
     * For example: Used to determine if {@link ToolActions#AXE_STRIP an axe can strip},
     * {@link ToolActions#SHOVEL_FLATTEN a shovel can path}, or {@link ToolActions#HOE_TILL a hoe can till}.
     * <p>
     * Care must be taken to ensure level-modifying events are only performed if {@link #isSimulated()} returns {@code false}.
     * <p>
     * This event is {@link Cancelable}. If canceled, this will prevent the tool
     * from changing the block's state.
     */
    final class BlockToolModificationEvent extends MutableEvent implements Cancellable, BlockEvent {
        public static final CancellableEventBus<BlockToolModificationEvent> BUS = CancellableEventBus.create(BlockToolModificationEvent.class);

        private final UseOnContext context;
        private final ToolAction toolAction;
        private final boolean simulate;
        private BlockState state;

        public BlockToolModificationEvent(BlockState originalState, @NotNull UseOnContext context, ToolAction toolAction, boolean simulate) {
            super(context.getLevel(), context.getClickedPos(), originalState);
            this.context = context;
            this.state = originalState;
            this.toolAction = toolAction;
            this.simulate = simulate;
        }

        /**
         * @return the player using the tool.
         * May be null based on what was provided by {@link #getContext() the use on context}.
         */
        @Nullable
        public Player getPlayer() {
            return this.context.getPlayer();
        }

        /**
         * @return the tool being used
         */
        public ItemStack getHeldItemStack() {
            return this.context.getItemInHand();
        }

        /**
         * @return the action being performed
         */
        public ToolAction getToolAction() {
            return this.toolAction;
        }

        /**
         * Returns {@code true} if this event should not perform any actions that modify the level.
         * If {@code false}, then level-modifying actions can be performed.
         *
         * @return {@code true} if this event should not perform any actions that modify the level.
         * If {@code false}, then level-modifying actions can be performed.
         */
        public boolean isSimulated() {
            return this.simulate;
        }

        /**
         * Returns the nonnull use on context that this event was performed in.
         *
         * @return the nonnull use on context that this event was performed in
         */
        @NotNull
        public UseOnContext getContext() {
            return context;
        }

        /**
         * Sets the state to transform the block into after tool use.
         *
         * @param finalState the state to transform the block into after tool use
         * @see #getFinalState()
         */
        public void setFinalState(@Nullable BlockState finalState) {
            this.state = finalState;
        }

        /**
         * Returns the state to transform the block into after tool use.
         * If {@link #setFinalState(BlockState)} is not called, this will return the original state.
         * If {@link #isCanceled()} is {@code true}, this value will be ignored and the tool action will be canceled.
         *
         * @return the state to transform the block into after tool use
         */
        public BlockState getFinalState() {
            return state;
        }
    }
}
