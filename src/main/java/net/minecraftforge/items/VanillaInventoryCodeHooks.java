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

import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockHopper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class VanillaInventoryCodeHooks
{
    //Return: Null if we did nothing {no IItemHandler}, True if we moved an item, False if we moved no items
    public static Boolean extractHook(IHopper dest)
    {
        TileEntity tileEntity = dest.getWorld().getTileEntity(new BlockPos(dest.getXPos(), dest.getYPos() + 1, dest.getZPos()));

        if (tileEntity == null || !tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN))
            return null;

        IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);

        for (int i = 0; i < handler.getSlots(); i++)
        {
            ItemStack extractItem = handler.extractItem(i, 1, true);
            if (!extractItem.func_190926_b())
            {
                for (int j = 0; j < dest.getSizeInventory(); j++)
                {
                    ItemStack destStack = dest.getStackInSlot(j);
                    if (destStack.func_190926_b() || destStack.func_190916_E() < destStack.getMaxStackSize() && destStack.func_190916_E() < dest.getInventoryStackLimit() && ItemHandlerHelper.canItemStacksStack(extractItem, destStack))
                    {
                        extractItem = handler.extractItem(i, 1, false);
                        if (destStack.func_190926_b())
                            dest.setInventorySlotContents(j, extractItem);
                        else
                        {
                            destStack.func_190917_f(1);
                            dest.setInventorySlotContents(j, destStack);
                        }
                        dest.markDirty();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean dropperInsertHook(World world, BlockPos pos, TileEntityDispenser dropper, int slot, @Nonnull ItemStack stack)
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

        if (result.func_190926_b())
        {
            result = stack.copy();
            result.func_190918_g(1);
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
            if (stackInSlot.func_190926_b())
            {
                ItemStack insert = stackInSlot.copy();
                insert.func_190920_e(1);
                ItemStack newStack = ItemHandlerHelper.insertItem(handler, insert, true);
                if (newStack.func_190926_b())
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
