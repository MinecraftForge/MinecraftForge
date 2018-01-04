package net.minecraftforge.client.gui.tabs;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import jline.internal.Nullable;

public interface IGuiTabAttachable
{
    /**
     * @return the name for the main gui tab
     */
    default String getDisplayName()
    {
        return "default";
    }

    /**
     * @return the icon itemstack for the main gui tab
     */
    default ItemStack getIconStack()
    {
        return getIconTexture() == null ? new ItemStack(Blocks.CRAFTING_TABLE) : ItemStack.EMPTY;
    }

    /**
     * @return the icon texture for the main gui tab
     */
    @Nullable
    default ResourceLocation getIconTexture()
    {
        return null;
    }

    default GuiContainer getMainGuiContainer()
    {
        return (GuiContainer) this;
    }
}
