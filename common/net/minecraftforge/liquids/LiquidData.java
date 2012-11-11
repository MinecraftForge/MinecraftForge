/** 
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 * 
 * BuildCraft is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package net.minecraftforge.liquids;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class LiquidData {

	public final LiquidStack stillLiquid;
	public final LiquidStack movingLiquid;

	public final ItemStack filled;
	public final ItemStack container;

	public LiquidData(int stillLiquidId, int movingLiquidId, Item filled) {
		this(new LiquidStack(stillLiquidId, LiquidManager.BUCKET_VOLUME), new LiquidStack(movingLiquidId, LiquidManager.BUCKET_VOLUME), new ItemStack(filled, 1), new ItemStack(Item.bucketEmpty));
	}

	public LiquidData(int stillLiquidId, int movingLiquidId, ItemStack filled) {
		this(new LiquidStack(stillLiquidId, LiquidManager.BUCKET_VOLUME), new LiquidStack(movingLiquidId, LiquidManager.BUCKET_VOLUME), filled, new ItemStack(Item.bucketEmpty));
	}

	public LiquidData(LiquidStack stillLiquid, ItemStack filled, ItemStack container) {
		this(stillLiquid, stillLiquid, filled, container);
	}
	
	public LiquidData(LiquidStack stillLiquid, LiquidStack movingLiquid, ItemStack filled, ItemStack container) {
		this.stillLiquid = stillLiquid;
		this.movingLiquid = movingLiquid;
		this.filled = filled;
		this.container = container;
		
		if(stillLiquid == null || filled == null || container == null)
			throw new RuntimeException("stillLiquid, filled, or container is null, this is an error");
	}

}
