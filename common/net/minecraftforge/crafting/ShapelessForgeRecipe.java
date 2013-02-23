package net.minecraftforge.crafting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.crafting.tags.ItemFilledContainer;
import net.minecraftforge.liquids.LiquidStack;

public class ShapelessForgeRecipe implements IRecipeExtractable {

    private ItemStack output = null;
    private ArrayList input = new ArrayList();

    public ShapelessForgeRecipe(Block result, Object... recipe)
    {
        this(new ItemStack(result), recipe);
    }

    public ShapelessForgeRecipe(Item result, Object... recipe)
    {
        this(new ItemStack(result), recipe);
    }

    public ShapelessForgeRecipe(ItemStack result, Object... recipe)
    {
        output = result.copy();
        for (Object in : recipe)
        {
            if (in instanceof ItemStack)
            {
                input.add(((ItemStack) in).copy());
            }
            else if (in instanceof Item)
            {
                input.add(new ItemStack((Item) in));
            }
            else if (in instanceof Block)
            {
                input.add(new ItemStack((Block) in));
            }
            else if (in instanceof LiquidStack)
            {
                input.add(new ItemFilledContainer((LiquidStack) in));
            }
            else if (in instanceof Collection && ForgeRecipeUtils.isValidItemList((Collection)in))
            {
                input.add(new ArrayList((Collection) in));
            }
            else if (in instanceof ICraftingMaterial)
            {
                input.add(in);
            }
            else
            {
                String ret = "Invalid shapeless forge recipe: ";
                for (Object tmp : recipe)
                {
                    ret += tmp + ", ";
                }
                ret += output;
                throw new RuntimeException(ret);
            }
        }
    }

    protected ShapelessForgeRecipe(ShapelessRecipes recipe, Map<ItemStack, Object> replacements)
    {
        output = recipe.getRecipeOutput();

        for (ItemStack ingred : (List<ItemStack>) recipe.recipeItems)
        {
            Object finalObj = ingred;
            for (Entry<ItemStack, Object> replace : replacements.entrySet())
            {
                if (ForgeRecipeUtils.itemMatches(replace.getKey(), ingred, true))
                {
                    Object value = replace.getValue();
                    if (value instanceof ICraftingMaterial)
                    {
                        finalObj = value;
                    }
                    else if (value instanceof Collection && ForgeRecipeUtils.isValidItemList((Collection)value))
                    {
                        finalObj = new ArrayList((Collection) value);
                    }
                    else if (value instanceof LiquidStack)
                    {
                        finalObj = new ItemFilledContainer((LiquidStack) value);
                    }
                    break;
                }
            }
            input.add(finalObj);
        }
    }

    @Override
    public boolean matches(InventoryCrafting var1, World var2)
    {
        ArrayList required = new ArrayList(input);

        for (int x = 0; x < var1.getSizeInventory(); x++)
        {
            ItemStack slot = var1.getStackInSlot(x);

            if (slot != null)
            {
                boolean inRecipe = false;
                Iterator req = required.iterator();

                while (req.hasNext())
                {
                    boolean match = false;

                    Object next = req.next();

                    if (next instanceof ItemStack)
                    {
                        match = ForgeRecipeUtils.itemMatches((ItemStack) next, slot);
                    }
                    else if (next instanceof ArrayList)
                    {
                        match = ForgeRecipeUtils.itemMatches((ArrayList) next, slot);
                    }
                    else if (next instanceof ICraftingMaterial)
                    {

                        match = ((ICraftingMaterial) next).itemMatches(slot);
                    }

                    if (match)
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

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1)
    {
        return output.copy();
    }

    @Override
    public int getRecipeSize()
    {
        return input.size();
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return output;
    }

    @Override
    public Object[] getRawRecipe()
    {
        Object[] recipe = null;
        if (input != null)
        {
            recipe = new Object[getRecipeSize() + 1];
            for (int i = 0; i < getRecipeSize(); ++i)
            {
                Object in = input.get(i);
                if (in instanceof ItemStack)
                {
                    recipe[i] = ((ItemStack) in).copy();
                }
                else if (in instanceof ArrayList)
                {
                    ArrayList items = new ArrayList();
                    for (Object item : (ArrayList) in)
                    {
                        if(item instanceof ItemStack)
                        {
                            items.add(((ItemStack)item).copy());
                        }
                        else
                        {
                            items.add(item);
                        }
                    }
                    recipe[i] = items;
                }
                else
                {
                    recipe[i] = in;
                }
            }
            recipe[recipe.length - 1] = output.copy();
        }
        return recipe;
    }

    @Override
    public ArrayList<Object[]> getPossibleRecipes()
    {
        Object[] recipe = null;
        if (input != null)
        {
            recipe = new Object[getRecipeSize() + 1];
            for (int i = 0; i < getRecipeSize(); ++i)
            {
                Object in = input.get(i);
                if (in instanceof ItemStack)
                {
                    recipe[i] = ((ItemStack) in).copy();
                }
                else if (in instanceof ArrayList)
                {
                    recipe[i] = ForgeRecipeUtils.expandArray((ArrayList)in);
                }
                else if (in instanceof ICraftingMaterial)
                {
                    recipe[i] = ((ICraftingMaterial) in).getItems();
                }
            }
            recipe[recipe.length - 1] = output.copy();
        }

        ArrayList<Object[]> result = new ArrayList<Object[]>();
        if (recipe != null)
        {
            result.add(recipe);
        }
        return result;
    }
}
