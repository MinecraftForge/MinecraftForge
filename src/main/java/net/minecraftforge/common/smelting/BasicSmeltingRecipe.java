package net.minecraftforge.common.smelting;

import net.minecraft.item.ItemStack;

public class BasicSmeltingRecipe implements ISmeltingRecipe
{
    private final ItemStack output;
    private final float experience;

    public BasicSmeltingRecipe(ItemStack output, float experience)
    {
        this.output = output;
        this.experience = experience;
    }

    @Override
    public ItemStack getSmeltingResult(ItemStack input)
    {
        return output;
    }

    @Override
    public float getExperience(ItemStack input)
    {
        float ret = input.getItem().getSmeltingExperience(input);
        if (ret != -1) return ret;
        return experience;
    }

    @Override
    public ItemStack getGenericOutput()
    {
        return output;
    }
}
