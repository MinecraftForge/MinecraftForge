/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import com.google.common.collect.ImmutableList;

public class BlockEvent extends Event
{
    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugBlockEvent", "false"));

    private final World world;
    private final BlockPos pos;
    private final IBlockState state;
    public BlockEvent(World world, BlockPos pos, IBlockState state)
    {
        this.pos = pos;
        this.world = world;
        this.state = state;
    }

    public World getWorld()
    {
        return world;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public IBlockState getState()
    {
        return state;
    }

    /**
     * Fired when a block is about to drop it's harvested items. The {@link #drops} array can be amended, as can the {@link #dropChance}.
     * <strong>Note well:</strong> the {@link #harvester} player field is null in a variety of scenarios. Code expecting null.
     *
     * The {@link #dropChance} is used to determine which items in this array will actually drop, compared to a random number. If you wish, you
     * can pre-filter yourself, and set {@link #dropChance} to 1.0f to always drop the contents of the {@link #drops} array.
     *
     * {@link #isSilkTouching} is set if this is considered a silk touch harvesting operation, vs a normal harvesting operation. Act accordingly.
     *
     * @author cpw
     */
    public static class HarvestDropsEvent extends BlockEvent
    {
        private final int fortuneLevel;
        private final List<ItemStack> drops;
        private final boolean isSilkTouching;
        private float dropChance; // Change to e.g. 1.0f, if you manipulate the list and want to guarantee it always drops
        private final EntityPlayer harvester; // May be null for non-player harvesting such as explosions or machines

        public HarvestDropsEvent(World world, BlockPos pos, IBlockState state, int fortuneLevel, float dropChance, List<ItemStack> drops, EntityPlayer harvester, boolean isSilkTouching)
        {
            super(world, pos, state);
            this.fortuneLevel = fortuneLevel;
            this.setDropChance(dropChance);
            this.drops = drops;
            this.isSilkTouching = isSilkTouching;
            this.harvester = harvester;
        }

        public int getFortuneLevel() { return fortuneLevel; }
        public List<ItemStack> getDrops() { return drops; }
        public boolean isSilkTouching() { return isSilkTouching; }
        public float getDropChance() { return dropChance; }
        public void setDropChance(float dropChance) { this.dropChance = dropChance; }
        public EntityPlayer getHarvester() { return harvester; }
    }

    /**
     * Event that is fired when an Block is about to be broken by a player
     * Canceling this event will prevent the Block from being broken.
     */
    @Cancelable
    public static class BreakEvent extends BlockEvent
    {
        /** Reference to the Player who broke the block. If no player is available, use a EntityFakePlayer */
        private final EntityPlayer player;
        private int exp;

        public BreakEvent(World world, BlockPos pos, IBlockState state, EntityPlayer player)
        {
            super(world, pos, state);
            this.player = player;

            if (state == null || !ForgeHooks.canHarvestBlock(state.getBlock(), player, world, pos) || // Handle empty block or player unable to break block scenario
                (state.getBlock().canSilkHarvest(world, pos, world.getBlockState(pos), player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getHeldItemMainhand()) > 0)) // If the block is being silk harvested, the exp dropped is 0
            {
                this.exp = 0;
            }
            else
            {
                int bonusLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand());
                this.exp = state.getBlock().getExpDrop(state, world, pos, bonusLevel);
            }
        }

        public EntityPlayer getPlayer()
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
     * Called when a block is placed by a player.
     *
     * If a Block Place event is cancelled, the block will not be placed.
     */
    @Cancelable
    public static class PlaceEvent extends BlockEvent
    {
        private final EntityPlayer player;
        private final ItemStack itemInHand;
        private final BlockSnapshot blockSnapshot;
        private final IBlockState placedBlock;
        private final IBlockState placedAgainst;

        public PlaceEvent(BlockSnapshot blockSnapshot, IBlockState placedAgainst, EntityPlayer player)
        {
            super(blockSnapshot.getWorld(), blockSnapshot.getPos(), blockSnapshot.getCurrentBlock());
            this.player = player;
            this.itemInHand = player.getHeldItemMainhand();
            this.blockSnapshot = blockSnapshot;
            this.placedBlock = blockSnapshot.getCurrentBlock();
            this.placedAgainst = placedAgainst;
            if (DEBUG)
            {
                System.out.printf("Created PlaceEvent - [PlacedBlock: %s ][PlacedAgainst: %s ][ItemStack: %s ][Player: %s ]\n", getPlacedBlock(), placedAgainst, getItemInHand(), player);
            }
        }

        public EntityPlayer getPlayer() { return player; }
        public ItemStack getItemInHand() { return itemInHand; }
        public BlockSnapshot getBlockSnapshot() { return blockSnapshot; }
        public IBlockState getPlacedBlock() { return placedBlock; }
        public IBlockState getPlacedAgainst() { return placedAgainst; }
    }

    /**
     * Fired when a single block placement action of a player triggers the
     * creation of multiple blocks(e.g. placing a bed block). The block returned
     * by {@link #state} and its related methods is the block where
     * the placed block would exist if the placement only affected a single
     * block.
     */
    @Cancelable
    public static class MultiPlaceEvent extends PlaceEvent
    {
        private final List<BlockSnapshot> blockSnapshots;

        public MultiPlaceEvent(List<BlockSnapshot> blockSnapshots, IBlockState placedAgainst, EntityPlayer player)
        {
            super(blockSnapshots.get(0), placedAgainst, player);
            this.blockSnapshots = ImmutableList.copyOf(blockSnapshots);
            if (DEBUG)
            {
                System.out.printf("Created MultiPlaceEvent - [PlacedAgainst: %s ][ItemInHand: %s ][Player: %s ]\n", placedAgainst, this.getItemInHand(), player);
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
        private final EnumSet<EnumFacing> notifiedSides;

        public NeighborNotifyEvent(World world, BlockPos pos, IBlockState state, EnumSet<EnumFacing> notifiedSides)
        {
            super(world, pos, state);
            this.notifiedSides = notifiedSides;
        }

        /**
         * Gets a list of directions from the base block that updates will occur upon.
         *
         * @return list of notified directions
         */
        public EnumSet<EnumFacing> getNotifiedSides()
        {
            return notifiedSides;
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
        public CreateFluidSourceEvent(World world, BlockPos pos, IBlockState state)
        {
            super(world, pos, state);
        }
    }

}
