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

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.OptionalInt;

public class SlotItemHandler extends Slot
{
    private final IItemHandler handler;
    private IItemHandler itemHandler;

    public SlotItemHandler(IItemHandler handler, int index, int xPosition, int yPosition)
    {
        super(ItemHandlerHelper.emptyInventory, index, xPosition, yPosition);
        this.handler = handler;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack)
    {
        return handler.isStackValidForSlot(getSlotIndex(), stack);
    }

    @Override
    @Nonnull
    public ItemStack getStack()
    {
        return handler.getStackInSlot(getSlotIndex());
    }

    @Override
    public void putStack(@Nonnull ItemStack stack)
    {
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(getSlotIndex(), stack);
        this.onSlotChanged();

    }

    @Override
    public int getSlotStackLimit()
    {
        return itemHandler.getSlotLimit(getSlotIndex());
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return itemHandler.getStackLimit(stack, getSlotIndex());
    }

    @Override
    public boolean isHere(IInventory inv, int slotIn)
    {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int amount)
    {
        return itemHandler.extract(OptionalInt.of(getSlotIndex()), stack -> true, amount, false);
    }

    @Override
    public boolean isSameInventory(Slot other)
    {
        return other instanceof SlotItemHandler && ((SlotItemHandler) other).getItemHandler() == this.itemHandler;
    }

    public IItemHandler getItemHandler()
    {
        return itemHandler;
    }
}
