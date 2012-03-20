package net.minecraft.src.forge;

import net.minecraft.src.RenderBlocks;

@Deprecated //Deprecated in favor of new more Robust IItemRenderer, Remove in next MC version
public interface ICustomItemRenderer
{
    @Deprecated
    public void renderInventory(RenderBlocks render, int itemID, int metadata);
}