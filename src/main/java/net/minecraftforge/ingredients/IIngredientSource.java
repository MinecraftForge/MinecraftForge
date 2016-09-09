package net.minecraftforge.ingredients;

import javax.annotation.Nullable;

/**
 * The source is the unit for interaction with ingredient inventories.
 *
 * */
public interface IIngredientSource
{
    /**
     * @return IngredientStack representing the Ingredient available from the source, null if the source
     * is depleted.
     * */
    @Nullable
    IngredientStack getIngredient();

    /**
     * @return Current amount of the ingredient available.
     * */
    int getIngredientVolume();

    /**
     * @return Capacity of this source.
     * */
    int getCapacity();

    /**
     * Returns a wrapper object {@link IngredientSourceInfo } containing the capacity and the
     * IngredientStack it holds.
     *
     * Should prevent unwanted manipulation of the IIngredientSource. See {@link IngredientSource}.
     *
     * @retunr State information for the IIngredientSource.
     * */
    IngredientSourceInfo getInfo();

    /**
     * @param resource
     *          IngredientStack attempting to be added to the source.
     * @param doAdd
     *          If false, the adding will be simulated.
     * @return Amount of the ingredient that will be accepted.
     * */
    int add(IngredientStack resource, boolean doAdd);

    /**
     * @param maxRemoved
     *          Maximum amount of the ingredient to be removed from the source.
     * @param doRemove
     *          If false, the removal will be simulated
     * @return Amount of the ingredient that was removed from the source
     * */
    @Nullable
    IngredientStack remove(int maxRemoved, boolean doRemove);

}
