package net.minecraft.src.forge;

import net.minecraft.src.RenderBlocks;

public interface IEquippedItemRenderer
{
    public void renderEquippedItem(RenderBlocks render, int itemID, int metadata);
}
