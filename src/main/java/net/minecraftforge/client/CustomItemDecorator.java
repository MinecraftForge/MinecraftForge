package net.minecraftforge.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

/**
 * An Object that is used by the CustomItemDecoration capability.
 * Please try to add only one decorator per mod, to keep NBT size to a minimum.
 */
public abstract class CustomItemDecorator {
    public final String key;

    public CustomItemDecorator(ResourceLocation key)
    {
        this.key = key.toString();
    }

    /**
     * Is passed the parameters from {@link ItemRenderer#renderGuiItemDecorations(net.minecraft.client.gui.Font, net.minecraft.world.item.ItemStack, int, int, java.lang.String)}
     */
    public abstract void render(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String stackSizeLabel);
}
