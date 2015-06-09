package net.minecraftforge.common.brewing;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class BrewingOreRecipe extends AbstractBrewingRecipe<List<ItemStack>> {

    public BrewingOreRecipe(ItemStack input, String ingredient, ItemStack output)
    {
        super(input, OreDictionary.getOres(ingredient), output);
    }

    public BrewingOreRecipe(ItemStack input, List<ItemStack> ingredient, ItemStack output)
    {
        super(input, ingredient, output);
    }

    @Override
    public boolean isIngredient(ItemStack stack)
    {
        for (ItemStack target : this.ingredient)
        {
            if (OreDictionary.itemMatches(target, stack, false))
            {
                return true;
            }

        }
        return false;
    }
}