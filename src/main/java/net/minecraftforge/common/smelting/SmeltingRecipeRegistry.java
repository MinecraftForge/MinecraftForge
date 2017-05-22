package net.minecraftforge.common.smelting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.reflect.Reflection;

/**
 * Recipe for smelting recipes, should be used over the GameRegistry methods and vanilla's FurnaceRecipes class.
 */
public class SmeltingRecipeRegistry
{

    /**
     * Default time a recipe takes to cook.
     */
    public static final int DEFAULT_COOK_TIME = 200;

    private static final List<SmeltingRecipe> recipes = new ArrayList<SmeltingRecipe>();
    private static final List<SmeltingRecipe> recipesUn = Collections.unmodifiableList(recipes);

    static
    {
        // ensure vanilla recipe is the first
        Reflection.initialize(FurnaceRecipes.class);
    }

    /**
     * Get an unmodifiable list of all smelting recipes. The list will update in-place as new recipes are added.
     *
     * @return the list of recipes
     */
    public static List<SmeltingRecipe> getRecipes()
    {
        return recipesUn;
    }

    /**
     * Get the first recipe matching the given input stack or null if no recipe matches.
     *
     * @param input the input stack
     * @return a matching recipe
     */
    public static SmeltingRecipe getMatchingRecipe(ItemStack input)
    {
        for (SmeltingRecipe recipe : recipes)
        {
            if (recipe.matches(input))
            {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Add the given recipe.
     *
     * @param recipe the recipe
     */
    public static void addRecipe(SmeltingRecipe recipe)
    {
        recipes.add(checkNotNull(recipe));
    }

    /**
     * Add a simple recipe that smelts the stacks matching the given input stack into the output stack
     * and provides the given amount of experience.
     * Stack matching is done according to {@link OreDictionary#itemMatches(ItemStack, ItemStack, boolean)}
     * with {@code strict} set to false.
     *
     * @param input  the input stack
     * @param output the output stack
     * @param xp     the amount of experience
     */
    public static void addSimpleRecipe(ItemStack input, ItemStack output)
    {
        addSimpleRecipe(input, output, DEFAULT_COOK_TIME);
    }

    /**
     * Add a simple recipe that smelts the stacks matching the given input stack into the output stack
     * in the given duration and provides the given amount of experience.
     * Stack matching is done according to {@link OreDictionary#itemMatches(ItemStack, ItemStack, boolean)}
     * with {@code strict} set to false.
     *
     * @param input    the input stack
     * @param output   the output stack
     * @param duration the duration for this recipe in ticks
     */
    public static void addSimpleRecipe(ItemStack input, ItemStack output, int duration)
    {
        addRecipe(new SimpleSmeltingRecipe(input, output, duration));
    }

    /**
     * Add a recipe that smelts stacks matching the given OreDictionary name into the output stack
     * and provides the given amount of experience.
     *
     * @param input  the OreDictionary name
     * @param output the output stack
     * @param xp     the amount of experience
     */
    public static void addOreRecipe(String input, ItemStack output)
    {
        addOreRecipe(input, output, DEFAULT_COOK_TIME);
    }

    /**
     * Add a recipe that smelts stacks matching the given OreDictionary name into the output stack
     * in the given duration and provides the given amount of experience.
     *
     * @param input    the OreDictionary name
     * @param output   the output stack
     * @param duration the duration for this recipe in ticks
     */
    public static void addOreRecipe(String input, ItemStack output, int duration)
    {
        addRecipe(new OreSmeltingRecipe(input, output, duration));
    }

    /**
     * Get the output stack for the given input. The result is always a defensive copy.
     *
     * @param input the input stack
     * @return the output stack
     */
    public static ItemStack getOutput(ItemStack input)
    {
        for (SmeltingRecipe recipe : recipes)
        {
            ItemStack out = recipe.getOutput(input);
            if (out != null)
            {
                return out;
            }
        }
        return null;
    }

    /**
     * Get the duration the given input stack takes to smelt.
     *
     * @param input the input stack
     * @return the duration in ticks
     */
    public static int getDuration(ItemStack input)
    {
        for (SmeltingRecipe recipe : recipes)
        {
            int time = recipe.getDuration(input);
            if (time >= 0)
            {
                return time;
            }
        }
        return 0;
    }

    @Deprecated // internal API, do not use!
    public static void addVanillaRecipe(Map<ItemStack, ItemStack> smeltingList)
    {
        recipes.add(new VanillaSmeltingRecipe(smeltingList));
    }
}
