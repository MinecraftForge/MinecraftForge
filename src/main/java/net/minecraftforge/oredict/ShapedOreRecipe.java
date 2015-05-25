package net.minecraftforge.oredict;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ItemPredicate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ShapedOreRecipe implements IRecipe
{
    //Added in for future ease of change, but hard coded for now.
    private static final int MAX_CRAFT_GRID_WIDTH = 3;
    private static final int MAX_CRAFT_GRID_HEIGHT = 3;

    private ItemStack output = null;
    private ItemPredicate[] input = null;
    private int width = 0;
    private int height = 0;
    private boolean mirrored = true;

    public ShapedOreRecipe(Block     result, Object... recipe){ this(new ItemStack(result), recipe); }
    public ShapedOreRecipe(Item      result, Object... recipe){ this(new ItemStack(result), recipe); }
    public ShapedOreRecipe(ItemStack result, Object... recipe)
    {
        output = result.copy();

        String shape = "";
        int idx = 0;

        if (recipe[idx] instanceof Boolean)
        {
            mirrored = (Boolean)recipe[idx];
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
            String[] parts = ((String[])recipe[idx++]);

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
                String s = (String)recipe[idx++];
                shape += s;
                width = s.length();
                height++;
            }
        }

        if (width * height != shape.length())
        {
            String ret = "Invalid shaped ore recipe: ";
            for (Object tmp :  recipe)
            {
                ret += tmp + ", ";
            }
            ret += output;
            throw new RuntimeException(ret);
        }

        HashMap<Character, ItemPredicate> itemMap = new HashMap<Character, ItemPredicate>();

        for (; idx < recipe.length; idx += 2)
        {
            Character chr = (Character)recipe[idx];
            Object in = recipe[idx + 1];

            if (in instanceof ItemStack)
            {
                itemMap.put(chr, ItemPredicate.ofItemStack((ItemStack)in));
            }
            else if (in instanceof Item)
            {
                itemMap.put(chr, ItemPredicate.ofItem((Item) in));
            }
            else if (in instanceof Block)
            {
                itemMap.put(chr, ItemPredicate.ofBlock((Block) in));
            }
            else if (in instanceof String)
            {
                itemMap.put(chr, new ItemPredicate.Ore((String) in));
            }
            else if (in instanceof ItemPredicate)
            {
                itemMap.put(chr, (ItemPredicate)in);
            }
            else
            {
                String ret = "Invalid shaped ore recipe: ";
                for (Object tmp :  recipe)
                {
                    ret += tmp + ", ";
                }
                ret += output;
                throw new RuntimeException(ret);
            }
        }

        input = new ItemPredicate[width * height];
        int x = 0;
        for (char chr : shape.toCharArray())
        {
            input[x++] = itemMap.get(chr);
        }
    }

    ShapedOreRecipe(ShapedRecipes recipe, Map<ItemStack, String> replacements)
    {
        output = recipe.getRecipeOutput();
        width = recipe.recipeWidth;
        height = recipe.recipeHeight;

        input = new ItemPredicate[recipe.recipeItems.length];

        for(int i = 0; i < input.length; i++)
        {
            ItemStack ingred = recipe.recipeItems[i];

            if(ingred == null) continue;

            input[i] = ItemPredicate.ofItemStack(recipe.recipeItems[i]);

            for(Entry<ItemStack, String> replace : replacements.entrySet())
            {
                if(OreDictionary.itemMatches(replace.getKey(), ingred, true))
                {
                    input[i] = new ItemPredicate.Ore(replace.getValue());
                    break;
                }
            }
        }
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1){ return output.copy(); }

    @Override
    public int getRecipeSize(){ return input.length; }

    @Override
    public ItemStack getRecipeOutput(){ return output; }

    @Override
    public boolean matches(InventoryCrafting inv, World world)
    {
        for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++)
        {
            for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y)
            {
                if (checkMatch(inv, x, y, false))
                {
                    return true;
                }

                if (mirrored && checkMatch(inv, x, y, true))
                {
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror)
    {
        for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++)
        {
            for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++)
            {
                int subX = x - startX;
                int subY = y - startY;
                ItemPredicate target = ItemPredicate.EMPTY_STACK;

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


                if (target != null && !(target.apply(slot, false)))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public ShapedOreRecipe setMirrored(boolean mirror)
    {
        mirrored = mirror;
        return this;
    }

    /**
     * Returns the input for this recipe, any mod accessing this value should never
     * manipulate the values in this array as it will effect the recipe itself.
     * @return The recipes input vales.
     */
    public Object[] getInput()
    {
        return this.input;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) //getRecipeLeftovers
    {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }
}
