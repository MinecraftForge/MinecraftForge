package net.minecraftforge.crafting;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

public interface ICraftingMaterial {

    /**
     * Checks whether the argument ItemStack matches this instance.
     * 
     * @param other
     *            ItemStack to compare.
     * @return Returns true if the argument is one of the items that this instance represents.
     */
    boolean itemMatches(ItemStack other);

    /**
     * Makes a list of ItemStacks that this instance represents.
     * 
     * @return
     */
    ArrayList<ItemStack> getItems();
}
