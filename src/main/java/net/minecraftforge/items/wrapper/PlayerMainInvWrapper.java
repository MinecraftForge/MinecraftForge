/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.wrapper;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Exposes the player inventory WITHOUT the armor inventory as IItemHandler.
 * Also takes core of inserting/extracting having the same logic as picking up items.
 */
public class PlayerMainInvWrapper extends RangedWrapper
{
    private final PlayerInventory inventoryPlayer;

    public PlayerMainInvWrapper(PlayerInventory inv)
    {
        super(new InvWrapper(inv), 0, inv.mainInventory.size());
        inventoryPlayer = inv;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        ItemStack rest = super.insertItem(slot, stack, simulate);
        if (rest.getCount()!= stack.getCount())
        {
            // the stack in the slot changed, animate it
            ItemStack inSlot = getStackInSlot(slot);
            if(!inSlot.isEmpty())
            {
                if (getInventoryPlayer().player.world.isRemote)
                {
                    inSlot.setAnimationsToGo(5);
                }
                else if(getInventoryPlayer().player instanceof ServerPlayerEntity) {
                    getInventoryPlayer().player.openContainer.detectAndSendChanges();
                }
            }
        }
        return rest;
    }

    public PlayerInventory getInventoryPlayer()
    {
        return inventoryPlayer;
    }
}
