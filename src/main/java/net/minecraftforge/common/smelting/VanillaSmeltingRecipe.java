package net.minecraftforge.common.smelting;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

final class VanillaSmeltingRecipe implements SmeltingRecipe
{

    private final Map<ItemStack, ItemStack> smeltingList;
    private final Map<ItemStack, Float> experienceList;

    VanillaSmeltingRecipe(Map<ItemStack, ItemStack> smeltingList, Map<ItemStack, Float> experienceList){
        this.smeltingList = smeltingList;
        this.experienceList = experienceList;
    }

    @Override
    public final boolean matches(ItemStack input)
    {
        return getOutput0(input) != null;
    }

    @Override
    public final ItemStack getOutput(ItemStack input)
    {
        return ItemStack.copyItemStack(getOutput0(input));
    }

    private ItemStack getOutput0(ItemStack input)
    {
        for (Map.Entry<ItemStack, ItemStack> entry : smeltingList.entrySet())
        {
            if (OreDictionary.itemMatches(entry.getKey(), input, false))
            {
                return entry.getValue().copy();
            }
        }
        return null;
    }

    @Override
    public int getDuration(ItemStack input)
    {
        return SmeltingRecipeRegistry.DEFAULT_COOK_TIME;
    }

    @Override
    public float getExperience(ItemStack input)
    {
        float ret = input.getItem().getSmeltingExperience(input);
        if (ret != -1) return ret;

        for (Map.Entry<ItemStack, Float> entry : this.experienceList.entrySet())
        {
            if (OreDictionary.itemMatches(entry.getKey(), input, false))
            {
                return entry.getValue();
            }
        }

        return 0.0F;
    }
}
