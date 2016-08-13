package net.minecraftforge.common.smelting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreSmeltingRecipe extends AbstractSmeltingRecipe
{

    protected final ItemStack output;
    protected final List<ItemStack> inputs;

    public OreSmeltingRecipe(String input, ItemStack output, float xp, int duration)
    {
        super(xp, duration);
        this.inputs = OreDictionary.getOres(input);
        this.output = output;
    }

    @Override
    public boolean matches(ItemStack input)
    {
        for (ItemStack candidate : inputs)
        {
            if (OreDictionary.itemMatches(input, candidate, false))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected ItemStack getOutput0(ItemStack input)
    {
        return output;
    }
}
