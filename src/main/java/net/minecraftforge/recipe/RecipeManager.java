package net.minecraftforge.recipe;

import java.util.ArrayList;
import java.util.Hashtable;

import net.minecraft.item.ItemStack;

public class RecipeManager
{
    private static Hashtable<String, RecipeHandler> machineLookupTable = new Hashtable<String, RecipeHandler>();;
    private static RecipeHandler activeRecipeHandler;

    public static void registerRecipeMachine(IRecipeMachine machine)
    {
        if (machineLookupTable.containsKey(machine.getUniqueMachineName()))
        {
            System.out.format("[WARNING] Another machine with name %s has already been registered.\n Skipping this one.", machine.getUniqueMachineName());
            return;
        }
        activeRecipeHandler = new RecipeHandler(machine);
        machineLookupTable.put(machine.getUniqueMachineName(), activeRecipeHandler);
    }

    public static RecipeHandler getRecipeHandler(IRecipeMachine machine)
    {
        return machineLookupTable.get(machine.getUniqueMachineName());
    }

    public static void setRecipesForMachine(IRecipeMachine machine)
    {
        activeRecipeHandler = machineLookupTable.get(machine.getUniqueMachineName());
    }

    public static void addShapedRecipe(AbstractIngredient[] ingredientList, ItemStack result)
    {
        activeRecipeHandler.addShapedRecipe(ingredientList, result);
    }

    public static void addShapelessRecipe(AbstractIngredient[] ingredientList, ItemStack result)
    {
        activeRecipeHandler.addShapelessRecipe(ingredientList, result);
    }

    public static void addShapedRecipe(IRecipeMachine machine, AbstractIngredient[] ingredientList, ItemStack result)
    {
        activeRecipeHandler = machineLookupTable.get(machine.getUniqueMachineName());
        activeRecipeHandler.addShapedRecipe(ingredientList, result);
    }

    public static void addShapelessRecipe(IRecipeMachine machine, AbstractIngredient[] ingredientList, ItemStack result)
    {
        activeRecipeHandler = machineLookupTable.get(machine.getUniqueMachineName());
        activeRecipeHandler.addShapelessRecipe(ingredientList, result);
    }
}
