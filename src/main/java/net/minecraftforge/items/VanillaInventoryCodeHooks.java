package net.minecraftforge.items;

import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockHopper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class VanillaInventoryCodeHooks
{

    public static boolean extractHook(IHopper dest)
    {
        TileEntity tileEntity = dest.getWorld().getTileEntity(new BlockPos(dest.getXPos(), dest.getYPos() + 1, dest.getZPos()));

        if (tileEntity == null || !tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN))
            return false;

        IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);

        for (int i = 0; i < handler.getSlots(); i++)
        {
            ItemStack extractItem = handler.extractItem(i, 1, true);
            if (extractItem != null)
            {
                for (int j = 0; j < dest.getSizeInventory(); j++)
                {
                    ItemStack destStack = dest.getStackInSlot(j);
                    if (destStack == null || destStack.stackSize < destStack.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(extractItem, destStack))
                    {
                        extractItem = handler.extractItem(i, 1, false);
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
            return true;
        if (!tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, enumfacing.getOpposite()))
            return true;

        IItemHandler capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, enumfacing.getOpposite());

        ItemStack result = ItemHandlerHelper.insertItem(capability, ItemHandlerHelper.copyStackWithSize(stack, 1), false);

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
                new BlockPos(hopper.getXPos(), hopper.getYPos(), hopper.getZPos()).offset(facing));

        if (tileEntity == null)
            return false;
        if (!tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()))
            return false;

        IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());

        for (int i = 0; i < hopper.getSizeInventory(); i++)
        {
            ItemStack stackInSlot = hopper.getStackInSlot(i);
            if (stackInSlot != null)
            {
                ItemStack insert = stackInSlot.copy();
                insert.stackSize = 1;
                ItemStack newStack = ItemHandlerHelper.insertItem(handler, insert, true);
                if (newStack == null || newStack.stackSize == 0)
                {
                    ItemHandlerHelper.insertItem(handler, hopper.decrStackSize(i, 1), false);
                    hopper.markDirty();
                    return true;
                }
            }
        }

        return true;
    }
}
