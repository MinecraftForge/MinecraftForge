package net.minecraftforge.common;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

/**
 * An Object that is used by the {@Link: net.minecraftforge.items.ItemDecoratorHandler} capability.
 * Please try to add only one decorator per mod, to keep NBT size to a minimum.
 */
public class ItemDecorator extends ForgeRegistryEntry<ItemDecorator> {
    public ItemDecorator()
    {}

    /**
     * Is passed the parameters from {@link ItemRenderer#renderGuiItemDecorations(net.minecraft.client.gui.Font, net.minecraft.world.item.ItemStack, int, int, java.lang.String)}
     * Is only called client side
     */
    @OnlyIn(Dist.CLIENT)
    public void render(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String stackSizeLabel)
    {}
}
