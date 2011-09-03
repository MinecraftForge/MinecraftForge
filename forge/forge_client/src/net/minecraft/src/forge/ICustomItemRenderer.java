package net.minecraft.src.forge;

import net.minecraft.src.RenderBlocks;

public interface ICustomItemRenderer {
	public void renderInventory (RenderBlocks renderblocks, int itemID, int meta);
}
