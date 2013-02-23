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
import net.minecraftforge.crafting.tags.ItemEmptyContainer;
import net.minecraftforge.crafting.tags.ItemFilledContainer;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

public class ShapelessLiquidFillingRecipe implements IRecipeExtractable {

    private LiquidStack output = null;
    private ArrayList input = new ArrayList();
    
    // If this member is true, matching fails when there are same items in the input matrix and output result.
    private boolean excludeSame = true;

    public ShapelessLiquidFillingRecipe(LiquidStack result, Object... recipe)
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
            else if (in instanceof ItemEmptyContainer || in instanceof ICraftingMaterial)
            {
                input.add(in);
            }
            else
            {
                String ret = "Invalid shapeless filling recipe: ";
                for (Object tmp : recipe)
                {
                    ret += tmp + ", ";
                }
                ret += output;
                throw new RuntimeException(ret);
            }
        }
    }

    protected ShapelessLiquidFillingRecipe(ShapelessRecipes recipe, Map<ItemStack, Object> replacements)
    {
        output = LiquidContainerRegistry.getLiquidForFilledItem(recipe.getRecipeOutput());

        for (ItemStack ingred : (List<ItemStack>) recipe.recipeItems)
        {
            Object finalObj = ingred;
            for (Entry<ItemStack, Object> replace : replacements.entrySet())
            {
                if (ForgeRecipeUtils.itemMatches(replace.getKey(), ingred, true))
                {
                    Object value = replace.getValue();
                    if (value instanceof ItemEmptyContainer
                            || value instanceof ICraftingMaterial)
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
        return getCraftingResult(var1) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1)
    {
        ArrayList required = new ArrayList(input);
        ItemStack result = null;
        ArrayList<ItemStack> foundMaterials = new ArrayList<ItemStack>();

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
                    boolean isMaterial = true;

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
                    else if (next instanceof ItemEmptyContainer)
                    {

                        ItemStack filled = ((ItemEmptyContainer) next).getFilledContainer(output, slot);
                        if (filled != null)
                        {
                            if (result == null)
                            {
                                result = filled;
                                match = true;
                            }
                            else if (result.isItemEqual(filled) && result.stackSize < result.getMaxStackSize())
                            {
                                ++result.stackSize;
                                match = true;
                            }
                            isMaterial = false;
                        }
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
                    return null;
                }
            }
        }

        if (!required.isEmpty())
        {
            return null;
        }

        if (result != null && excludeSame)
        {
            for (ItemStack material : foundMaterials)
            {
                if (material.isItemEqual(result))
                {
                    return null;
                }
            }
        }
        return result;
    }

    @Override
    public int getRecipeSize()
    {
        return input.size();
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return null;
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
        ArrayList<Object[]> result = new ArrayList<Object[]>();
        if (input == null || output == null)
        {
            return result;
        }

        ItemEmptyContainer container = null;
        int stackSize = 0;
        for (Object in : input)
        {
            if (in instanceof ItemEmptyContainer)
            {
                container = (ItemEmptyContainer) in;
                ++stackSize;
            }
        }
        if (stackSize == 0)
        {
            return result;
        }

        calcresult: for (LiquidContainerData data : container.getLiquidContainerData(output))
        {
            Object[] recipe = new Object[getRecipeSize() + 1];
            ItemStack output = data.filled.copy();
            if (output.getMaxStackSize() >= stackSize)
            {
                output.stackSize = stackSize;
            }
            else
            {
                continue;
            }

            for (int i = 0; i < getRecipeSize(); ++i)
            {
                Object in = input.get(i);

                if (in instanceof ItemStack)
                {
                    recipe[i] = ((ItemStack) in).copy();
                }
                else if (in instanceof ArrayList)
                {
                    ArrayList<ItemStack> acceptable = new ArrayList<ItemStack>();
                    for (ItemStack item : ForgeRecipeUtils.expandArray((ArrayList)in))
                    {
                        if (!excludeSame || !item.isItemEqual(output))
                        {
                            acceptable.add(item);
                        }
                    }
                    if (acceptable.size() == 0)
                    {
                        continue calcresult;
                    }
                    recipe[i] = acceptable;
                }
                else if (in instanceof ICraftingMaterial)
                {
                    ArrayList<ItemStack> acceptable = new ArrayList<ItemStack>();
                    for (ItemStack item : ((ICraftingMaterial) in).getItems())
                    {
                        if (!excludeSame || !item.isItemEqual(output))
                        {
                            acceptable.add(item);
                        }
                    }
                    if (acceptable.size() == 0)
                    {
                        continue calcresult;
                    }
                    recipe[i] = acceptable;
                }
                else if (in instanceof ItemEmptyContainer)
                {
                    recipe[i] = data.container.copy();
                }
            }
            recipe[recipe.length - 1] = output;
            result.add(recipe);
        }
        return result;
    }

    public ShapelessLiquidFillingRecipe setExcludeSame(boolean exclude)
    {
        excludeSame = exclude;
        return this;
    }
}
