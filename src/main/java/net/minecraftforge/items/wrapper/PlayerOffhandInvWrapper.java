package net.minecraftforge.items.wrapper;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class PlayerOffhandInvWrapper extends InvWrapper
{
    public final InventoryPlayer inventoryPlayer;
    private final int offset;

    public PlayerOffhandInvWrapper(InventoryPlayer inv)
    {
        super(inv);

        inventoryPlayer = inv;
        offset = inventoryPlayer.mainInventory.length + inventoryPlayer.armorInventory.length;
    }

    @Override
    public int getSlots()
    {
        return inventoryPlayer.offHandInventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return super.getStackInSlot(slot + offset);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        return super.insertItem(slot + offset, stack, simulate);
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
