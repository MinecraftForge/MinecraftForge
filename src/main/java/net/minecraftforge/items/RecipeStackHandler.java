package net.minecraftforge.items;

import net.minecraft.item.ItemStack;

public class RecipeStackHandler extends ItemStackHandler implements IRecipeInventory
{

    protected int height;
    protected int width;

    public RecipeStackHandler(int height, int width)
    {
        super(height * width);
        this.height = height;
        this.width = width;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        setStackInSlot(index, stack);
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public ItemStack getStack(int index) {
        return getStackInSlot(index);
    }

}
