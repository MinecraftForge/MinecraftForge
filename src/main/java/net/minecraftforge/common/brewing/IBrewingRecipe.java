package net.minecraftforge.common.brewing;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface IBrewingRecipe {

    /**
     * Returns true is the passed ItemStack is an input for this recipe. "Input"
     * being the item that goes in one of the three bottom slots of the brewing
     * stand (e.g: water bottle)
     */
    public boolean isInput(ItemStack input);

    /**
     * Returns true if the passed ItemStack is an ingredient for this recipe.
     * "Ingredient" being the item that goes in the top slot of the brewing
     * stand (e.g: nether wart)
     */
    public boolean isIngredient(ItemStack ingredient);

    /**
     * Returns the output when the passed input is brewed with the passed
     * ingredient inside the given TileEntity. Null has to be returned if input or ingredient are invalid.
     */
    public ItemStack getOutput(ItemStack input, ItemStack ingredient, TileEntity tile);
}