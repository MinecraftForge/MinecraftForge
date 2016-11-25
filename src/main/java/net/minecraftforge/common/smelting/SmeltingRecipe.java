package net.minecraftforge.common.smelting;

import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Base interface for furnace recipes.
 *
 * @see SmeltingRecipeRegistry
 */
public interface SmeltingRecipe
{

    /**
     * Determine if the recipe matches the given input.
     *
     * @param input the input stack
     * @return true if the recipe matches
     */
    boolean matches(@Nonnull ItemStack input);

    /**
     * Get the output for the recipe.
     *
     * @param input the input stack
     * @return the output stack
     */
    @Nonnull
    ItemStack getOutput(@Nonnull ItemStack input);

    /**
     * The time in ticks it takes for this recipe to apply in a furnace.
     *
     * @param input the input stack
     * @return the cooking time in ticks
     */
    int getDuration(@Nonnull ItemStack input);

    /**
     * Provide a list of Items that are possible as a result of {@code getOutput}. This is needed for e.g. crafting stats.
     * @return a list of outputs
     */
    Collection<Item> getPossibleOutputs();

}
