package net.minecraftforge.common.brewing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BrewingRecipeRegistry {

    private static List<IBrewingRecipe> recipes = new ArrayList<IBrewingRecipe>();
    private static boolean disableVanillaRecipes;

    static
    {
        addRecipe(new VanillaBrewingRecipe());
    }

    /**
     * Adds a recipe to the registry. Due to the nature of the brewing stand
     * inputs that stack (a.k.a max stack size > 1) are not allowed.
     * 
     * @param input
     *            The ItemStack that goes in same slots as the water bottles
     *            would.
     * @param ingredient
     *            The ItemStack that goes in the same slot as nether wart would.
     * @param output
     *            The ItemStack that will replace the input once the brewing is
     *            done.
     * @return true if the recipe was added.
     */
    public static boolean addRecipe(ItemStack input, ItemStack ingredient, ItemStack output)
    {
        return addRecipe(new BrewingRecipe(input, ingredient, output));
    }

    /**
     * Adds a recipe to the registry. Due to the nature of the brewing stand
     * inputs that stack (a.k.a max stack size > 1) are not allowed.
     * 
     * @param input
     *            The ItemStack that goes in same slots as the water bottles
     *            would.
     * @param ingredient
     *            The ItemStack that goes in the same slot as nether wart would.
     * @param output
     *            The ItemStack that will replace the input once the brewing is
     *            done.
     * @return true if the recipe was added.
     */
    public static boolean addRecipe(ItemStack input, String ingredient, ItemStack output)
    {
        return addRecipe(new BrewingOreRecipe(input, ingredient, output));
    }

    /**
     * Adds a recipe to the registry. Due to the nature of the brewing stand
     * inputs that stack (a.k.a max stack size > 1) are not allowed.
     * If vanilla brewing recipes have been disabled and the recipe is of said type,
     * the element will not be inserted.
     */
    public static boolean addRecipe(IBrewingRecipe recipe)
    {
        return disableVanillaRecipes && recipe instanceof VanillaBrewingRecipe ? false : recipes.add(recipe);
    }
    
    /**
     * Removes the first occure of this recipe from the recipe list
     */
    public static boolean removeRecipe(IBrewingRecipe recipe)
    {
        return recipes.remove(recipe);
    }

    /**
     * Returns the output ItemStack obtained by brewing the passed input and
     * ingredient at the given location in the brewer. Null if no matches are found.
     */
    public static ItemStack getOutput(ItemStack input, ItemStack ingredient, World world, BlockPos position, Object brewer)
    {
        if (input == null || input.getMaxStackSize() != 1 || input.stackSize != 1) return null;
        if (ingredient == null || ingredient.stackSize <= 0) return null;

        for (IBrewingRecipe recipe : recipes)
        {
            ItemStack output = recipe.getOutput(input, ingredient, world, position, brewer);
            if (output != null)
            {
                return output;
            }
        }
        
        return null;
    }

    /**
     * Returns true if the passed input, ingredient, location and brewing object have an output
     */
    public static boolean hasOuput(ItemStack input, ItemStack ingredient, World world, BlockPos position, Object brewer)
    {
        return getOutput(input, ingredient, world, position, brewer) != null;
    }

    /**
     * Used by the brewing stand to determine if its contents can be brewed.
     * Extra parameters exist to allow modders to create bigger brewing stands
     * without much hassle
     */
    public static boolean canBrew(ItemStack[] inputs, ItemStack ingredient, int[] inputIndexes, World world, BlockPos position, Object brewer)
    {
        if (ingredient == null || ingredient.stackSize <= 0) return false;

        for (int i : inputIndexes)
        {
            if (hasOuput(inputs[i], ingredient, world, position, brewer))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Used by the brewing stand to brew its inventory Extra parameters exist to
     * allow modders to create bigger brewing stands without much hassle
     */
    public static void brewPotions(ItemStack[] inputs, ItemStack ingredient, int[] inputIndexes, World world, BlockPos position, Object brewer)
    {
        for (int i : inputIndexes)
        {
            ItemStack output = getOutput(inputs[i], ingredient, world, position, brewer);
            if (output != null)
            {
                inputs[i] = output;
            }
        }
    }

    /**
     * Returns true if the passed ItemStack is a valid ingredient for any of the
     * recipes in the registry.
     */
    public static boolean isValidIngredient(ItemStack stack)
    {
        if (stack == null || stack.stackSize <= 0) return false;

        for (IBrewingRecipe recipe : recipes)
        {
            if (recipe.isIngredient(stack))
            {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Returns true if the passed ItemStack is a valid input for any of the
     * recipes in the registry.
     */
    public static boolean isValidInput(ItemStack stack)
    {
        if (stack == null || stack.getMaxStackSize() != 1 || stack.stackSize != 1) return false;

        for (IBrewingRecipe recipe : recipes)
        {
            if (recipe.isInput(stack))
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Removes all recipes extending VanillaBrewingRecipe from the recipe list and
     *  prevents further recipes of this type from being added
     */
    public static void disableVanillaBrewingRecipes()
    {
        disableVanillaRecipes = true;
    	Iterator<IBrewingRecipe> iterator = recipes.iterator();
        
        while(iterator.hasNext())
        {
            if(iterator.next() instanceof VanillaBrewingRecipe) iterator.remove();            
        }
    }
    
    /**
     * @return true if vanilla brewing recipes have been disabled, false otherwise
     */
    public static boolean vanillaBrewingDisabled()
    {
    	return disableVanillaRecipes;
    }

    /**
     * Returns an unmodifiable list containing all the recipes in the registry
     */
    public static List<IBrewingRecipe> getRecipes()
    {
        return Collections.unmodifiableList(recipes);
    }
}