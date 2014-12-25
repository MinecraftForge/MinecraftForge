package net.minecraftforge.client.model;

import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;

/**
 * An extension of {@link IBakedModel} that allows defining of a custom item rendering method.
 */
public interface IDynamicItemModel extends IBakedModel
{
    /**
     * Called by {@link RenderItem#renderItem} to render the passed in ItemStack
     * @param stack The ItemStack to render
     */
    void render(ItemStack stack);
}
