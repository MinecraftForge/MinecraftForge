package net.minecraftforge.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class DefaultLinearInventorySlot implements ILinearInventorySlot {

    private ItemStack contents;

    private int maxStackSize = 64;

    @Override
    public boolean canExtractItems()
    {
        return contents != null;
    }

    @Override
    public ItemStack getStack()
    {
        return contents;
    }

    @Override
    public boolean setStack(ItemStack is, boolean simulate)
    {
        if (!simulate) contents = is;
        return true;
    }

    @Override
    public int getMaximumStackSize()
    {
        return maxStackSize;
    }

    @Override
    public boolean canInsertItem(ItemStack is)
    {
        return true;
    }

    public void setMaximumStackSize(int maxStackSize) throws IllegalArgumentException
    {
        if (maxStackSize < 0 || maxStackSize > 64) throw new IllegalArgumentException("Max stack size " + maxStackSize + " out of range.");

        this.maxStackSize = maxStackSize;
    }

    public void readFromNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("id"))
            contents = ItemStack.loadItemStackFromNBT(tag);
        else
            contents = null;
    }

    public void writeToNBT(NBTTagCompound tag)
    {
        if (contents != null) contents.writeToNBT(tag);
    }
}
