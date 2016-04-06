package net.minecraftforge.common.brewing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class BrewingRecipeRegistry
{

    private static List<IBrewingRecipe> recipes = new ArrayList<IBrewingRecipe>();
    private static List<FuelEntry> fuels = new CopyOnWriteArrayList<FuelEntry>();

    static
    {
        addRecipe(new VanillaBrewingRecipe());
        addFuel(new ItemStack(Items.blaze_powder), 20);
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
     */
    public static boolean addRecipe(IBrewingRecipe recipe)
    {
        return recipes.add(recipe);
    }

    /**
     * Returns the output ItemStack obtained by brewing the passed input and
     * ingredient. Null if no matches are found.
     */
    public static ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        if (input == null || input.getMaxStackSize() != 1 || input.stackSize != 1) return null;
        if (ingredient == null || ingredient.stackSize <= 0) return null;

        for (IBrewingRecipe recipe : recipes)
        {
            ItemStack output = recipe.getOutput(input, ingredient);
            if (output != null)
            {
                return output;
            }
        }
        return null;
    }

    /**
     * Returns true if the passed input and ingredient have an output
     */
    public static boolean hasOutput(ItemStack input, ItemStack ingredient)
    {
        return getOutput(input, ingredient) != null;
    }

    /**
     * Used by the brewing stand to determine if its contents can be brewed.
     * Extra parameters exist to allow modders to create bigger brewing stands
     * without much hassle
     */
    public static boolean canBrew(ItemStack[] inputs, ItemStack ingredient, int[] inputIndexes)
    {
        if (ingredient == null || ingredient.stackSize <= 0) return false;

        for (int i : inputIndexes)
        {
            if (hasOutput(inputs[i], ingredient))
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
    public static void brewPotions(ItemStack[] inputs, ItemStack ingredient, int[] inputIndexes)
    {
        for (int i : inputIndexes)
        {
            ItemStack output = getOutput(inputs[i], ingredient);
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
     * Returns an unmodifiable list containing all the recipes in the registry
     */
    public static List<IBrewingRecipe> getRecipes()
    {
        return Collections.unmodifiableList(recipes);
    }

    /**
     * Returns an unmodifiable list of all fuels in the registry.
     */
    public static List<FuelEntry> getFuels()
    {
        return Collections.unmodifiableList(fuels);
    }

    /**
     * Adds a new fuel for brewing stands. Fuel value MUST BE positive.
     */
    public static void addFuel(ItemStack item, int fuelValue)
    {
        for (FuelEntry e : fuels) {
            if (ItemStack.areItemStacksEqual(e.getItem(), item))
            {
                if (e.getFuelValue() < fuelValue)
                {
                    e.power = fuelValue;
                }
                return;
            }
        }
        fuels.add(new FuelEntry(item, fuelValue));
    }

    /**
     * Overrides the fuel value of a fuel. Fuel value MUST BE positive.
     *
     * @return True if the value is overridden
     */
    public static boolean setFuelValue(ItemStack fuel, int value)
    {
        for (FuelEntry e : fuels) {
            if (ItemStack.areItemStacksEqual(e.getItem(), fuel))
            {
                e.power = value;
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a fuel.
     *
     * @return True if the fuel is removed
     */
    public static boolean removeFuel(ItemStack fuel)
    {
        Iterator<FuelEntry> itr = fuels.iterator();
        while (itr.hasNext())
        {
            FuelEntry fuelEntry = itr.next();
            if (ItemStack.areItemStacksEqual(fuelEntry.getItem(), fuel))
            {
                itr.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether an item is a fuel of brewing stands.
     */
    public static boolean isFuel(@Nullable ItemStack item)
    {
        if (item == null)
        {
            return false;
        }
        for (FuelEntry e : fuels)
        {
            if (ItemStack.areItemStacksEqual(e.getItem(), item))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the fuel value of an item.
     */
    public static int getFuelValue(@Nullable ItemStack fuel)
    {
        if (fuel == null)
        {
            return 0;
        }
        for (FuelEntry e : fuels)
        {
            if (ItemStack.areItemStacksEqual(e.getItem(), fuel))
            {
                return e.getFuelValue();
            }
        }
        return 0;
    }

    public static final class FuelEntry
    {
        private ItemStack item;
        private int power;

        private FuelEntry(ItemStack itemStack, int fuelValue)
        {
            item = itemStack;
            power = fuelValue;
        }

        public ItemStack getItem()
        {
            return item;
        }

        public int getFuelValue()
        {
            return power;
        }
    }
}
