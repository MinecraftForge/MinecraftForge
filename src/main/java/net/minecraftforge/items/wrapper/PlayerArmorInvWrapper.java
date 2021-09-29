/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class PlayerArmorInvWrapper extends RangedWrapper
{
    private final Inventory inventoryPlayer;

    public PlayerArmorInvWrapper(Inventory inv)
    {
        super(new InvWrapper(inv), inv.items.size(), inv.items.size() + inv.armor.size());
        inventoryPlayer = inv;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        EquipmentSlot equ = null;
        for (EquipmentSlot s : EquipmentSlot.values())
        {
            if (s.getType() == EquipmentSlot.Type.ARMOR && s.getIndex() == slot)
            {
                equ = s;
                break;
            }
        }
        // check if it's valid for the armor slot
        if (equ != null && slot < 4 && !stack.isEmpty() && stack.canEquip(equ, getInventoryPlayer().player))
        {
            return super.insertItem(slot, stack, simulate);
        }
        return stack;
    }

    public Inventory getInventoryPlayer()
    {
        return inventoryPlayer;
    }
}
