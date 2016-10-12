package net.minecraftforge.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class SlotItemHandler extends Slot {
    private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
    public final IItemHandler itemHandler;
    private final EnumFacing side;

    public SlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, EnumFacing side)
    {
        super(emptyInventory, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.side = side;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        if (stack == null)
            return false;
        ItemStack remainder = this.itemHandler.insertItem(slotNumber, stack, side, true);
        return remainder == null || remainder.stackSize < stack.stackSize;
    }

    @Override
    public ItemStack getStack()
    {
        return this.itemHandler.getStackInSlot(slotNumber, side);
    }

    @Override
    public void putStack(ItemStack stack)
    {
        this.itemHandler.insertItem(slotNumber, stack, side, false);
        this.onSlotChanged();
    }

    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_)
    {

    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        ItemStack maxAdd = stack.copy();
        maxAdd.stackSize = maxAdd.getMaxStackSize();
        ItemStack remainder = this.itemHandler.insertItem(slotNumber, maxAdd, side, true);

        ItemStack currentStack = this.itemHandler.getStackInSlot(slotNumber, side);

        int current = currentStack == null ? 0 : currentStack.stackSize;
        int added = maxAdd.stackSize - remainder.stackSize;
        return current + added;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return this.itemHandler.extractItem(slotNumber, 1, side, true) != null;
    }

    @Override
    public ItemStack decrStackSize(int amount)
    {
        return this.itemHandler.extractItem(slotNumber, amount, side, false);
    }
}
