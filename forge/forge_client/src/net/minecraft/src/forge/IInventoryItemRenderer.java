package net.minecraft.src.forge;

import net.minecraft.src.RenderBlocks;

public interface IInventoryItemRenderer
{
    public void renderInventoryItem(RenderBlocks render, int itemID, int metadata);
}
