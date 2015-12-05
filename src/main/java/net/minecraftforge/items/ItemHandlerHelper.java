package net.minecraftforge.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class ItemHandlerHelper {
	public static ItemStack insertItem(IItemHandler dest, ItemStack stack, EnumFacing side, boolean simulate) {
		if(dest == null || stack == null) return stack;

		for (int i = 0; i < dest.getSlots(side) && stack != null; i++) {
			stack = dest.insertItem(i, stack, side, simulate);
		}

		return stack;
	}

	public static boolean canItemsStack(ItemStack a, ItemStack b) {
		return (a != null && b != null) && (a.isItemEqual(b)) && (!(a.getTagCompound() == null && b.getTagCompound() != null) && (a.getTagCompound() == null || a.getTagCompound().equals(b.getTagCompound())));
	}
}
