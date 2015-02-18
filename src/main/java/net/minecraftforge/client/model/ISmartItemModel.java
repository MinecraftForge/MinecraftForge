package net.minecraftforge.client.model;

import net.minecraft.item.ItemStack;
import net.minecraft.client.resources.model.IBakedModel;

public interface ISmartItemModel extends IBakedModel
{
    IBakedModel handleItemState(ItemStack stack);
}
