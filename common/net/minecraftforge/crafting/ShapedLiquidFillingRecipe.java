package net.minecraftforge.crafting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import net.minecraftforge.crafting.tags.ItemEmptyContainer;
import net.minecraftforge.crafting.tags.ItemFilledContainer;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

public class ShapedLiquidFillingRecipe implements IRecipeExtractable {
    // Added in for future ease of change, but hard coded for now.
    private static final int MAX_CRAFT_GRID_WIDTH = 3;
    private static final int MAX_CRAFT_GRID_HEIGHT = 3;

    private LiquidStack output = null;
    private Object[] input = null;
    private int width = 0;
    private int height = 0;
    private boolean mirrored = true;
    
    // If this member is true, matching fails when there are same items in the input matrix and output result.
    private boolean excludeSame = true;

    public ShapedLiquidFillingRecipe(LiquidStack result, Object... recipe)
    {
        output = result.copy();

        String shape = "";
        int idx = 0;

        if (recipe[idx] instanceof Boolean)
        {
            mirrored = (Boolean) recipe[idx];
            if (recipe[idx + 1] instanceof Object[])
            {
                recipe = (Object[]) recipe[idx + 1];
            }
            else
            {
                idx = 1;
            }
        }

        if (recipe[idx] instanceof String[])
        {
            String[] parts = (String[]) recipe[idx++];

            for (String s : parts)
            {
                width = s.length();
                shape += s;
            }

            height = parts.length;
        }
        else
        {
            while (recipe[idx] instanceof String)
            {
                String s = (String) recipe[idx++];
                shape += s;
                width = s.length();
                height++;
            }
        }

        if (width * height != shape.length())
        {
            String ret = "Invalid shaped filling recipe: ";
            for (Object tmp : recipe)
            {
                ret += tmp + ", ";
            }
            ret += output;
            throw new RuntimeException(ret);
        }

        HashMap<Character, Object> itemMap = new HashMap<Character, Object>();

        for (; idx < recipe.length; idx += 2)
        {
            Character chr = (Character) recipe[idx];
            Object in = recipe[idx + 1];

            if (in instanceof ItemStack)
            {
                itemMap.put(chr, ((ItemStack) in).copy());
            }
            else if (in instanceof Item)
            {
                itemMap.put(chr, new ItemStack((Item) in));
            }
            else if (in instanceof Block)
            {
                itemMap.put(chr, new ItemStack((Block) in, 1, -1));
            }
            else if (in instanceof LiquidStack)
            {
                itemMap.put(chr, new ItemFilledContainer((LiquidStack) in));
            }
            else if (in instanceof Collection && ForgeRecipeUtils.isValidItemList((Collection)in))
            {
                itemMap.put(chr, new ArrayList((Collection) in));
            }
            else if (in instanceof ItemEmptyContainer || in instanceof ICraftingMaterial)
            {
                itemMap.put(chr, in);
            }
            else
            {
                String ret = "Invalid shaped filling recipe: ";
                for (Object tmp : recipe)
                {
                    ret += tmp + ", ";
                }
                ret += output;
                throw new RuntimeException(ret);
            }
        }

        input = new Object[width * height];
        int x = 0;
        for (char chr : shape.toCharArray())
        {
            input[x++] = itemMap.get(chr);
        }
    }

    protected ShapedLiquidFillingRecipe(ShapedRecipes recipe, Map<ItemStack, Object> replacements)
    {
        output = LiquidContainerRegistry.getLiquidForFilledItem(recipe.getRecipeOutput());
        width = recipe.recipeWidth;
        height = recipe.recipeHeight;

        input = new Object[recipe.recipeItems.length];

        for (int i = 0; i < input.length; i++)
        {
            ItemStack ingred = recipe.recipeItems[i];

            if (ingred == null)
            {
                continue;
            }

            input[i] = recipe.recipeItems[i];

            for (Entry<ItemStack, Object> replace : replacements.entrySet())
            {
                if (ForgeRecipeUtils.itemMatches(replace.getKey(), ingred, true))
                {
                    Object value = replace.getValue();
                    if (value instanceof ItemEmptyContainer
                            || value instanceof ICraftingMaterial)
                    {
                        input[i] = value;
                    }
                    else if (value instanceof Collection && ForgeRecipeUtils.isValidItemList((Collection)value))
                    {
                        input[i] = new ArrayList((Collection) value);
                    }
                    else if (value instanceof LiquidStack)
                    {
                        input[i] = new ItemFilledContainer((LiquidStack) value);
                    }
                    break;
                }
            }
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
        for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++)
        {
            for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y)
            {
                ItemStack result = checkResult(var1, x, y, true);
                if (result != null)
                {
                    return result;
                }

                if (mirrored)
                {
                    result = checkResult(var1, x, y, false);
                    if (result != null)
                    {
                        return result;
                    }
                }
            }
        }

