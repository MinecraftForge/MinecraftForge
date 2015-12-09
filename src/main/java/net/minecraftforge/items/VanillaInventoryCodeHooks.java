package net.minecraftforge.items;

import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class VanillaInventoryCodeHooks {

    public static boolean extractHook(IHopper dest)
    {
        TileEntity tileEntity = dest.getWorld().getTileEntity(new BlockPos(dest.getXPos(), dest.getYPos() + 1, dest.getZPos()));
        if (!(tileEntity instanceof IItemHandler))
            return false;

        EnumFacing side = EnumFacing.DOWN;
        IItemHandler handler = (IItemHandler) tileEntity;

        for (int i = 0; i < handler.getSlots(side); i++)
        {
            ItemStack extractItem = handler.extractItem(i, 1, side, true);
            if (extractItem != null)
            {
                for (int j = 0; j < dest.getSizeInventory(); j++)
                {
                    ItemStack destStack = dest.getStackInSlot(j);
                    if (destStack == null || destStack.stackSize < destStack.getMaxStackSize() || ItemHandlerHelper.canItemStacksStack(extractItem, destStack))
                    {
                        extractItem = handler.extractItem(i, 1, side, false);
                        if (destStack == null)
                            dest.setInventorySlotContents(j, extractItem);
                        else
                        {
                            destStack.stackSize++;
                            dest.setInventorySlotContents(j, destStack);
                        }
                        dest.markDirty();
                        return true;
                    }
                }
            }
        }

        return true;
    }

    public static boolean dropperInsertHook(World world, BlockPos pos, TileEntityDispenser dropper, int slot, ItemStack stack)
    {
        EnumFacing enumfacing = world.getBlockState(pos).getValue(BlockDropper.FACING);
        BlockPos offsetPos = pos.offset(enumfacing);
        TileEntity tileEntity = world.getTileEntity(offsetPos);
        if (tileEntity == null)
            return false;
        if (!(tileEntity instanceof IItemHandler))
            return true;

        ItemStack result = ItemHandlerHelper.insertItem((IItemHandler) tileEntity, stack.copy().splitStack(1), enumfacing.getOpposite(), false);

        if (result == null)
        {
            result = stack.copy();

            if (--result.stackSize == 0)
            {
                result = null;
            }
        }
        else
        {
            result = stack.copy();
        }
        dropper.setInventorySlotContents(slot, result);
        dropper.markDirty();
        return false;
    }

    public static boolean insertHook(TileEntityHopper hopper)
    {
        return insertHook(hopper, BlockHopper.getFacing(hopper.getBlockMetadata()));
    }

    public static boolean insertHook(IHopper hopper, EnumFacing facing)
    {
        TileEntity tileEntity = hopper.getWorld().getTileEntity(
                new BlockPos(hopper.getXPos() + facing.getFrontOffsetX(), hopper.getYPos() + facing.getFrontOffsetY(),
                        hopper.getZPos() + facing.getFrontOffsetZ()));

        if (!(tileEntity instanceof IItemHandler))
            return false;

        EnumFacing side = facing.getOpposite();
        IItemHandler handler = (IItemHandler) tileEntity;

        for (int i = 0; i < hopper.getSizeInventory(); i++)
        {
            ItemStack stackInSlot = hopper.getStackInSlot(i);
            if (stackInSlot != null)
            {
                ItemStack insert = stackInSlot.copy();
                insert.stackSize = 1;
                ItemStack newStack = ItemHandlerHelper.insertItem(handler, insert, side, true);
                if (newStack == null || newStack.stackSize == 0)
                {
                    ItemHandlerHelper.insertItem(handler, hopper.decrStackSize(i, 1), side, false);
                    hopper.markDirty();
                    return true;
                }
            }
        }

        return true;
    }

    public static ItemStack extractItem(IInventory inventory, int slot, int amount, EnumFacing side, boolean simulate)
    {
        if (inventory instanceof ISidedInventory)
        {
            return SidedInvWrapper.extractItem((ISidedInventory) inventory, slot, amount, side, simulate);
        }
        else
        {
            return InvWrapper.extractItem(inventory, slot, amount, side, simulate);
        }
    }

    public static int getSlots(IInventory inventory, EnumFacing side)
    {
        if (inventory instanceof ISidedInventory)
        {
            return SidedInvWrapper.getSlots((ISidedInventory) inventory, side);
        }
        else
        {
            return InvWrapper.getSlots(inventory, side);
        }
    }

    public static ItemStack insertItem(IInventory inventory, int slot, ItemStack stack, EnumFacing side, boolean simulate)
    {
        if (inventory instanceof ISidedInventory)
        {
            return SidedInvWrapper.insertItem((ISidedInventory) inventory, slot, stack, side, simulate);
        }
        else
        {
            return InvWrapper.insertItem(inventory, slot, stack, side, simulate);
        }
    }

    public static ItemStack getStackInSlot(IInventory inventory, int slot, EnumFacing side)
    {
        if (inventory instanceof ISidedInventory)
        {
            return SidedInvWrapper.getStackInSlot((ISidedInventory) inventory, slot, side);
        }
        else
        {
            return InvWrapper.getStackInSlot(inventory, slot, side);
        }
    }
}
