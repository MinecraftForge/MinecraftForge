package net.minecraftforge.ingredients.capability;

import net.minecraftforge.ingredients.IngredientSourceInfo;
import net.minecraftforge.ingredients.IngredientStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Implement this interface as a capability which should handle ingredients, generally storing
 * the IngredientStacks and providing them on demand
 * */
public interface IIngredientHandler
{

    /**
     * Returns an array of objects which represent the internal sources.
     * These objects cannot be sued to manipulate the internal sources.
     *
     * @return Properties for the relevant internal sources.
     * */
    IIngredientSourceProperties[] getContainerProperties();

    /**
     * Adds an ingredient into the internal source. Distribution is up to the IIngredientHandler.
     *
     * @param resource  IngredientStack representing the Ingredient and the maximum amount to be added.
     * @param doAdd     If false, adding will only be simulated.
     * @return The amount of resource that was (or would have been if simulated) added.
     */
    int add(IngredientStack resource, boolean doAdd);

    /**
     * Removes a given ingredient from the internal source. Distribution is up to the IIngredientHandler.
     *
     * @param resource  IngredientStack representing the Ingredient and the maximum amount to be removed.
     * @param doRemove     If false, adding will only be simulated.
     * @return IngredientStack representing the Ingredient and the amount that was (or would have been if
     * simulated) removed.
     */
    @Nullable
    IngredientStack remove(IngredientStack resource, boolean doRemove);

    /**
     * Removes a given ingredient from the internal source. Distribution is up to the IIngredientHandler.
     * <p/>
     * This method is not Ingredient-Sensitive.
     *
     * @param maxRemoved   The maximum amount of ingredient(s) to be removed;
     * @param doRemove     If false, adding will only be simulated.
     * @return IngredientStack representing the Ingredient and the amount that was (or would have been if
     * simulated) removed.
     */
    @Nullable
    IngredientStack remove(int maxRemoved, boolean doRemove);

    /**
     * Returns information on the 'main' ingredient to be gleened from the source.
     * This is the resource that is typically the most valuable.
     * <p/>
     * As an example, ore blocks provide the ore itself as their primary source.
     * <p/>
     * If no IngredientStack is available for the SourceInfo, then provide info on
     * the the container.
     * **/
    @Nonnull
    IngredientSourceProperties getPrimarySourceIngredient();
}
