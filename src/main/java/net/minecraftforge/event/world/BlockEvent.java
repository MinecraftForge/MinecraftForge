/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.world;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockEvent extends Event
{
    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugBlockEvent", "false"));

    private final LevelAccessor world;
    private final BlockPos pos;
    private final BlockState state;
    public BlockEvent(LevelAccessor world, BlockPos pos, BlockState state)
    {
        this.pos = pos;
        this.world = world;
        this.state = state;
    }

    public LevelAccessor getWorld()
    {
        return world;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public BlockState getState()
    {
        return state;
    }

    /**
     * Event that is fired when an Block is about to be broken by a player
     * Canceling this event will prevent the Block from being broken.
     */
    @Cancelable
    public static class BreakEvent extends BlockEvent
    {
        /** Reference to the Player who broke the block. If no player is available, use a EntityFakePlayer */
        private final Player player;
        private int exp;

        public BreakEvent(Level world, BlockPos pos, BlockState state, Player player)
        {
            super(world, pos, state);
            this.player = player;

            if (state == null || !ForgeHooks.isCorrectToolForDrops(state, player)) // Handle empty block or player unable to break block scenario
            {
                this.exp = 0;
            }
            else
            {
                int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, player.getMainHandItem());
                int silkTouchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, player.getMainHandItem());
                this.exp = state.getExpDrop(world, pos, fortuneLevel, silkTouchLevel);
            }
        }

        public Player getPlayer()
        {
            return player;
        }

        /**
         * Get the experience dropped by the block after the event has processed
         *
         * @return The experience to drop or 0 if the event was canceled
         */
        public int getExpToDrop()
        {
            return this.isCanceled() ? 0 : exp;
        }

        /**
         * Set the amount of experience dropped by the block after the event has processed
         *
         * @param exp 1 or higher to drop experience, else nothing will drop
         */
        public void setExpToDrop(int exp)
        {
            this.exp = exp;
        }
    }

    /**
     * Called when a block is placed.
     *
     * If a Block Place event is cancelled, the block will not be placed.
     */
    @Cancelable
    public static class EntityPlaceEvent extends BlockEvent
    {
        private final Entity entity;
        private final BlockSnapshot blockSnapshot;
        private final BlockState placedBlock;
        private final BlockState placedAgainst;

        public EntityPlaceEvent(@Nonnull BlockSnapshot blockSnapshot, @Nonnull BlockState placedAgainst, @Nullable Entity entity)
        {
            super(blockSnapshot.getLevel(), blockSnapshot.getPos(), !(entity instanceof Player) ? blockSnapshot.getReplacedBlock() : blockSnapshot.getCurrentBlock());
            this.entity = entity;
            this.blockSnapshot = blockSnapshot;
            this.placedBlock = !(entity instanceof Player) ? blockSnapshot.getReplacedBlock() : blockSnapshot.getCurrentBlock();
            this.placedAgainst = placedAgainst;

            if (DEBUG)
            {
                System.out.printf("Created EntityPlaceEvent - [PlacedBlock: %s ][PlacedAgainst: %s ][Entity: %s ]\n", getPlacedBlock(), placedAgainst, entity);
            }
        }

        @Nullable
        public Entity getEntity() { return entity; }
        public BlockSnapshot getBlockSnapshot() { return blockSnapshot; }
        public BlockState getPlacedBlock() { return placedBlock; }
        public BlockState getPlacedAgainst() { return placedAgainst; }
    }

    /**
     * Fired when a single block placement triggers the
     * creation of multiple blocks(e.g. placing a bed block). The block returned
     * by {@link #state} and its related methods is the block where
     * the placed block would exist if the placement only affected a single
     * block.
     */
    @Cancelable
    public static class EntityMultiPlaceEvent extends EntityPlaceEvent
    {
        private final List<BlockSnapshot> blockSnapshots;

        public EntityMultiPlaceEvent(@Nonnull List<BlockSnapshot> blockSnapshots, @Nonnull BlockState placedAgainst, @Nullable Entity entity) {
            super(blockSnapshots.get(0), placedAgainst, entity);
            this.blockSnapshots = ImmutableList.copyOf(blockSnapshots);
            if (DEBUG)
            {
                System.out.printf("Created EntityMultiPlaceEvent - [PlacedAgainst: %s ][Entity: %s ]\n", placedAgainst, entity);
            }
        }

        /**
         * Gets a list of BlockSnapshots for all blocks which were replaced by the
         * placement of the new blocks. Most of these blocks will just be of type AIR.
         *
         * @return immutable list of replaced BlockSnapshots
         */
        public List<BlockSnapshot> getReplacedBlockSnapshots()
        {
            return blockSnapshots;
        }
    }

    /**
     * Fired when a physics update occurs on a block. This event acts as
     * a way for mods to detect physics updates, in the same way a BUD switch
     * does. This event is only called on the server.
     */
    @Cancelable
    public static class NeighborNotifyEvent extends BlockEvent
    {
        private final EnumSet<Direction> notifiedSides;
        private final boolean forceRedstoneUpdate;

        public NeighborNotifyEvent(Level world, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate)
        {
            super(world, pos, state);
            this.notifiedSides = notifiedSides;
            this.forceRedstoneUpdate = forceRedstoneUpdate;
        }

        /**
         * Gets a list of directions from the base block that updates will occur upon.
         *
         * @return list of notified directions
         */
        public EnumSet<Direction> getNotifiedSides()
        {
            return notifiedSides;
        }

        /**
         * Get if redstone update was forced during setBlock call (0x16 to flags)
         * @return if the flag was set
         */
        public boolean getForceRedstoneUpdate()
        {
            return forceRedstoneUpdate;
        }
    }

    /**
     * Fired to check whether a non-source block can turn into a source block.
     * A result of ALLOW causes a source block to be created even if the liquid
     * usually doesn't do that (like lava), and a result of DENY prevents creation
     * even if the liquid usually does do that (like water).
     */
    @HasResult
    public static class CreateFluidSourceEvent extends Event
    {
        private final LevelReader world;
        private final BlockPos pos;
        private final BlockState state;

        public CreateFluidSourceEvent(LevelReader world, BlockPos pos, BlockState state)
        {
            this.world = world;
            this.pos = pos;
            this.state = state;
        }

        public LevelReader getWorld()
        {
            return world;
        }

        public BlockPos getPos()
        {
            return pos;
        }

        public BlockState getState()
        {
            return state;
        }
    }

    /**
     * Fired when a liquid places a block. Use {@link #setNewState(BlockState)} to change the result of
     * a cobblestone generator or add variants of obsidian. Alternatively, you  could execute
     * arbitrary code when lava sets blocks on fire, even preventing it.
     *
     * {@link #getState()} will return the block that was originally going to be placed.
     * {@link #getPos()} will return the position of the block to be changed.
     */
    @Cancelable
    public static class FluidPlaceBlockEvent extends BlockEvent
    {
        private final BlockPos liquidPos;
        private BlockState newState;
        private BlockState origState;

        public FluidPlaceBlockEvent(LevelAccessor world, BlockPos pos, BlockPos liquidPos, BlockState state)
        {
            super(world, pos, state);
            this.liquidPos = liquidPos;
            this.newState = state;
            this.origState = world.getBlockState(pos);
        }

        /**
         * @return The position of the liquid this event originated from. This may be the same as {@link #getPos()}.
         */
        public BlockPos getLiquidPos()
        {
            return liquidPos;
        }

        /**
         * @return The block state that will be placed after this event resolves.
         */
        public BlockState getNewState()
        {
            return newState;
        }

        public void setNewState(BlockState state)
        {
            this.newState = state;
        }

        /**
         * @return The state of the block to be changed before the event was fired.
         */
        public BlockState getOriginalState()
        {
            return origState;
        }
    }

    /**
     * Fired when a crop block grows.  See subevents.
     *
     */
    public static class CropGrowEvent extends BlockEvent
    {
        public CropGrowEvent(Level world, BlockPos pos, BlockState state)
        {
            super(world, pos, state);
        }

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
        @HasResult
        public static class Pre extends CropGrowEvent
        {
            public Pre(Level world, BlockPos pos, BlockState state)
            {
                super(world, pos, state);
            }
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
        public static class Post extends CropGrowEvent
        {
            private final BlockState originalState;
            public Post(Level world, BlockPos pos, BlockState original, BlockState state)
            {
                super(world, pos, state);
                originalState = original;
            }

            public BlockState getOriginalState()
            {
                return originalState;
            }
        }
    }

    /**
     * Fired when when farmland gets trampled
     * This event is {@link Cancelable}
     */
    @Cancelable
    public static class FarmlandTrampleEvent extends BlockEvent
    {

        private final Entity entity;
        private final float fallDistance;

        public FarmlandTrampleEvent(Level world, BlockPos pos, BlockState state, float fallDistance, Entity entity)
        {
            super(world, pos, state);
            this.entity = entity;
            this.fallDistance = fallDistance;
        }

        public Entity getEntity() {
            return entity;
        }

        public float getFallDistance() {
            return fallDistance;
        }

    }

    /* Fired when an attempt is made to spawn a nether portal from
     * {@link net.minecraft.block.BlockPortal#trySpawnPortal(World, BlockPos)}.
     *
     * If cancelled, the portal will not be spawned.
     */
    @Cancelable
    public static class PortalSpawnEvent extends BlockEvent
    {
        private final PortalShape size;

        public PortalSpawnEvent(LevelAccessor world, BlockPos pos, BlockState state, PortalShape size)
        {
            super(world, pos, state);
            this.size = size;
        }

        public PortalShape getPortalSize()
        {
            return size;
        }
    }

    /**
     * Fired when when this block is right clicked by a tool to change its state.
     * For example: Used to determine if an axe can strip or a shovel can path.
     * For hoes, see {@code net.minecraft.world.item.HoeItem#TILLABLES} and
     * {@link net.minecraftforge.event.entity.player.UseHoeEvent}.
     *
     * This event is {@link Cancelable}. If canceled, this will prevent the tool
     * from changing the block's state.
     */
    @Cancelable
    public static class BlockToolInteractEvent extends BlockEvent
    {

        private final Player player;
        private final ItemStack stack;
        private final ToolAction toolAction;
        private BlockState state;

        public BlockToolInteractEvent(LevelAccessor world, BlockPos pos, BlockState originalState, Player player, ItemStack stack, ToolAction toolAction)
        {
            super(world, pos, originalState);
            this.player = player;
            this.stack = stack;
            this.state = originalState;
            this.toolAction = toolAction;
        }

        /**Gets the player using the tool.*/
        public Player getPlayer()
        {
            return player;
        }

        /**Gets the tool being used.*/
        public ItemStack getHeldItemStack()
        {
            return stack;
        }

        /**Gets the action being performed.*/
        public ToolAction getToolAction()
        {
            return toolAction;
        }

        /**
         * Sets the transformed state after tool use.
         * If not set, will return the original state.
         * This will be bypassed if canceled returning null instead.
         * */
        public void setFinalState(BlockState finalState)
        {
            this.state = finalState;
        }

        /**
         * Gets the transformed state after tool use.
         * If setFinalState not called, will return the original state.
         * This will be bypassed if canceled returning null instead.
         * */
        public BlockState getFinalState()
        {
            return state;
        }
    }
}
