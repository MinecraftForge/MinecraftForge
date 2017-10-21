package net.minecraftforge.common.crafting;

import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;

/**
 * This interface is to be implemented on Container objects.
 * For GUIs with recipe books, this allows their containers to have
 * recipe completion and ghost recipes in their craft matrices.
 */
public interface IRecipeContainer {
    public InventoryCraftResult getCraftResult();

    public InventoryCrafting getCraftMatrix();
}
