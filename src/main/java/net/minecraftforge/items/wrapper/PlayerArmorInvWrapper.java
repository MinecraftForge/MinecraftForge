package net.minecraftforge.items.wrapper;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class PlayerArmorInvWrapper extends InvWrapper
{
    public final InventoryPlayer inventoryPlayer;
    public final int offset;

    public PlayerArmorInvWrapper(InventoryPlayer inv)
    {
        super(inv);

        inventoryPlayer = inv;
        offset = inventoryPlayer.mainInventory.length;
    }

    @Override
    public int getSlots()
    {
        return inventoryPlayer.armorInventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return super.getStackInSlot(slot + offset);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        // check if it's valid for the armor slot
        if (slot < 4 && stack != null && stack.getItem().isValidArmor(stack, 3 - slot, inventoryPlayer.player))
        {
            return super.insertItem(slot + offset, stack, simulate);
        }
        return stack;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack)
    {
        super.setStackInSlot(slot + offset, stack);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return super.extractItem(slot + offset, amount, simulate);
    }
}
