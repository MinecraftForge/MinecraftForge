package net.minecraft.src.forge;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.RenderBlocks;

public interface IEquippedItemRenderer
{
    public void renderEquippedItem(RenderBlocks render, EntityLiving entity, int itemID, int metadata);
}