        return null;
    }

    private ItemStack checkResult(InventoryCrafting inv, int startX, int startY, boolean mirror)
    {
        ItemStack result = null;
        ArrayList<ItemStack> foundMaterials = new ArrayList<ItemStack>();
        for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++)
        {
            for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++)
            {
                int subX = x - startX;
                int subY = y - startY;
                Object target = null;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height)
                {
                    if (mirror)
                    {
                        target = input[width - subX - 1 + subY * width];
                    }
                    else
                    {
                        target = input[subX + subY * width];
                    }
                }

                ItemStack slot = inv.getStackInRowAndColumn(x, y);
                boolean isMaterial = slot != null;

                if (target instanceof ItemStack)
                {
                    if (!ForgeRecipeUtils.itemMatches((ItemStack) target, slot))
                    {
                        return null;
                    }
                }
                else if (target instanceof ArrayList)
                {

                    if (!ForgeRecipeUtils.itemMatches((ArrayList) target, slot))
                    {
                        return null;
                    }
                }
                else if (target instanceof ICraftingMaterial)
                {

                    if (!((ICraftingMaterial) target).itemMatches(slot))
                    {
                        return null;
                    }
                }
                else if (target instanceof ItemEmptyContainer)
                {
                    ItemStack filled = ((ItemEmptyContainer) target).getFilledContainer(output, slot);
                    if (filled == null)
                    {
                        return null;
                    }
                    else if (result == null)
                    {
                        result = filled;
                    }
                    else if (result.isItemEqual(filled) && result.stackSize < result.getMaxStackSize())
                    {
                        ++result.stackSize;
                    }
                    else
                    {
                        return null;
                    }
                    isMaterial = false;
                }
                else if (target == null && slot != null)
                {
                    return null;
                }

                if (isMaterial)
                {
                    foundMaterials.add(slot);
                }
            }
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
        return width * height;
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
                if (input[i] instanceof ItemStack)
                {
                    recipe[i] = ((ItemStack) input[i]).copy();
                }
                else if (input[i] instanceof ArrayList)
                {
                    ArrayList items = new ArrayList();
                    for (Object item : (ArrayList) input[i])
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
                    recipe[i] = input[i];
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
                if (input[i] instanceof ItemStack)
                {
                    recipe[i] = ((ItemStack) input[i]).copy();
                }
                else if (input[i] instanceof ArrayList)
                {
                    ArrayList<ItemStack> acceptable = new ArrayList<ItemStack>();
                    for (ItemStack item : ForgeRecipeUtils.expandArray((ArrayList)input[i]))
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
                else if (input[i] instanceof ICraftingMaterial)
                {
                    ArrayList<ItemStack> acceptable = new ArrayList<ItemStack>();
                    for (ItemStack item : ((ICraftingMaterial) input[i]).getItems())
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
                else if (input[i] instanceof ItemEmptyContainer)
                {
                    recipe[i] = data.container.copy();
                }
            }
            recipe[recipe.length - 1] = output;
            result.add(recipe);
        }
        return result;
    }

    public ShapedLiquidFillingRecipe setMirrored(boolean mirror)
    {
        mirrored = mirror;
        return this;
    }

    public ShapedLiquidFillingRecipe setExcludeSame(boolean exclude)
    {
        excludeSame = exclude;
        return this;
    }

}
