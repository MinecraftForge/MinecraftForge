package net.minecraftforge.common.smelting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SimpleSmeltingRecipe extends AbstractSmeltingRecipe
{

    protected final ItemStack input, output;

    public SimpleSmeltingRecipe(ItemStack input, ItemStack output, float xp, int duration){
        super(xp, duration);
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(ItemStack input)
    {
        return OreDictionary.itemMatches(this.input, input, false);
    }

    @Override
    public ItemStack getOutput0(ItemStack input)
    {
        return output;
    }

}
