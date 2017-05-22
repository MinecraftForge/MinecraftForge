package net.minecraftforge.common.smelting;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

final class VanillaSmeltingRecipe implements SmeltingRecipe
{

    private final Map<ItemStack, ItemStack> smeltingList;

    VanillaSmeltingRecipe(Map<ItemStack, ItemStack> smeltingList){
        this.smeltingList = smeltingList;
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
        return matches(input) ? SmeltingRecipeRegistry.DEFAULT_COOK_TIME : -1;
    }

}