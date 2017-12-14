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

package net.minecraftforge.items.wrapper;

import com.google.common.collect.Range;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.InsertTransaction;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

/**
 * Exposes the player inventory WITHOUT the armor inventory as IItemHandler.
 * Also takes core of inserting/extracting having the same logic as picking up items.
 */
public class PlayerMainInvWrapper extends RangedWrapper
{
    private final InventoryPlayer inventoryPlayer;

    public PlayerMainInvWrapper(InventoryPlayer inv)
    {
        super(new InvWrapper(inv), 0, inv.mainInventory.size());
        inventoryPlayer = inv;
    }


    @Nonnull
    @Override
    public InsertTransaction insert(Range<Integer> slotRange, ItemStack stack, boolean simulate)
    {
        if (ItemHandlerHelper.isRangeSingleton(slotRange))
        {
            int slot = slotRange.lowerEndpoint();
            ItemStack existing = getStackInSlot(slot);
            if (existing.isEmpty() || ItemHandlerHelper.canItemStacksStack(existing, stack))
            {
                InsertTransaction transaction = ItemHandlerHelper.split(stack, ItemHandlerHelper.getFreeSpaceForSlot(this, slot));
                if (!transaction.getInsertedStack().isEmpty())
                {
                    if (simulate)
                        return transaction;
                    else
                    {
                        ItemStack InsertedStack = transaction.getInsertedStack();
                        getInventoryPlayer().setInventorySlotContents(slot, InsertedStack);
                        getInventoryPlayer().markDirty();
                        setAnimationsToGo(InsertedStack);
                        return transaction;
                    }
                }
            }
        }
        else
        {
            InsertTransaction transaction;
            transaction = insert(slotRange, stack, simulate, true);
            if (!transaction.getInsertedStack().isEmpty())
                return transaction;
            else
            {
                transaction = insert(slotRange, stack, simulate, false);
                return transaction;
            }
        }
        return new InsertTransaction(ItemStack.EMPTY, stack);
    }

    protected InsertTransaction insert(Range<Integer> slotRange, ItemStack stack, boolean simulate, boolean firstRun)
    {
        int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
        int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());

        for (int i = minSlot; i < maxSlot; i++)
        {
            ItemStack existing = getStackInSlot(i);
            if (existing.isEmpty() && firstRun) continue;

            InsertTransaction transaction = ItemHandlerHelper.split(stack, ItemHandlerHelper.getFreeSpaceForSlot(this, i));
            if (!transaction.getInsertedStack().isEmpty())
            {
                if (simulate)
                    return transaction;
                else
                {
                    if (existing.isEmpty())
                    {
                        ItemStack insertedStack = transaction.getInsertedStack();
                        getInventoryPlayer().setInventorySlotContents(i, insertedStack);
                        getInventoryPlayer().markDirty();
                        setAnimationsToGo(insertedStack);
                    }
                    else
                    {
                        existing.grow(transaction.getInsertedStack().getCount());
                        getInventoryPlayer().markDirty();
                        setAnimationsToGo(existing);
                    }
                    return transaction;
                }
            }
        }
        return new InsertTransaction(ItemStack.EMPTY, stack);
    }

    protected void setAnimationsToGo(ItemStack stack)
    {
        if (getInventoryPlayer().player.world.isRemote)
        {
            stack.setAnimationsToGo(5);
        }
        else if (getInventoryPlayer().player instanceof EntityPlayerMP)
        {
            getInventoryPlayer().player.openContainer.detectAndSendChanges();
        }
    }


    public InventoryPlayer getInventoryPlayer()
    {
        return inventoryPlayer;
    }
}
