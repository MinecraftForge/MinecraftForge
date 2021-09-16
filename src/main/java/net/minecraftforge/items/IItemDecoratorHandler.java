package net.minecraftforge.items;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ItemDecorator;

import javax.annotation.Nullable;

public interface IItemDecoratorHandler
{
    void addDecorator(ItemDecorator decorator);
    void removeDecorator(ItemDecorator decorator);
    @OnlyIn(Dist.CLIENT)
    void render(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String itemCountLabel);
}
