package net.minecraft.src.forge;

import net.minecraft.src.EntityItem;
import net.minecraft.src.RenderBlocks;

public interface IEntityItemRenderer
{
    public void renderEntityItem(RenderBlocks render, EntityItem item, int itemID, int metadata);
}
