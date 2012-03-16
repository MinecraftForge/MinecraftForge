package net.minecraft.src.forge;

import net.minecraft.src.RenderBlocks;

public interface IEntityItemRenderer
{
    public void renderEntityItem(RenderBlocks render, int itemID, int metadata);
}
