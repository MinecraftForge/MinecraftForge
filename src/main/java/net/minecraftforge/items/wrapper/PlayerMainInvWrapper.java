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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.OptionalInt;

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
    public ItemStack insert(OptionalInt slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (simulate)
            return super.insert(slot, stack, true);
        if (slot.isPresent()){

           ItemStack remainder = super.insert(slot, stack, false);

           if (remainder.getCount() != stack.getCount())
               setAnimationsToGo(getStackInSlot(slot.getAsInt()));
        }
        for (int i = 0; i < size(); i++)
        {
            ItemStack remainder = super.insert(OptionalInt.of(i), stack, false);
            if (remainder.getCount() != stack.getCount()){
                setAnimationsToGo(getStackInSlot(i));
                return remainder;
            }
        }
        return ItemStack.EMPTY;
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
