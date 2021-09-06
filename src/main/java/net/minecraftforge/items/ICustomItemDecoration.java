package net.minecraftforge.items;

import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public interface ICustomItemDecoration extends INBTSerializable<CompoundTag>
{
    void addDecoration(ResourceLocation key, ItemStack stack);
    void removeDecoration(ResourceLocation key, ItemStack stack);
    @OnlyIn(Dist.CLIENT)
    void render(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String itemCountLabel);
}