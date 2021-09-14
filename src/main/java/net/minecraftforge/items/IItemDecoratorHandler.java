package net.minecraftforge.items;

import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ItemDecorator;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public interface IItemDecoratorHandler extends INBTSerializable<CompoundTag>
{
    void addDecoration(ItemDecorator decorator, ItemStack stack);
    void removeDecoration(ItemDecorator decorator, ItemStack stack);
    @OnlyIn(Dist.CLIENT)
    void render(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String itemCountLabel);
}
