package net.minecraftforge.oredict;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ItemCondition;
import net.minecraftforge.common.util.ItemPredicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ShapelessOreRecipe implements IRecipe
{
    private ItemStack output = null;
    private ArrayList<ItemPredicate> input = new ArrayList<ItemPredicate>();

    public ShapelessOreRecipe(Block result, Object... recipe){ this(new ItemStack(result), recipe); }
    public ShapelessOreRecipe(Item  result, Object... recipe){ this(new ItemStack(result), recipe); }

    public ShapelessOreRecipe(ItemStack result, Object... recipe)
    {
        output = result.copy();
        for (Object in : recipe)
        {
            if (in instanceof ItemStack)
            {
                input.add(ItemPredicate.of(ItemCondition.ofItemStack((ItemStack)in)));
            }
            else if (in instanceof Item)
            {
                input.add(ItemPredicate.ofItem((Item) in));
            }
            else if (in instanceof Block)
            {
                input.add(ItemPredicate.ofBlock((Block) in));
            }
            else if (in instanceof String)
            {
                input.add(ItemPredicate.ofOre((String) in));
            }
            else if (in instanceof ItemPredicate)
            {
                input.add((ItemPredicate) in);
            }
            else if (in instanceof ItemCondition)
            {
                input.add(ItemPredicate.of((ItemCondition) in));
            }
            else
            {
                String ret = "Invalid shapeless ore recipe: ";
                for (Object tmp :  recipe)
                {
                    ret += tmp + ", ";
                }
                ret += output;
                throw new RuntimeException(ret);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    ShapelessOreRecipe(ShapelessRecipes recipe, Map<ItemStack, String> replacements)
    {
        output = recipe.getRecipeOutput();

        for(ItemStack ingred : ((List<ItemStack>)recipe.recipeItems))
        {
            ItemPredicate finalObj = ItemPredicate.of(ItemCondition.ofItemStack(ingred));
            for(Entry<ItemStack, String> replace : replacements.entrySet())
            {
                if(OreDictionary.itemMatches(replace.getKey(), ingred, false))
                {
                    finalObj = ItemPredicate.ofOre(replace.getValue());
                    break;
                }
            }
            input.add(finalObj);
        }
    }

    @Override
    public int getRecipeSize(){ return input.size(); }

    @Override
    public ItemStack getRecipeOutput(){ return output; }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1){ return output.copy(); }

    @Override
    public boolean matches(InventoryCrafting var1, World world)
    {
        ArrayList<ItemPredicate> required = new ArrayList<ItemPredicate>(input);

        for (int x = 0; x < var1.getSizeInventory(); x++)
        {
            ItemStack slot = var1.getStackInSlot(x);

            if (slot != null)
            {
                boolean inRecipe = false;
                Iterator<ItemPredicate> req = required.iterator();

                while (req.hasNext())
                {
                    ItemPredicate next = req.next();

                    if (next.apply(slot))
                    {
                        inRecipe = true;
                        required.remove(next);
                        break;
                    }
                }

                if (!inRecipe)
                {
                    return false;
                }
            }
        }

        return required.isEmpty();
    }

    /**
     * Returns the input for this recipe, any mod accessing this value should never
     * manipulate the values in this array as it will effect the recipe itself.
     * @deprecated
     * @return The recipes input vales.
     */
    @Deprecated
    public ArrayList<Object> getInput()
    {
        ArrayList objList = this.input;
        return objList;
    }

    /**
     * Returns the input for this recipe, any mod accessing this value should never
     * manipulate the values in this list as it will effect the recipe itself.
     * @return The recipes input vales.
     */
    public ArrayList<ItemPredicate> getInputPredicates()
    {
        return this.input;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) //getRecipeLeftovers
    {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }
    
}
