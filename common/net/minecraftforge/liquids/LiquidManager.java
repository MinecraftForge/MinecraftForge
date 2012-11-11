package net.minecraftforge.liquids;

import java.util.LinkedList;

import net.minecraft.src.ItemStack;

public class LiquidManager {
	
	public static final int BUCKET_VOLUME = 1000;
	public static LinkedList<LiquidData> liquids = new LinkedList<LiquidData>();
	
	public static LiquidStack getLiquidForFilledItem(ItemStack filledItem) {
		if (filledItem == null)
			return null;

		for (LiquidData liquid : liquids)
			if (liquid.filled.isItemEqual(filledItem))
				return liquid.stillLiquid;

		return null;
	}
	
	public static int getLiquidIDForFilledItem(ItemStack filledItem) {
		LiquidStack liquidForFilledItem = getLiquidForFilledItem(filledItem);
		
		if (liquidForFilledItem == null)
			return 0;
		
		return liquidForFilledItem.itemID;
	}

	public static ItemStack getFilledItemForLiquid(LiquidStack liquid) {
		for (LiquidData data : liquids)
			if(data.stillLiquid.isLiquidEqual(liquid))
				return data.filled.copy();

		return null;
	}
	
	public static ItemStack fillLiquidContainer(int liquidId, int quantity, ItemStack emptyContainer) {
		return fillLiquidContainer(new LiquidStack(liquidId, quantity, 0), emptyContainer);
	}
	
	public static ItemStack fillLiquidContainer(LiquidStack liquid, ItemStack emptyContainer) {
		for(LiquidData data : liquids)
			if(liquid.containsLiquid(data.stillLiquid)
					&& data.container.isItemEqual(emptyContainer))
				return data.filled.copy();
        return null;
	}

	public static boolean isLiquid(ItemStack block) {
		if (block.itemID == 0)
			return false;

		for (LiquidData liquid : liquids)
			if (liquid.stillLiquid.isLiquidEqual(block) || liquid.movingLiquid.isLiquidEqual(block))
				return true;

		return false;
	}


}
