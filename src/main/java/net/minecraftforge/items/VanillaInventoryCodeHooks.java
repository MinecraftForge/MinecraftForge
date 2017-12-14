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

package net.minecraftforge.items;

import com.google.common.collect.Range;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockHopper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VanillaInventoryCodeHooks
{
    /**
     * Copied from TileEntityHopper#captureDroppedItems and added capability support
     *
     * @return Null if we did nothing {no IItemHandler}, True if we moved an item, False if we moved no items
     */
    @Nullable
    public static Boolean extractHook(IHopper dest)
    {
        Pair<IItemHandler, Object> itemHandlerResult = getItemHandler(dest, EnumFacing.UP);
        if (itemHandlerResult == null)
            return null;

        IItemHandler handler = itemHandlerResult.getKey();


        for (int i = 0; i < dest.getSizeInventory(); i++)
        {
            ItemStack extract;
            ItemStack stackInHopper = dest.getStackInSlot(i);

            if (!handler.canExtractStack(stackInHopper))
                return false;

            if (stackInHopper.getCount() >= stackInHopper.getMaxStackSize())
                continue;

            if (stackInHopper.isEmpty())
                extract = handler.extract(Range.all(), stack -> true, 1, false);
            else extract = handler.extractStack(Range.all(), stackInHopper, true, true, 1, false);
            if (!extract.isEmpty())
            {
                dest.markDirty();
                if (stackInHopper.isEmpty())
                {
                    dest.setInventorySlotContents(i, extract);
                    dest.markDirty();
                    return true;
                }
                else
                {
                    stackInHopper.grow(1);
                    dest.markDirty();
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Copied from BlockDropper#dispense and added capability support
     */
    public static boolean dropperInsertHook(World world, BlockPos pos, TileEntityDispenser dropper, int slot, @Nonnull ItemStack stack)
    {
        EnumFacing enumfacing = world.getBlockState(pos).getValue(BlockDropper.FACING);
        BlockPos blockpos = pos.offset(enumfacing);
        Pair<IItemHandler, Object> destinationResult = getItemHandler(world, (double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ(), enumfacing.getOpposite());
        if (destinationResult == null)
        {
            return true;
        }
        else
        {
            IItemHandler itemHandler = destinationResult.getKey();

            InsertTransaction transaction = itemHandler.insert(Range.all(), stack.copy().splitStack(1), false);
            if (!transaction.getInsertedStack().isEmpty())
            {
                dropper.decrStackSize(slot, 1);
            }
        }
        return false;
    }

    /**
     * Copied from TileEntityHopper#transferItemsOut and added capability support
     */
    public static boolean insertHook(TileEntityHopper hopper)
    {
        EnumFacing hopperFacing = BlockHopper.getFacing(hopper.getBlockMetadata());
        Pair<IItemHandler, Object> destinationResult = getItemHandler(hopper, hopperFacing);
        if (destinationResult == null)
        {
            return false;
        }
        else
        {
            IItemHandler itemHandler = destinationResult.getKey();
            boolean inventoryWasEmpty = itemHandler.isEmpty();
            Object destination = destinationResult.getValue();
            if (itemHandler.isFull())
            {
                return false;
            }
            else
            {
                for (int i = 0; i < hopper.getSizeInventory(); ++i)
                {
                    ItemStack stackInHopper = hopper.getStackInSlot(i);
                    if (!stackInHopper.isEmpty())
                    {
                        InsertTransaction transaction = itemHandler.insert(Range.all(), stackInHopper.copy().splitStack(1), false);
                        if (!transaction.getInsertedStack().isEmpty())
                        {
                            hopper.decrStackSize(i, 1);

                            if (inventoryWasEmpty && destination instanceof TileEntityHopper)
                            {
                                TileEntityHopper destinationHopper = (TileEntityHopper) destination;

                                if (!destinationHopper.mayTransfer())
                                {
                                    int k = 0;

                                    if (destinationHopper.getLastUpdateTime() >= (hopper.getLastUpdateTime()))
                                    {
                                        k = 1;
                                    }

                                    destinationHopper.setTransferCooldown(8 - k);
                                }
                            }
                            return true;

                        }
                    }
                }

                return false;
            }
        }
    }

    @Nullable
    private static Pair<IItemHandler, Object> getItemHandler(IHopper hopper, EnumFacing hopperFacing)
    {
        double x = hopper.getXPos() + (double) hopperFacing.getFrontOffsetX();
        double y = hopper.getYPos() + (double) hopperFacing.getFrontOffsetY();
        double z = hopper.getZPos() + (double) hopperFacing.getFrontOffsetZ();
        return getItemHandler(hopper.getWorld(), x, y, z, hopperFacing.getOpposite());
    }

    @Nullable
    public static Pair<IItemHandler, Object> getItemHandler(World worldIn, double x, double y, double z, final EnumFacing side)
    {
        Pair<IItemHandler, Object> destination = null;
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(y);
        int k = MathHelper.floor(z);
        BlockPos blockpos = new BlockPos(i, j, k);
        net.minecraft.block.state.IBlockState state = worldIn.getBlockState(blockpos);
        Block block = state.getBlock();

        if (block.hasTileEntity(state))
        {
            TileEntity tileentity = worldIn.getTileEntity(blockpos);
            if (tileentity != null)
            {
                if (tileentity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side))
                {
                    IItemHandler capability = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
                    destination = ImmutablePair.of(capability, tileentity);
                }
            }
        }

        return destination;
    }
}
