package net.minecraftforge.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IItemHandler {
	int getSlots(EnumFacing side);

	ItemStack getStackInSlot(int slot, EnumFacing side);

	ItemStack insertItem(int slot, ItemStack stack, EnumFacing side, boolean simulate);

	ItemStack extractItem(int slot, int amount, EnumFacing side, boolean simulate);
}
