package net.minecraftforge.ingredients.capability;

import net.minecraftforge.ingredients.IngredientStack;

import javax.annotation.Nullable;

/**
 * Simplified Read-only Information about the internals of an {@link IIngredientHandler}.
 * This is useful for displaying information, and as hints for interacting with it.
 * These properties are constant and do not depend on the ingredients contents (except the contents themselves, of course).
 *
 * The information here may not tell the full story of how the source actually works,
 * for real ingredient transactions you must use {@link IIngredientHandler} to simulate, check, and then interact.
 * None of the information in these properties is required to successfully interact using an {@link IIngredientHandler}.
 */
public interface IIngredientSourceProperties
{

    /**
     * @return A copy of the ingredient contents of this source. May be null;
     * To modify the contents use {@link IIngredientHandler}
     */
    @Nullable
    IngredientStack getContents();

    /**
     * @return the maximum volume of an ingredient this source can hold, in milliblocks
     * */
    int getCapacity();

    /**
     * Returns true if the source can be added to at any time, regardless of whether it is full.
     * This does not consider the contents of capacity of the source.
     *
     * This value is constant. If the source's behavior is more complicated, return true;
     * */
    boolean canAdd();

    /**
     * Returns true if the source can have its contents taken at any time, regardless of whether it is empty.
     * This does not consider the contents or capacaity of this source.
     *
     * This value is constant. If the source's behavior is more complicated, return true.
     * */
    boolean canRemove();

    /**
     * Returns true if the source can have a particular ingredient added to it.
     * Use as a filter for ingredients.
     *
     * Does not consider the current contents, nor the capacity of the source.
     * {@link IngredientStack} is used because the material state, refinement level, and tag data may be intrinsic
     * to the ingredient's properties.
     * */
    boolean canAddIngredientType(IngredientStack stack);

    /**
     * Returns true if the source can have a particular ingredient added to it.
     * Use as a filter for ingredients.
     *
     * Does not consider the current contents, nor the capacity of the source.
     * {@link IngredientStack} is used because the material state, refinement level, and tag data may be intrinsic
     * to the ingredient's properties.
     * */
    boolean canRemoveIngredientType(IngredientStack stack);
}
