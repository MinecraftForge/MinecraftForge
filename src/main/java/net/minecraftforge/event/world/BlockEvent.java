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

package net.minecraftforge.event.world;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.eventbus.api.Event.HasResult;

public class BlockEvent extends Event
{
    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugBlockEvent", "false"));

    private final IWorld world;
    private final BlockPos pos;
    private final BlockState state;
    public BlockEvent(IWorld world, BlockPos pos, BlockState state)
    {
        this.pos = pos;
        this.world = world;
        this.state = state;
    }

    public IWorld getWorld()
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
     * Fired just before a block's loot is generated,
     * used for modifying the loot context and loot table that will be used to generate the loot for a block.
     *
     * Cancelling this event will skip the loot generation entirely and simply pass an empty list to the block.
     */
    @Cancelable
    public static class GenerateLootEvent extends BlockEvent
    {
        private final LootContext.Builder contextBuilder;
        private LootTable table;

        public GenerateLootEvent(World world, BlockPos pos, BlockState state, LootTable table, LootContext.Builder contextBuilder)
        {
            super(world, pos, state);
            this.table = table;
            this.contextBuilder = contextBuilder;
        }

        /**
         * Get the loot context builder that will be used to generate the block's loot.
         * Used to modify or gather data from the final loot context that will be built.
         *
         * @return The loot context builder that will be used for the generation of loot.
         */
        public LootContext.Builder getContextBuilder()
        {
            return contextBuilder;
        }

        /**
         * Get the loot table that was selected to generate loot for the block.
         *
         * @return the loot table being used to generate block loot.
         */
        public LootTable getTable()
        {
            return table;
        }

        /**
         * Set the loot table that will be used to generate loot for the block.
         *
         * @param table the new loot table that will be used to generate block loot.
         */
        public void setTable(LootTable table)
        {
            this.table = table;
        }
    }

    /**
     * Fired just before a block's loot is dropped into the world.
     * Used to modify a block's drops after they're already generated.
     *
     * Cancelling this event will prevent the block from spawning in any drops.
     */
    @Cancelable
    public static class DropLootEvent extends BlockEvent
    {
        private final PlayerEntity player;
        private final LootContext context;
        private final NonNullList<ItemStack> drops;

        public DropLootEvent(World world, BlockPos pos, BlockState state, @Nullable LootContext context, @Nullable PlayerEntity player, NonNullList<ItemStack> drops)
        {
            super(world, pos, state);
            this.context = context;
            this.player = player;
            this.drops = drops;
        }

        /**
         * Get the loot context that was used to generate the dropped loot.
         *
         * @return The context that was used when generating the drops, or null if none was used.
         */
        @Nullable
        public LootContext getContext()
        {
            return context;
        }

        /**
         * Get the player that caused the loot to drop.
         *
         * @return The player that caused the loot to drop, or null if no player was directly involved.
         */
        @Nullable
        public PlayerEntity getPlayer()
        {
            return player;
        }

        /**
         * Get the list of the drops that will be added to the world after the event has processed.
         *
         * @return The list of drops to spawn, or an empty list if the event was cancelled.
         */
        public NonNullList<ItemStack> getDrops()
        {
            return this.isCanceled() ? NonNullList.create() : drops;
        }
    }

    /**
     * Event that is fired when an Block is about to be broken by a player
     * Canceling this event will prevent the Block from being broken.
     */
    @Cancelable
    public static class BreakEvent extends BlockEvent
    {
        /** Reference to the Player who broke the block. If no player is available, use a EntityFakePlayer */
        private final PlayerEntity player;
        private int exp;

        public BreakEvent(World world, BlockPos pos, BlockState state, PlayerEntity player)
        {
            super(world, pos, state);
            this.player = player;

            if (state == null || !ForgeHooks.canHarvestBlock(state, player, world, pos)) // Handle empty block or player unable to break block scenario
            {
                this.exp = 0;
            }
            else
            {
                int bonusLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand());
                int silklevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getHeldItemMainhand());
                this.exp = state.getExpDrop(world, pos, bonusLevel, silklevel);
            }
        }

        public PlayerEntity getPlayer()
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
            super(blockSnapshot.getWorld(), blockSnapshot.getPos(), !(entity instanceof PlayerEntity) ? blockSnapshot.getReplacedBlock() : blockSnapshot.getCurrentBlock());
            this.entity = entity;
            this.blockSnapshot = blockSnapshot;
            this.placedBlock = !(entity instanceof PlayerEntity) ? blockSnapshot.getReplacedBlock() : blockSnapshot.getCurrentBlock();
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

        public NeighborNotifyEvent(World world, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate)
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
    public static class CreateFluidSourceEvent extends BlockEvent
    {
        public CreateFluidSourceEvent(World world, BlockPos pos, BlockState state)
        {
            super(world, pos, state);
        }
    }

    /**
     * Fired when a liquid places a block. Use {@link #setNewState(IBlockState)} to change the result of
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

        public FluidPlaceBlockEvent(World world, BlockPos pos, BlockPos liquidPos, BlockState state)
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
        public CropGrowEvent(World world, BlockPos pos, BlockState state)
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
            public Pre(World world, BlockPos pos, BlockState state)
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
            public Post(World world, BlockPos pos, BlockState original, BlockState state)
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

        public FarmlandTrampleEvent(World world, BlockPos pos, BlockState state, float fallDistance, Entity entity)
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
        private final NetherPortalBlock.Size size;

        public PortalSpawnEvent(IWorld world, BlockPos pos, BlockState state, NetherPortalBlock.Size size)
        {
            super(world, pos, state);
            this.size = size;
        }

        public NetherPortalBlock.Size getPortalSize()
        {
            return size;
        }
    }
}
