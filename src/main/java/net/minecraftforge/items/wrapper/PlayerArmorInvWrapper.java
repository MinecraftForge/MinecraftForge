/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class PlayerArmorInvWrapper extends RangedWrapper
{
    private final InventoryPlayer inventoryPlayer;

    public PlayerArmorInvWrapper(InventoryPlayer inv)
    {
        super(new InvWrapper(inv), inv.mainInventory.size(), inv.mainInventory.size() + inv.armorInventory.size());
        inventoryPlayer = inv;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        EntityEquipmentSlot equ = null;
        for (EntityEquipmentSlot s : EntityEquipmentSlot.values())
        {
            if (s.getSlotType() == EntityEquipmentSlot.Type.ARMOR && s.getIndex() == slot)
            {
                equ = s;
                break;
            }
        }
        // check if it's valid for the armor slot
        if (equ != null && slot < 4 && !stack.isEmpty() && stack.getItem().isValidArmor(stack, equ, getInventoryPlayer().player))
        {
            return super.insertItem(slot, stack, simulate);
        }
        return stack;
    }

    public InventoryPlayer getInventoryPlayer()
    {
        return inventoryPlayer;
    }
}
