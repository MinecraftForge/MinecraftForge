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
import net.minecraftforge.crafting.tags.ItemFilledContainer;
import net.minecraftforge.liquids.LiquidStack;

public class ShapedForgeRecipe implements IRecipeExtractable {
    // Added in for future ease of change, but hard coded for now.
    private static final int MAX_CRAFT_GRID_WIDTH = 3;
    private static final int MAX_CRAFT_GRID_HEIGHT = 3;

    private ItemStack output = null;
    private Object[] input = null;
    private int width = 0;
    private int height = 0;
    private boolean mirrored = true;

    public ShapedForgeRecipe(Block result, Object... recipe)
    {
        this(new ItemStack(result), recipe);
    }

    public ShapedForgeRecipe(Item result, Object... recipe)
    {
        this(new ItemStack(result), recipe);
    }

    public ShapedForgeRecipe(ItemStack result, Object... recipe)
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
            String ret = "Invalid shaped forge recipe: ";
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
            else if (in instanceof ICraftingMaterial)
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

    protected ShapedForgeRecipe(ShapedRecipes recipe, Map<ItemStack, Object> replacements)
    {
        output = recipe.getRecipeOutput();
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
                    if (value instanceof ICraftingMaterial)
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
    public ItemStack getCraftingResult(InventoryCrafting var1)
    {
        return output.copy();
    }

    @Override
    public int getRecipeSize()
    {
        return input.length;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return output;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world)
    {
        for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++)
        {
            for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y)
            {
                if (checkMatch(inv, x, y, true))
                {
                    return true;
                }

                if (mirrored && checkMatch(inv, x, y, false))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror)
    {
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

                if (target instanceof ItemStack)
                {
                    if (!ForgeRecipeUtils.itemMatches((ItemStack) target, slot))
                    {
                        return false;
                    }
                }
                else if (target instanceof ArrayList)
                {

                    if (!ForgeRecipeUtils.itemMatches((ArrayList) target, slot))
                    {
                        return false;
                    }
                }
                else if (target instanceof ICraftingMaterial)
                {

                    if (!((ICraftingMaterial) target).itemMatches(slot))
                    {
                        return false;
                    }
                }
                else if (target == null && slot != null)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public ShapedForgeRecipe setMirrored(boolean mirror)
    {
        mirrored = mirror;
        return this;
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
        Object[] recipe = null;
        if (input != null && output != null)
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
                    recipe[i] = ForgeRecipeUtils.expandArray((ArrayList)input[i]);
                }
                else if (input[i] instanceof ICraftingMaterial)
                {
                    recipe[i] = ((ICraftingMaterial) input[i]).getItems();
                }
            }
            recipe[recipe.length - 1] = output.copy();
        }

        if (recipe != null)
        {
            result.add(recipe);
        }
        return result;
    }
}
