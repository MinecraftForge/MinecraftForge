package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.crafting.ShapedForgeRecipe;
import net.minecraftforge.crafting.tags.ItemOre;

public class ShapedOreRecipe extends ShapedForgeRecipe
{

    public ShapedOreRecipe(Block     result, Object... recipe){ this(new ItemStack(result), recipe); }
    public ShapedOreRecipe(Item      result, Object... recipe){ this(new ItemStack(result), recipe); }
    public ShapedOreRecipe(ItemStack result, Object... recipe)
    {
        super(result, convertInput(recipe));
    }
    
    ShapedOreRecipe(ShapedRecipes recipe, Map<ItemStack, String> replacements)
    {
        super(recipe, convertInput(replacements));
    }

    private static Object[] convertInput(Object[] recipe)
    {
        ArrayList result = new ArrayList();
        int idx = 0;

        if (recipe[idx] instanceof Boolean)
        {
            result.add(recipe[idx]);
            if (recipe[idx+1] instanceof Object[])
            {
                recipe = (Object[])recipe[idx+1];
            }
            else
            {
                idx = 1;
            }
        }

        if (recipe[idx] instanceof String[])
        {
            result.add(recipe[idx++]);
        }
        else
        {
            while (recipe[idx] instanceof String)
            {
                result.add(recipe[idx++]);
            }
        }

        for (; idx < recipe.length; ++idx)
        {
            if (recipe[idx] instanceof String)
            {
                result.add(new ItemOre((String)recipe[idx]));
            }
            else
            {
                result.add(recipe[idx]);
            }
        }

        return result.toArray();
    }
    
    private static Map<ItemStack, Object> convertInput(Map<ItemStack, String> replacements)
    {
        Map<ItemStack, Object> converted = new HashMap<ItemStack, Object>();
        
        for(Entry<ItemStack, String> entry : replacements.entrySet())
        {
            converted.put(entry.getKey(), new ItemOre(entry.getValue()));
        }
        
        return converted;
    }
}
