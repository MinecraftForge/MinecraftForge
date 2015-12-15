package net.minecraftforge.common.brewing;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

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
     * ingredient at the given location and brewer (usually a TileEntity, but this may vary).
     * Null has to be returned if input or ingredient are invalid or the given recipe is not applicable to the situation.
     */
    public ItemStack getOutput(ItemStack input, ItemStack ingredient, @Nullable World world, @Nullable BlockPos position, @Nullable Object brewer);
}