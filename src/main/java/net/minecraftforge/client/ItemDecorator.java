package net.minecraftforge.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.stringtemplate.v4.ST;

import javax.annotation.Nullable;

/**
 * An Object that is used by the {@Link: net.minecraftforge.items.ItemDecoratorHandler} capability.
 */
public abstract class ItemDecorator{
    private final ResourceLocation key;
    public ItemDecorator(ResourceLocation key)
    {
        this.key = key;
    }

    /**
     * Is passed the parameters from {@link ItemRenderer#renderGuiItemDecorations(net.minecraft.client.gui.Font, net.minecraft.world.item.ItemStack, int, int, java.lang.String)}
     */
    public void render(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String stackSizeLabel)
    {}
    
    public ResourceLocation getKey() {
        return key;
    }
}
