package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.crafting.ShapelessForgeRecipe;
import net.minecraftforge.crafting.tags.ItemOre;

public class ShapelessOreRecipe extends ShapelessForgeRecipe
{

    public ShapelessOreRecipe(Block result, Object... recipe){ this(new ItemStack(result), recipe); }
    public ShapelessOreRecipe(Item  result, Object... recipe){ this(new ItemStack(result), recipe); }
    
    public ShapelessOreRecipe(ItemStack result, Object... recipe)
    {
        super(result, convertInput(recipe));
    }
    
    ShapelessOreRecipe(ShapelessRecipes recipe, Map<ItemStack, String> replacements)
    {
        super(recipe, convertInput(replacements));
    }
    
    private static Object[] convertInput(Object[] recipe)
    {
        ArrayList result = new ArrayList();
        for (Object in : recipe)
        {
            if (in instanceof String)
            {
                result.add(new ItemOre((String)in));
            }
            else
            {
                result.add(in);
            }
        }
        return result.toArray();
    }

    private static Map<ItemStack, Object> convertInput(Map<ItemStack, String> replacements)
    {
        Map<ItemStack, Object> result = new HashMap<ItemStack, Object>();

        for(Entry<ItemStack, String> entry : replacements.entrySet())
        {
            result.put(entry.getKey(), new ItemOre(entry.getValue()));
        }
        
        return result;
    }
}
