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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemTransferEvent;
import net.minecraftforge.items.filter.SimpleStackFilter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.OptionalInt;

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
        Pair<IItemHandler, TileEntity> itemHandlerResult = getItemHandler(dest, EnumFacing.UP);
        if (itemHandlerResult == null)
            return null;

        IItemHandler handler = itemHandlerResult.getKey();

        TileEntity tileEntity = dest instanceof TileEntity ? (TileEntity) dest : null;

        boolean cancelled = MinecraftForge.EVENT_BUS.post(new ItemTransferEvent.PRE(handler, itemHandlerResult.getValue(), EnumFacing.UP, tileEntity, ItemTransferEvent.flow.EXTRACT));

        if (cancelled)
        {
            if (tileEntity != null && tileEntity instanceof TileEntityHopper)
                ((TileEntityHopper) tileEntity).setTransferCooldown(8);
            return false;
        }

        for (int i = 0; i < dest.getSizeInventory(); i++)
        {
            ItemStack extract;
            ItemStack stackInHopper = dest.getStackInSlot(i);

            if (!handler.canExtractStack(stackInHopper))
                return false;

            if (stackInHopper.getCount() >= stackInHopper.getMaxStackSize())
                continue;

            if (stackInHopper.isEmpty())
                extract = handler.extract(OptionalInt.empty(), stack -> true, 1, false);
            else extract = handler.extract(OptionalInt.empty(), new SimpleStackFilter(stackInHopper), 1, false);
            if (!extract.isEmpty())
            {
                dest.markDirty();
                if (stackInHopper.isEmpty())
                {
                    dest.setInventorySlotContents(i, extract);
                    dest.markDirty();
                }
                else
                {
                    stackInHopper.grow(1);
                    dest.markDirty();
                }

                MinecraftForge.EVENT_BUS.post(new ItemTransferEvent.POST(handler, itemHandlerResult.getValue(), EnumFacing.UP, tileEntity, ItemTransferEvent.flow.EXTRACT, extract));

                return true;
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
        Pair<IItemHandler, TileEntity> destinationResult = getItemHandler(world, (double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ(), enumfacing.getOpposite());
        if (destinationResult == null)
        {
            return true;
        }
        else
        {
            IItemHandler itemHandler = destinationResult.getKey();
            if (MinecraftForge.EVENT_BUS.post(new ItemTransferEvent.PRE(itemHandler, destinationResult.getValue(), enumfacing.getOpposite(), dropper, ItemTransferEvent.flow.INSERT)))
                return false;

            ItemStack stackToInsert = ItemHandlerHelper.copyStackWithSize(stack, 1);

            ItemStack remainder = itemHandler.insert(OptionalInt.empty(), ItemHandlerHelper.copyStackWithSize(stack, 1), false);
            if (remainder.getCount() != stack.getCount())
            {
                dropper.decrStackSize(slot, 1);
                MinecraftForge.EVENT_BUS.post(new ItemTransferEvent.POST(itemHandler, destinationResult.getValue(), enumfacing.getOpposite(), dropper, ItemTransferEvent.flow.INSERT, stackToInsert));
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
        Pair<IItemHandler, TileEntity> destinationResult = getItemHandler(hopper, hopperFacing);
        if (destinationResult == null)
        {
            return false;
        }
        else
        {
            IItemHandler itemHandler = destinationResult.getKey();
            boolean inventoryWasEmpty = itemHandler.isEmpty();
            TileEntity destination = destinationResult.getValue();
            if (itemHandler.isFull())
            {
                return false;
            }
            else
            {
                if (MinecraftForge.EVENT_BUS.post(new ItemTransferEvent.PRE(itemHandler, destination, hopperFacing, hopper, ItemTransferEvent.flow.INSERT)))
                    return false;

                for (int i = 0; i < hopper.getSizeInventory(); ++i)
                {
                    ItemStack stackInHopper = hopper.getStackInSlot(i);
                    if (!stackInHopper.isEmpty())
                    {
                        ItemStack toInsert = ItemHandlerHelper.copyStackWithSize(stackInHopper, 1);

                        ItemStack insert = itemHandler.insert(OptionalInt.empty(), toInsert, false);
                        if (insert.isEmpty())
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

                            MinecraftForge.EVENT_BUS.post(new ItemTransferEvent.POST(itemHandler, destination, hopperFacing, hopper, ItemTransferEvent.flow.INSERT, toInsert));

                            return true;

                        }
                    }
                }
                return false;
            }
        }
    }

    @Nullable
    private static Pair<IItemHandler, TileEntity> getItemHandler(IHopper hopper, EnumFacing hopperFacing)
    {
        double x = hopper.getXPos() + (double) hopperFacing.getFrontOffsetX();
        double y = hopper.getYPos() + (double) hopperFacing.getFrontOffsetY();
        double z = hopper.getZPos() + (double) hopperFacing.getFrontOffsetZ();
        return getItemHandler(hopper.getWorld(), x, y, z, hopperFacing.getOpposite());
    }

    @Nullable
    public static Pair<IItemHandler, TileEntity> getItemHandler(World worldIn, double x, double y, double z, final EnumFacing side)
    {
        Pair<IItemHandler, TileEntity> destination = null;
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
