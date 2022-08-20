/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.wrapper;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerArmorInvWrapper extends RangedWrapper
{
    private final Inventory inventoryPlayer;

    public PlayerArmorInvWrapper(Inventory inv)
    {
        super(new InvWrapper(inv), inv.items.size(), inv.items.size() + inv.armor.size());
        inventoryPlayer = inv;
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty() || !isItemValid(slot, stack))
            return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        if (slot >= inventoryPlayer.armor.size() || !super.isItemValid(slot, stack) || stack.isEmpty())
            return false;

        for (EquipmentSlot s : EquipmentSlot.values())
            if (s.getType() == EquipmentSlot.Type.ARMOR && s.getIndex() == slot)
                return stack.canEquip(s, inventoryPlayer.player);

        return false;
    }

    public Inventory getInventoryPlayer()
    {
        return inventoryPlayer;
    }
}
